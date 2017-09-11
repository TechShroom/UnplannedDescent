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

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import com.flowpowered.math.vector.Vector4i;
import com.flowpowered.math.vector.VectorNi;

// css order, top-right-bottom-left
public class SidedVector4i extends Vector4i {

    private static final long serialVersionUID = -5860154702722742447L;

    public static final SidedVector4i ZERO = new SidedVector4i(0, 0, 0, 0);
    public static final SidedVector4i UNIT_TOP = new SidedVector4i(1, 0, 0, 0);
    public static final SidedVector4i UNIT_RIGHT = new SidedVector4i(0, 1, 0, 0);
    public static final SidedVector4i UNIT_BOTTOM = new SidedVector4i(0, 0, 1, 0);
    public static final SidedVector4i UNIT_LEFT = new SidedVector4i(0, 0, 0, 1);
    public static final SidedVector4i ONE = new SidedVector4i(1, 1, 1, 1);

    public SidedVector4i() {
    }

    public SidedVector4i(double x, double y, double z, double w) {
        super(x, y, z, w);
    }

    public SidedVector4i(int x, int y, int z, int w) {
        super(x, y, z, w);
    }

    public SidedVector4i(Vector2i v, double z, double w) {
        super(v, z, w);
    }

    public SidedVector4i(Vector2i v, int z, int w) {
        super(v, z, w);
    }

    public SidedVector4i(Vector2i v) {
        super(v);
    }

    public SidedVector4i(Vector3i v, double w) {
        super(v, w);
    }

    public SidedVector4i(Vector3i v, int w) {
        super(v, w);
    }

    public SidedVector4i(Vector3i v) {
        super(v);
    }

    public SidedVector4i(Vector4i v) {
        super(v);
    }

    public SidedVector4i(VectorNi v) {
        super(v);
    }

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

    public Vector2i getTopLeft() {
        return new Vector2i(getLeft(), getTop());
    }

    public Vector2i getTopRight() {
        return new Vector2i(getRight(), getTop());
    }

    public Vector2i getBottomRight() {
        return new Vector2i(getRight(), getBottom());
    }

    public Vector2i getBottomLeft() {
        return new Vector2i(getLeft(), getBottom());
    }

    public Vector2i getAsWidthHeight() {
        return new Vector2i(getLeft() + getRight(), getTop() + getBottom());
    }

    @Override
    public String toString() {
        return String.format("[top=%s,right=%s,bottom=%s,left=%s]", getTop(), getRight(), getBottom(), getLeft());
    }

}
