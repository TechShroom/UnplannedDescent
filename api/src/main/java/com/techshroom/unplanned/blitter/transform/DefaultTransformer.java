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

import com.flowpowered.math.matrix.Matrix4f;
import com.google.common.collect.ImmutableList;
import com.techshroom.unplanned.blitter.matrix.MatrixUploader;

import javax.annotation.Nullable;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

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

        void m(Function<Matrix4f, Matrix4f> mod) {
            ref = mod.apply(ref);
        }

    }

    private static final class M4fTransformer implements MatrixTransformer {

        private final Deque<M4fRef> stack = new LinkedList<>();

        M4fTransformer() {
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
        public MatrixTransformer transform(UnaryOperator<Matrix4f> transformation) {
            ref().m(transformation);
            return this;
        }
    }

    private final M4fTransformer model = new M4fTransformer();
    private final M4fTransformer camera = new M4fTransformer();
    private final M4fTransformer projection = new M4fTransformer();
    private final List<M4fTransformer> m4fts = ImmutableList.of(model, camera, projection);

    private DefaultTransformer() {
    }

    @Override
    public void apply(MatrixUploader uploader) {
        uploader.upload(model.ref().ref, camera.ref().ref.invert(), projection.ref().ref);
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
