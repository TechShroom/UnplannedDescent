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

package com.techshroom.unplanned.rp.ff2;

import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;
import com.google.common.collect.ImmutableMap;
import com.techshroom.unplanned.rp.RId;

/**
 * Representation of the FF2 index file. Used to look up where resources are
 * stored.
 */
@AutoValue
public abstract class FF2Index {

    @AutoValue
    public static abstract class Value {

        public static Value create(byte index, long offset, long size) {
            return new AutoValue_FF2Index_Value(index, offset, size);
        }

        Value() {
        }

        public abstract byte getIndex();

        public abstract long getOffset();

        public abstract long getSize();

    }

    public static Builder builder() {
        return new AutoValue_FF2Index.Builder();
    }

    @AutoValue.Builder
    public interface Builder {

        ImmutableMap.Builder<RId, Value> dataLookupBuilder();

        default Builder putValue(RId id, Value resource) {
            dataLookupBuilder().put(id, resource);
            return this;
        }

        FF2Index build();

    }

    FF2Index() {
    }

    public abstract ImmutableMap<RId, Value> getDataLookup();

    public final Value getData(RId resourceId) {
        return getDataLookup().get(resourceId);
    }

    @Memoized
    public int getFileCount() {
        return getDataLookup().values().stream().mapToInt(Value::getIndex).max().getAsInt() + 1;
    }

}
