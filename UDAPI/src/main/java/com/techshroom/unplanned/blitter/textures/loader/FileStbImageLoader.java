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
package com.techshroom.unplanned.blitter.textures.loader;

import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;

import org.lwjgl.system.MemoryStack;

import com.techshroom.unplanned.blitter.textures.TextureData;

final class FileStbImageLoader implements FileTextureLoader {

    @Override
    public TextureData load(Path source) throws IOException {
        int[][] rgba;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer xBuf = stack.mallocInt(1);
            IntBuffer yBuf = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);
            ByteBuffer data = stbi_load(source.toAbsolutePath().toString(), xBuf, yBuf, channels, 4);

            if (data == null) {
                String why = stbi_failure_reason();
                throw new IllegalArgumentException("Image " + source + " could not be loaded: " + why);
            }

            rgba = new int[xBuf.get(0)][yBuf.get(0)];
            for (int x = 0; x < rgba.length; x++) {
                int[] row = rgba[x];
                for (int y = 0; y < row.length; y++) {
                    int r = data.get() & 0xFF;
                    int g = data.get() & 0xFF;
                    int b = data.get() & 0xFF;
                    int a = data.get() & 0xFF;
                    row[y] = TextureData.makeRGBAInt(r, g, b, a);
                }
            }
        }
        return TextureData.wrap(rgba);
    }

}
