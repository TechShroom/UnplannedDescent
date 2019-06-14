/*
 * This file is part of unplanned-descent, licensed under the MIT License (MIT).
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
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Some useful {@link FileRetentionPolicy} factories.
 */
public class FileRetentionPolicies {

    public static FileRetentionPolicy noRetention() {
        return new NoRetentionPolicy();
    }

    private static final class NoRetentionPolicy implements FileRetentionPolicy {

        @Override
        public InputStream openStream(Path file) throws IOException {
            return new BufferedInputStream(Files.newInputStream(file));
        }

    }

    /**
     * Policy that keeps one file mmap'd at a time.
     */
    public static FileRetentionPolicy singleMemoryMappedFile() {
        return new SingleMMapPolicy();
    }

    private static final class SingleMMapPolicy implements FileRetentionPolicy {

        private Path mmapped;
        private MappedByteBuffer mmapData;

        @Override
        public InputStream openStream(Path file) throws IOException {
            if (!file.toAbsolutePath().equals(mmapped)) {
                mapFile(file);
            }
            return new ByteBufStream(mmapData);
        }

        private void mapFile(Path file) throws IOException {
            try (FileChannel fc = FileChannel.open(file)) {
                mmapData = fc.map(MapMode.READ_ONLY, 0, fc.size());
            }
            mmapped = file.toAbsolutePath();
        }

        private static final class ByteBufStream extends InputStream {

            private final ByteBuffer mmapView;

            private ByteBufStream(ByteBuffer original) {
                mmapView = original.asReadOnlyBuffer();
            }

            @Override
            public int read() throws IOException {
                if (!mmapView.hasRemaining()) {
                    return -1;
                }
                return mmapView.get();
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                int realLen = Math.min(len, mmapView.remaining());
                mmapView.get(b, off, realLen);
                return realLen;
            }

            @Override
            public long skip(long n) throws IOException {
                long actualSkip = Math.min(n, mmapView.remaining());
                mmapView.position(mmapView.position() + (int) actualSkip);
                return actualSkip;
            }

            @Override
            public int available() throws IOException {
                return mmapView.remaining();
            }

            @Override
            public synchronized void mark(int readlimit) {
                mmapView.mark();
            }

            @Override
            public synchronized void reset() throws IOException {
                mmapView.reset();
            }

            @Override
            public boolean markSupported() {
                return true;
            }

        }

    }

    private FileRetentionPolicies() {
    }

}
