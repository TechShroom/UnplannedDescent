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

import java.nio.ByteBuffer;

public enum TextureFormat {
    RGB(3){
        @Override
        public void putRGBA(int rgba, ByteBuffer buffer) {
            buffer.put((byte) (rgba & 0xFF));
            buffer.put((byte) ((rgba >> 8) & 0xFF));
            buffer.put((byte) ((rgba >> 16) & 0xFF));
        }

        @Override
        public int getRGBA(ByteBuffer buffer) {
            int result = 0;
            result |= buffer.get() & 0xFF;
            result |= (buffer.get() << 8) & 0xFF;
            result |= (buffer.get() << 16) & 0xFF;
            return result;
        }
    },
    RGBA(4) {
        @Override
        public void putRGBA(int rgba, ByteBuffer buffer) {
            buffer.put((byte) (rgba & 0xFF));
            buffer.put((byte) ((rgba >> 8) & 0xFF));
            buffer.put((byte) ((rgba >> 16) & 0xFF));
            buffer.put((byte) ((rgba >> 24) & 0xFF));
        }

        @Override
        public int getRGBA(ByteBuffer buffer) {
            int result = 0;
            result |= buffer.get() & 0xFF;
            result |= (buffer.get() << 8) & 0xFF;
            result |= (buffer.get() << 16) & 0xFF;
            result |= (buffer.get() << 24) & 0xFF;
            return result;
        }
    },
    BGRA(4) {
        @Override
        public void putRGBA(int rgba, ByteBuffer buffer) {
            buffer.put((byte) ((rgba >> 16) & 0xFF));
            buffer.put((byte) ((rgba >> 8) & 0xFF));
            buffer.put((byte) (rgba & 0xFF));
            buffer.put((byte) ((rgba >> 24) & 0xFF));
        }

        @Override
        public int getRGBA(ByteBuffer buffer) {
            int result = 0;
            result |= (buffer.get() << 16) & 0xFF;
            result |= (buffer.get() << 8) & 0xFF;
            result |= buffer.get() & 0xFF;
            result |= (buffer.get() << 24) & 0xFF;
            return result;
        }
    };

    public final int channels;

    TextureFormat(int channels) {
        this.channels = channels;
    }

    /**
     * Attempt to store an RGBA integer (alpha in highest byte, red in lowest) in the given buffer.
     *
     * May not store all components, or could translate them to another color model.
     */
    public abstract void putRGBA(int rgba, ByteBuffer buffer);

    /**
     * Attempt to get an RGBA integer from the given buffer.
     *
     * If a component doesn't exist, it will default to 255.
     * This handles the common case of RGB -> RGBA.
     */
    public abstract int getRGBA(ByteBuffer buffer);
}
