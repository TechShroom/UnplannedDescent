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
package com.techshroom.unplanned.blitter.matrix;

import static org.lwjgl.opengl.GL20.glUniformMatrix3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

import com.flowpowered.math.matrix.Matrix3f;
import com.flowpowered.math.matrix.Matrix4f;
import com.techshroom.unplanned.window.ShaderInitialization;
import com.techshroom.unplanned.window.ShaderInitialization.Uniform;

public class GLMatrixUploader implements MatrixUploader {

    @Override
    public void upload(Matrix4f model, Matrix4f view, Matrix4f projection) {
        setMat(Uniform.MVP, Matrices.buildMVPMatrix(model, view, projection));
        setMat(Uniform.MODEL, model);
        // compute normal matrix: transpose inverse model
        Matrix3f normal = model.toMatrix3().invert().transpose();
        setMat(Uniform.NORMAL, normal);
    }

    private void setMat(Uniform uniform, Matrix4f mat) {
        glUniformMatrix4fv(ShaderInitialization.getUniform(uniform), false, mat.toArray(true));
    }

    private void setMat(Uniform uniform, Matrix3f mat) {
        glUniformMatrix3fv(ShaderInitialization.getUniform(uniform), false, mat.toArray(true));
    }

}
