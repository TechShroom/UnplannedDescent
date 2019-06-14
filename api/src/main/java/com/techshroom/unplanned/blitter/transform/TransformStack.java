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

package com.techshroom.unplanned.blitter.transform;

import static com.google.common.base.Preconditions.checkArgument;

import org.lwjgl.system.MemoryStack;

import com.flowpowered.math.matrix.Matrix4f;
import com.techshroom.unplanned.blitter.matrix.Matrices;
import com.techshroom.unplanned.blitter.matrix.MatrixUploader;

/**
 * Like {@link MemoryStack}, but for transforms.
 */
public interface TransformStack extends AutoCloseable {

    MatrixTransformer model();

    MatrixTransformer camera();

    MatrixTransformer projection();

    default void orthographic(double width, double height, double zNear, double zFar) {
        projection().set(Matrices.orthographicProjection(width, height, zNear, zFar));
    }

    default void perspective(double fov, double width, double height, double near, double far) {
        checkArgument(height != 0, "height cannot be zero");
        checkArgument(near != 0, "near plane cannot be zero");
        checkArgument(near != far, "near plane cannot equal far plane");
        double aspect = width / height;
        projection().set(Matrix4f.createPerspective(fov, aspect, near, far));
    }

    /**
     * Uploads the matrices stored in the stack.
     */
    void apply(MatrixUploader uploader);

    /**
     * Pop a frame. Un-does all transforms currently active.
     */
    void pop();

    /**
     * Calls {@link #pop} on this {@link TransformStack}.
     *
     * <p>
     * This method should not be used directly. It is called automatically when
     * the {@code TransformStack} is used as a resource in a try-with-resources
     * statement.
     * </p>
     */
    @Override
    default void close() {
        pop();
    }

}
