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
package com.techshroom.unplanned.geometry;

import com.google.auto.value.AutoValue;

// css order, TL-TR-BR-BL
@AutoValue
public abstract class CornerVector4i {

    public static CornerVector4i all(int value) {
        return of(value, value, value, value);
    }

    public static CornerVector4i of(int x, int y, int z, int w) {
        if (x == y && y == z && z == w) {
            switch (w) {
                case 0:
                    if (ZERO == null) {
                        break;
                    }
                    return ZERO;
                case 1:
                    if (ONE == null) {
                        break;
                    }
                    return ONE;
            }
        }
        return new AutoValue_CornerVector4i(x, y, z, w);
    }

    public static final CornerVector4i ZERO = all(0);
    public static final CornerVector4i UNIT_TOP = of(1, 0, 0, 0);
    public static final CornerVector4i UNIT_RIGHT = of(0, 1, 0, 0);
    public static final CornerVector4i UNIT_BOTTOM = of(0, 0, 1, 0);
    public static final CornerVector4i UNIT_LEFT = of(0, 0, 0, 1);
    public static final CornerVector4i ONE = all(1);

    public boolean allEqual() {
        return getX() == getY() && getY() == getZ() && getZ() == getW();
    }

    public abstract int getX();

    public abstract int getY();

    public abstract int getZ();

    public abstract int getW();

    public int getTopLeft() {
        return getX();
    }

    public int getTopRight() {
        return getY();
    }

    public int getBottomRight() {
        return getZ();
    }

    public int getBottomLeft() {
        return getW();
    }

    @Override
    public final String toString() {
        return String.format("[topLeft=%s,topRight=%s,bottomRight=%s,bottomLeft=%s]", getTopLeft(), getTopRight(), getBottomRight(), getBottomLeft());
    }

}
