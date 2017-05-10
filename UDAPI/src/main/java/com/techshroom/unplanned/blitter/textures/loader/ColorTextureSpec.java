/*
 * This file is part of UnplannedDescent, licensed under the MIT License (MIT).
 *
 * Copyright (c) TechShroom Studios <https://techshoom.com>
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

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import com.flowpowered.math.vector.Vector4i;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ColorTextureSpec {

    public static ColorTextureSpec create(String color, int w, int h) {
        int r;
        int g;
        int b;
        int a = 255;
        switch (color.length()) {
            case 4:
                // RGBA, RGB handled below
                a = Integer.parseInt(color.substring(3, 4), 16) * 0x11;
            case 3:
                // RGB
                r = Integer.parseInt(color.substring(0, 1), 16) * 0x11;
                g = Integer.parseInt(color.substring(1, 2), 16) * 0x11;
                b = Integer.parseInt(color.substring(2, 3), 16) * 0x11;
                break;
            case 8:
                // RRGGBBAA, RRGGBB handled below
                a = Integer.parseInt(color.substring(6, 8), 16);
            case 6:
                // RRGGBB
                r = Integer.parseInt(color.substring(0, 2), 16);
                g = Integer.parseInt(color.substring(2, 4), 16);
                b = Integer.parseInt(color.substring(4, 6), 16);
                break;
            default:
                throw new IllegalArgumentException("Not a hex color: " + color);
        }
        return create(new Vector4i(r, g, b, a), new Vector2i(w, h));
    }

    public static ColorTextureSpec create(Vector3i color, int w, int h) {
        return create(color, new Vector2i(w, h));
    }

    public static ColorTextureSpec create(Vector3i color, Vector2i size) {
        return create(new Vector4i(color, 255), size);
    }

    public static ColorTextureSpec create(Vector4i color, int w, int h) {
        return create(color, new Vector2i(w, h));
    }

    public static ColorTextureSpec create(Vector4i color, Vector2i size) {
        return new AutoValue_ColorTextureSpec(color, size);
    }

    ColorTextureSpec() {
    }

    public abstract Vector4i getColor();

    public abstract Vector2i getSize();

}
