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

package com.techshroom.unplanned.geometry;

import com.flowpowered.math.vector.Vector2i;
import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;

// css order, top-right-bottom-left
@AutoValue
public abstract class SidedVector4i {

    public static SidedVector4i all(int value) {
        return of(value, value, value, value);
    }

    public static SidedVector4i of(int x, int y, int z, int w) {
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
        return new AutoValue_SidedVector4i(x, y, z, w);
    }

    public static final SidedVector4i ZERO = all(0);
    public static final SidedVector4i UNIT_TOP = of(1, 0, 0, 0);
    public static final SidedVector4i UNIT_RIGHT = of(0, 1, 0, 0);
    public static final SidedVector4i UNIT_BOTTOM = of(0, 0, 1, 0);
    public static final SidedVector4i UNIT_LEFT = of(0, 0, 0, 1);
    public static final SidedVector4i ONE = all(1);

    public boolean allEqual() {
        return getX() == getY() && getY() == getZ() && getZ() == getW();
    }

    public abstract int getX();

    public abstract int getY();

    public abstract int getZ();

    public abstract int getW();

    public int getTop() {
        return getX();
    }

    public int getRight() {
        return getY();
    }

    public int getBottom() {
        return getZ();
    }

    public int getLeft() {
        return getW();
    }

    @Memoized
    public Vector2i getTopLeft() {
        return new Vector2i(getLeft(), getTop());
    }

    @Memoized
    public Vector2i getTopRight() {
        return new Vector2i(getRight(), getTop());
    }

    @Memoized
    public Vector2i getBottomRight() {
        return new Vector2i(getRight(), getBottom());
    }

    @Memoized
    public Vector2i getBottomLeft() {
        return new Vector2i(getLeft(), getBottom());
    }

    @Memoized
    public Vector2i getAsWidthHeight() {
        return new Vector2i(getLeft() + getRight(), getTop() + getBottom());
    }

    public final SidedVector4i add(SidedVector4i b) {
        return of(getX() + b.getX(), getY() + b.getY(), getZ() + b.getZ(), getW() + b.getW());
    }

    @Override
    public final String toString() {
        return String.format("[top=%s,right=%s,bottom=%s,left=%s]", getTop(), getRight(), getBottom(), getLeft());
    }

}
