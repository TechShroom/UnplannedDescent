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

package com.techshroom.unplanned.core.util;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.techshroom.unplanned.core.util.CompileGeneric.ClassCompileGeneric;

/**
 * Shoddy enhanced enum support.
 */
// TODO replace with Enhanced Enums if it ever lands in JDK 10
public abstract class FakeEnum<E extends FakeEnum<E>> implements Comparable<E> {

    private static final SetMultimap<Class<?>, FakeEnum<?>> FAKE_ENUM_REGISTRY = MultimapBuilder.hashKeys()
            .treeSetValues().build();

    private static int addFakeEnum(FakeEnum<?> e) {
        Set<FakeEnum<?>> store = FAKE_ENUM_REGISTRY.get(e.getClass());
        int ord = store.size();
        store.add(e);
        return ord;
    }

    protected static <E extends FakeEnum<E>> List<E> values(ClassCompileGeneric<E> clazz) {
        @SuppressWarnings("unchecked")
        // should all be of the appropriate type
        Set<E> values = (Set<E>) FAKE_ENUM_REGISTRY.get(clazz.getRawClass());
        return ImmutableList.copyOf(values);
    }

    private final int ordinal;
    private final String name;

    protected FakeEnum(String name) {
        this.name = name;
        this.ordinal = addFakeEnum(this);
    }

    @Override
    public int compareTo(E o) {
        return Integer.compare(ordinal, o.ordinal());
    }

    public final int ordinal() {
        return ordinal;
    }

    public final String name() {
        return name;
    }

    @Override
    public String toString() {
        return name();
    }

}
