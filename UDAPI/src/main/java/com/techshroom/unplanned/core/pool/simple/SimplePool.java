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
package com.techshroom.unplanned.core.pool.simple;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.Array;
import java.util.function.Supplier;

import com.techshroom.unplanned.core.pool.Pool;

/**
 * Simple pool off-loads the actual state management to the objects themselves.
 * It only manages the pool of objects.
 * 
 * @param <L>
 *            - the liquid type
 * @param <S>
 *            - the solid type
 */
public class SimplePool<L extends SimpleLiquid<S>, S extends SimpleSolid<L>> implements Pool<L, S> {

    public static <L extends SimpleLiquid<S>, S extends SimpleSolid<L>>
            SimplePool<L, S> create(int size, Supplier<L> liquidConstructor) {
        checkArgument(size > 0, "pools must contain some objects");
        L first = liquidConstructor.get();
        @SuppressWarnings("unchecked")
        L[] pool = (L[]) Array.newInstance(first.getClass(), size);
        pool[0] = first;
        for (int i = 1; i < size; i++) {
            pool[i] = liquidConstructor.get();
        }
        return new SimplePool<>(pool);
    }

    private final L[] pool;
    private int poolIndex = 0;

    private SimplePool(L[] pool) {
        this.pool = pool;
    }

    @Override
    public L take() {
        checkState(poolIndex < pool.length, "pool is empty");
        L ret = pool[poolIndex];
        pool[poolIndex] = null;
        poolIndex++;
        return ret;
    }

    @Override
    public S freeze(L liquid) {
        return liquid.freeze();
    }

    @Override
    public void releaseLiquid(L liquid) {
        liquid.release();
        checkState(poolIndex > 0, "pool is full");
        poolIndex--;
        pool[poolIndex] = liquid;
    }

    @Override
    public void releaseSolid(S solid) {
        releaseLiquid(solid.melt());
    }

}
