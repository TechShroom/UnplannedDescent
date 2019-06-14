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

import java.util.HashMap;
import java.util.WeakHashMap;
import java.util.function.Function;

import com.google.common.collect.Table;
import com.google.common.collect.Tables;

public final class Memoizer {

    private static final Table<Object, String, Object> MEMOIZE_TABLE = Tables.newCustomTable(new WeakHashMap<>(), HashMap::new);

    public static <O, T> T memoize(O owner, String key, Function<O, T> valueCalculator) {
        // should be safe, unless someone tries to break it...
        @SuppressWarnings("unchecked")
        T value = (T) MEMOIZE_TABLE.row(owner).computeIfAbsent(key, k -> valueCalculator.apply(owner));
        return value;
    }

}
