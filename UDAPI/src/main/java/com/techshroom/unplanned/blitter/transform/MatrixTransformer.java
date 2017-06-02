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
package com.techshroom.unplanned.blitter.transform;

import com.flowpowered.math.imaginary.Quaternionf;
import com.flowpowered.math.matrix.Matrix4f;
import com.flowpowered.math.vector.Vector3f;

public interface MatrixTransformer {
    
    default MatrixTransformer reset() {
        set(Matrix4f.IDENTITY);
        return this;
    }

    void set(Matrix4f matrix);

    default MatrixTransformer translate(float x, float y, float z) {
        return translate(new Vector3f(x, y, z));
    }

    MatrixTransformer translate(Vector3f translation);

    default MatrixTransformer rotate(Vector3f eulerAngles) {
        return rotate(Quaternionf.fromAxesAnglesDeg(eulerAngles.getX(), eulerAngles.getY(), eulerAngles.getZ()));
    }

    MatrixTransformer rotate(Quaternionf quat);

    default MatrixTransformer scale(float x, float y, float z) {
        return scale(new Vector3f(x, y, z));
    }

    MatrixTransformer scale(Vector3f scale);

}
