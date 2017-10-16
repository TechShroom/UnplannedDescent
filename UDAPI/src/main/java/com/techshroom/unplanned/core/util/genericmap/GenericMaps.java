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
package com.techshroom.unplanned.core.util.genericmap;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class GenericMaps {

    public static ImmutableGenericMap immutableMap(Supplier<Map<GenericMapKey<?>, Object>> mapConstructor) {
        return immutableMap(mapConstructor.get());
    }

    public static ImmutableGenericMap immutableMap(Map<GenericMapKey<?>, Object> map) {
        return new Immutable(map);
    }

    public static MutableGenericMap mutableMap(Supplier<Map<GenericMapKey<?>, Object>> mapConstructor) {
        return mutableMap(mapConstructor.get());
    }

    public static MutableGenericMap mutableMap(Map<GenericMapKey<?>, Object> map) {
        return new Mutable(map);
    }

    public static ImmutableGenericMap immutableCopy(GenericMap map) {
        if (map instanceof ImmutableGenericMap) {
            return (ImmutableGenericMap) map;
        }
        if (map instanceof Base) {
            return immutableMap(((Base) map).delegate);
        }
        throw new AssertionError("Someone else is implementing GenericMap, we don't support that yet.");
    }

    private abstract static class Base implements GenericMap {

        protected final Map<GenericMapKey<?>, Object> delegate;

        protected Base(Map<GenericMapKey<?>, Object> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean containsKey(GenericMapKey<?> key) {
            return delegate.containsKey(key);
        }

        @Override
        public <V> V get(GenericMapKey<V> key) {
            @SuppressWarnings("unchecked")
            V v = (V) delegate.get(key);
            return v;
        }

        @Override
        public <V> V getOrDefault(GenericMapKey<V> key, V defaultValue) {
            @SuppressWarnings("unchecked")
            V v = (V) delegate.getOrDefault(key, defaultValue);
            return v;
        }

    }

    private static final class Immutable extends Base implements ImmutableGenericMap {

        protected Immutable(Map<GenericMapKey<?>, Object> delegate) {
            super(delegate);
        }

    }

    private static final class Mutable extends Base implements MutableGenericMap {

        protected Mutable(Map<GenericMapKey<?>, Object> delegate) {
            super(delegate);
        }

        @Override
        public <V> V put(GenericMapKey<V> key, V value) {
            @SuppressWarnings("unchecked")
            V old = (V) delegate.put(key, value);
            return old;
        }

        @Override
        public <V> V computeIfAbsent(GenericMapKey<V> key, Function<GenericMapKey<V>, V> func) {
            @SuppressWarnings("unchecked")
            V compute = (V) delegate.computeIfAbsent(key, k -> func.apply(key));
            return compute;
        }

    }

    private GenericMaps() {
    }

}
