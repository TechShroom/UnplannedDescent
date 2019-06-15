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

package com.techshroom.unplanned.blitter.matrix;

import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.GenericMath;
import com.flowpowered.math.matrix.Matrix4f;
import com.flowpowered.math.vector.Vector3f;

/**
 * Matrix helper. All matrices are row-major, and translation should be handled
 * in the graphics implementation.
 */
public final class Matrices {

    // TODO perspective projection
    // figure out what it is and what needs to be added to compute it
    // public static Matrix4f perspectiveProjection(Window window) {
    //
    // }

    public static Matrix4f orthographicProjection(double width, double height, double zNear, double zFar) {
        double halfW = width / 2;
        double halfH = height / 2;
        return Matrix4f.createOrthographic(halfW, -halfW, -halfH, halfH, zNear, zFar);
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f center, Vector3f up) {
        final Vector3f f = GenericMath.normalizeSafe(center.sub(eye));
        final Vector3f s = GenericMath.normalizeSafe(f.cross(up));
        final Vector3f u = s.cross(f);
        return new Matrix4f(
                s.getX(), s.getY(), s.getZ(), 0,
                u.getX(), u.getY(), u.getZ(), 0,
                -f.getX(), -f.getY(), -f.getZ(), 0,
                -s.dot(eye), -u.dot(eye), f.dot(eye), 1).transpose();
    }

    public static Matrix4f buildMVPMatrix(Matrix4f model, Matrix4f view, Matrix4f proj) {
        checkNotNull(model, "model");
        checkNotNull(view, "view");
        checkNotNull(proj, "proj");
        Matrix4f fin = proj;
        fin = fin.mul(view);
        fin = fin.mul(model);
        return fin;
    }

    private Matrices() {
    }

}
