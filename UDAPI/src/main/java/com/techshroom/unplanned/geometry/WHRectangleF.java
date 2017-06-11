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

import com.flowpowered.math.vector.Vector2f;
import com.google.auto.value.AutoValue;

/**
 * Rectangle based on x, y, width and height, rather than 4 points.
 */
@AutoValue
public abstract class WHRectangleF {

    public static WHRectangleF of(float x, float y, float w, float h) {
        return new AutoValue_WHRectangleF(x, y, w, h);
    }

    public static WHRectangleF fromXY(float x1, float y1, float x2, float y2) {
        return new AutoValue_WHRectangleF(x1, y1, x2 - x1, y2 - y1);
    }

    WHRectangleF() {
    }

    public abstract float getX();

    public abstract float getY();

    public abstract float getWidth();

    public abstract float getHeight();

    public final boolean contains(Vector2f vec) {
        boolean xIn = getX() <= vec.getX() && vec.getX() <= getX() + getWidth();
        boolean yIn = getY() <= vec.getY() && vec.getY() <= getY() + getHeight();
        return xIn && yIn;
    }

}
