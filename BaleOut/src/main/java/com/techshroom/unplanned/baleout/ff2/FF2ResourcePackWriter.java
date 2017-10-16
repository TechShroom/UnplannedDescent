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
package com.techshroom.unplanned.baleout.ff2;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.ImmutableList.toImmutableList;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteStreams;
import com.techshroom.unplanned.core.util.IOFunction;
import com.techshroom.unplanned.rp.RId;
import com.techshroom.unplanned.rp.ff2.FF2Index;
import com.techshroom.unplanned.rp.ff2.FF2Index.Value;

/**
 * Writer for FF2 resource packs.
 */
public class FF2ResourcePackWriter {

    private final Path folderBase;

    public FF2ResourcePackWriter(Path folderBase) {
        this.folderBase = folderBase;
    }

    public void write(FF2Index index, IOFunction<RId, InputStream> resStream) throws IOException {
        writeIndex(index);

        writeResources(index, resStream);
    }

    private void writeIndex(FF2Index index) throws IOException {
        ImmutableMap<RId, FF2Index.Value> indexMap = index.getDataLookup();
        Path indexFile = folderBase.resolve("index");
        try (DataOutputStream data = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(indexFile)))) {
            data.writeInt(indexMap.size());
            for (Entry<RId, FF2Index.Value> e : indexMap.entrySet()) {
                data.writeUTF(e.getKey().toString());
                data.writeByte(e.getValue().getIndex());
                data.writeLong(e.getValue().getOffset());
            }
        }
    }

    private void writeResources(FF2Index index, IOFunction<RId, InputStream> resStream) throws IOException {
        int currentIndex = -1;
        long checkOffset = 0;
        OutputStream openStream = null;
        try {
            for (Entry<RId, FF2Index.Value> e : sortByFileIndex(index.getDataLookup().entrySet())) {
                if (e.getValue().getIndex() != currentIndex) {
                    if (openStream != null) {
                        openStream.close();
                    }

                    currentIndex = e.getValue().getIndex();
                    Path indexFile = folderBase.resolve(String.valueOf(currentIndex));
                    openStream = new BufferedOutputStream(Files.newOutputStream(indexFile));
                    checkOffset = 0;
                }

                checkState(checkOffset == e.getValue().getOffset(), "offset failure for resource %s (info: %s)", e.getKey(), e.getValue());

                checkOffset += ByteStreams.copy(resStream.apply(e.getKey()), openStream);
            }
        } finally {
            if (openStream != null) {
                openStream.close();
            }
        }
    }

    private Iterable<Entry<RId, Value>> sortByFileIndex(ImmutableSet<Entry<RId, Value>> entrySet) {
        return entrySet.stream()
                .sorted(Comparator.comparingInt(e -> e.getValue().getIndex()))
                .collect(toImmutableList());
    }

}
