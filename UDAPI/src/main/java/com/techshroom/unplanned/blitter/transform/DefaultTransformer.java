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

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.flowpowered.math.imaginary.Quaternionf;
import com.flowpowered.math.matrix.Matrix4f;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector4f;
import com.google.common.collect.ImmutableList;
import com.techshroom.unplanned.blitter.matrix.MatrixUploader;

public class DefaultTransformer implements TransformStack {

    private static final ThreadLocal<DefaultTransformer> INSTANCE = ThreadLocal.withInitial(DefaultTransformer::new);

    public static DefaultTransformer getInstance() {
        return INSTANCE.get();
    }

    private static final class M4fRef {

        private Matrix4f ref = Matrix4f.IDENTITY;

        M4fRef(@Nullable M4fRef prev) {
            if (prev != null) {
                ref = prev.ref;
            }
        }

        M4fRef m(Function<Matrix4f, Matrix4f> mod) {
            ref = mod.apply(ref);
            return this;
        }

    }

    private static final class M4fTransformer implements MatrixTransformer {

        private final Deque<M4fRef> stack = new LinkedList<>();
        private final boolean inverted;

        M4fTransformer(boolean inverted) {
            this.inverted = inverted;
        }

        void push() {
            stack.push(new M4fRef(stack.peekFirst()));
        }

        void pop() {
            stack.pop();
        }

        M4fRef ref() {
            return stack.getFirst();
        }

        @Override
        public void set(Matrix4f matrix) {
            ref().ref = matrix;
        }

        @Override
        public M4fTransformer translate(Vector3f translation) {
            Vector3f v;
            if (inverted) {
                v = translation.negate();
            } else {
                v = translation;
            }
            ref().m(m -> m.translate(v));
            return this;
        }

        @Override
        public M4fTransformer rotate(Quaternionf quat) {
            Quaternionf q;
            if (inverted) {
                q = quat.conjugate().normalize();
            } else {
                q = quat;
            }
            ref().m(m -> m.rotate(q));
            return this;
        }

        @Override
        public M4fTransformer scale(Vector3f scale) {
            Vector4f s;
            if (inverted) {
                s = scale.toVector4().negate();
            } else {
                s = scale.toVector4();
            }
            ref().m(m -> m.scale(s));
            return this;
        }

    }

    private final M4fTransformer model = new M4fTransformer(false);
    private final M4fTransformer camera = new M4fTransformer(true);
    private final M4fTransformer projection = new M4fTransformer(false);
    private final List<M4fTransformer> m4fts = ImmutableList.of(model, camera, projection);

    private DefaultTransformer() {
    }

    @Override
    public void apply(MatrixUploader uploader) {
        uploader.upload(model.ref().ref, camera.ref().ref, projection.ref().ref);
    }

    @Override
    public MatrixTransformer model() {
        return model;
    }

    @Override
    public MatrixTransformer camera() {
        return camera;
    }

    @Override
    public MatrixTransformer projection() {
        return projection;
    }

    public TransformStack push() {
        m4fts.forEach(M4fTransformer::push);
        return this;
    }

    @Override
    public void pop() {
        m4fts.forEach(M4fTransformer::pop);
    }

}
