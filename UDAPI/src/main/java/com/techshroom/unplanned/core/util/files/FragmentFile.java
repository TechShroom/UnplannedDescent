/*
 * This file is part of UnplannedDescent, licensed under the MIT License (MIT).
 *
 * Copyright (c) TechShroom Studios <https://techshroom.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.techshroom.unplanned.core.util.files;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFileChooser;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;
import com.google.common.primitives.Ints;

/**
 * The non-shitty solution to storing large files in a repository! SPLIT EM' UP!
 */
public class FragmentFile {

    private static final String FRAG_DIR = ".frag";
    private static final String SIZE_FILE = ".size";
    private static final String CHUNK_FILE = "%010x.chunk";
    // we can store about 20MB per file efficiently
    private static final int CHUNK_SIZE = 20 * 1024 * 1024;

    public static InputStream getFragmentFile(String classpathLocation) throws IOException {
        long size = readFragSize(classpathLocation);
        return ByteSource.concat(getChunks(classpathLocation, size)).openBufferedStream();
    }

    private static Iterator<ByteSource> getChunks(String cpLoc, long size) {
        int chunks = getChunkCount(size);
        List<ByteSource> sources = new ArrayList<>(chunks);
        for (int i = 0; i < chunks; i++) {
            final int chunk = i;
            sources.add(new ByteSource() {

                @Override
                public InputStream openStream() throws IOException {
                    return FragmentFile.openStream(cpLoc + FRAG_DIR + "/" + getChunkFileName(chunk));
                }
            });
        }
        return sources.iterator();
    }

    private static long readFragSize(String classpathLocation) throws IOException {
        try (InputStream stream = openStream(classpathLocation + FRAG_DIR + "/" + SIZE_FILE)) {
            return new DataInputStream(stream).readLong();
        }
    }

    public static void main(String[] args) throws Exception {
        JFileChooser chooser = new JFileChooser(".");
        chooser.setDialogTitle("Fragment File Picker");
        int success = chooser.showOpenDialog(null);
        if (success == JFileChooser.APPROVE_OPTION) {
            makeFragmentFile(chooser.getSelectedFile().toPath());
        }
    }

    public static void makeFragmentFile(Path path) throws IOException {
        System.err.println("Fragmenting `" + path + "`...");
        long size = Files.size(path);
        int chunks = getChunkCount(size);
        System.err.println("Size: " + size + " byte(s), " + chunks + " chunk(s).");
        Path parent = path.resolveSibling(path.getFileName().toString() + FRAG_DIR);
        Files.createDirectories(parent);
        System.err.println("Writing size...");
        try (OutputStream stream = Files.newOutputStream(parent.resolve(SIZE_FILE))) {
            new DataOutputStream(stream).writeLong(size);
        }
        System.err.println("Writing chunks...");
        try (InputStream in = new BufferedInputStream(Files.newInputStream(path))) {
            for (int i = 0; i < chunks; i++) {
                int chunkSize = Ints.checkedCast(Math.min(size, CHUNK_SIZE));
                try (OutputStream stream = Files.newOutputStream(parent.resolve(getChunkFileName(i)))) {
                    InputStream limitedIn = ByteStreams.limit(in, chunkSize);
                    ByteStreams.copy(limitedIn, stream);
                }
                size -= chunkSize;
            }
        }
        System.err.println("Finished!");
    }

    private static String getChunkFileName(int chunk) {
        return String.format(CHUNK_FILE, chunk);
    }

    private static int getChunkCount(long size) {
        return Ints.checkedCast(((size + CHUNK_SIZE - 1) / CHUNK_SIZE));
    }

    private static InputStream openStream(String cpLoc) throws IOException {
        URL loc = Resources.getResource(cpLoc);
        return Resources.asByteSource(loc).openBufferedStream();
    }

}
