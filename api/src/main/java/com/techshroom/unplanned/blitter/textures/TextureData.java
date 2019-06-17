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

package com.techshroom.unplanned.blitter.textures;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

/**
 * Container for texture data.
 */
public final class TextureData implements UVMapper.BySize {

    public static TextureData wrap(int width, int height, ByteBuffer data, TextureFormat format) {
        ByteBuffer buffer = BufferUtils.createByteBuffer(data.remaining()).put(data);
        buffer.flip();
        return new TextureData(width, height, buffer, format);
    }

    private final int width;
    private final int height;
    private final ByteBuffer data;
    private final TextureFormat format;

    private TextureData(int width, int height, ByteBuffer data, TextureFormat format) {
        this.width = width;
        this.height = height;
        this.data = data;
        this.format = format;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public ByteBuffer getData() {
        return data;
    }

    public TextureFormat getFormat() {
        return format;
    }

    @Override
    public String toString() {
        return String.format("%s[size=%s,width=%s,height=%s,format=%s]",
            getClass().getName(), data.remaining(), width, height, format);
    }
}
