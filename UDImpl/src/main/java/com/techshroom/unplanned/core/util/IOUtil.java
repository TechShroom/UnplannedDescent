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

package com.techshroom.unplanned.core.util;

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;

import org.lwjgl.system.MemoryUtil;

/* Also copyright:
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
public final class IOUtil {

    private IOUtil() {
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = MemoryUtil.memRealloc(buffer, newCapacity);
        if (newBuffer == null) {
            throw new OutOfMemoryError("Failed to reallocate buffer: newCapacity=" + newCapacity);
        }
        return newBuffer;
    }

    private static ByteBuffer createByteBuffer(int capacity) {
        ByteBuffer newBuffer = MemoryUtil.memAlloc(capacity);
        if (newBuffer == null) {
            throw new OutOfMemoryError("Failed to allocate buffer: capacity=" + capacity);
        }
        return newBuffer;
    }

    /**
     * Reads the specified resource and returns the raw data as a ByteBuffer.
     * THIS BUFFER IS ALLOCATTED USING {@link MemoryUtil} AND MUST BE EXPLICITLY
     * FREED!!!
     *
     * @param resource
     *            the resource to read
     * @param bufferSize
     *            the initial buffer size
     *
     * @return the resource data
     *
     * @throws IOException
     *             if an IO error occurs
     */
    public static ByteBuffer ioResourceToByteBuffer(InputStream resource, int bufferSize) throws IOException {
        ByteBuffer buffer = null;

        try (ReadableByteChannel rbc = Channels.newChannel(resource)) {
            int size = bufferSize;
            if (rbc instanceof SeekableByteChannel) {
                long seekSize = ((SeekableByteChannel) rbc).size();
                checkState(seekSize <= Integer.MAX_VALUE, "resource is too large to load");
                size = (int) seekSize;
            }
            buffer = createByteBuffer(size);

            while (true) {
                int bytes = rbc.read(buffer);
                if (bytes == -1) {
                    break;
                }
                if (buffer.remaining() == 0) {
                    buffer = resizeBuffer(buffer, buffer.capacity() * 2);
                }
            }
        } catch (Throwable t) {
            MemoryUtil.memFree(buffer);
            throw t;
        }

        buffer.flip();
        return buffer;
    }

}