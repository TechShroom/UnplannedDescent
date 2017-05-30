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

import com.flowpowered.math.vector.Vector4i;
import com.techshroom.unplanned.blitter.textures.TextureData;

class SimpleColorLoader implements ColorTextureLoader {

    @Override
    public TextureData load(ColorTextureSpec source) {
        int color = colorFrom4i(source.getColor());
        int[][] data = new int[source.getSize().getX()][source.getSize().getY()];
        for (int i = 0; i < data.length; i++) {
            int[] js = data[i];
            for (int j = 0; j < js.length; j++) {
                js[j] = color;
            }
        }
        return TextureData.wrap(data);
    }

    private int colorFrom4i(Vector4i color) {
        int r = color.getX() & 0xFF;
        int g = color.getY() & 0xFF;
        int b = color.getZ() & 0xFF;
        int a = color.getW() & 0xFF;
        return (r << 0) | (g << 8) | (b << 16) | (a << 24);
    }

}
