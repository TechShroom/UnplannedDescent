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
package com.techshroom.unplanned.blitter.textures;

import static com.google.common.base.Preconditions.checkArgument;

import java.nio.IntBuffer;
import java.util.Arrays;

import org.lwjgl.BufferUtils;

/**
 * Container for texture data. 2D array of RGBA integers, packaged like so:
 * {@code Ax8-Bx8-Gx8-Rx8}.
 */
public final class TextureData implements UVMapper.BySize {

    /**
     * Helper for creating the correct integer.
     * 
     * @param r
     *            - red component
     * @param g
     *            - green component
     * @param b
     *            - blue component
     * @param a
     *            - alpha component
     * @return the RGBA integer as described in {@link TextureData}
     */
    public static int makeRGBAInt(int r, int g, int b, int a) {
        return ((a & 0xFF) << 24)
                | ((b & 0xFF) << 16)
                | ((g & 0xFF) << 8)
                | ((r & 0xFF));
    }

    public static TextureData wrap(int[][] data) {
        checkArgument(data.length > 0 && data[0].length > 0, "must have at least 1 pixel!");
        return new TextureData(new TextureData(data).getData());
    }

    private final int[][] data;

    private TextureData(int[][] data) {
        this.data = data;
    }

    public IntBuffer getDataAsSingleArray() {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length * data[0].length);
        for (int i = 0; i < data.length; i++) {
            int[] col = data[i];
            for (int j = 0; j < col.length; j++) {
                buffer.put(i + j * data.length, col[j]);
            }
        }
        buffer.flip();
        return buffer;
    }

    @Override
    public int getWidth() {
        return data.length;
    }

    @Override
    public int getHeight() {
        return data[0].length;
    }

    public int[][] getData() {
        int[][] clone = data.clone();
        for (int i = 0; i < clone.length; i++) {
            clone[i] = clone[i].clone();
        }
        return clone;
    }

    public int getRGBA(int x, int y) {
        return data[x][y];
    }

    public byte getRed(int x, int y) {
        return (byte) ((getRGBA(x, y) /* 0 */) & 0xFF);
    }

    public byte getGreen(int x, int y) {
        return (byte) ((getRGBA(x, y) >> 0x0F) & 0xFF);
    }

    public byte getBlue(int x, int y) {
        return (byte) ((getRGBA(x, y) >> 0x10) & 0xFF);
    }

    public byte getAlpha(int x, int y) {
        return (byte) ((getRGBA(x, y) >> 0x1F) & 0xFF);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.deepHashCode(data);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TextureData other = (TextureData) obj;
        if (!Arrays.deepEquals(data, other.data))
            return false;
        return true;
    }

}
