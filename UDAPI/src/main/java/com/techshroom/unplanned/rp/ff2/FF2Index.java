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
package com.techshroom.unplanned.rp.ff2;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import com.techshroom.unplanned.rp.RId;
import com.techshroom.unplanned.rp.ResourceType;

/**
 * Representation of the FF2 index file. Used to look up where resources are
 * stored.
 */
class FF2Index {

    @AutoValue
    static abstract class Value {

        static Value create(byte index, long offset, int size, ResourceType<?> type) {
            return new AutoValue_FF2Index_Value(index, offset, size, type);
        }

        Value() {
        }

        public abstract byte getIndex();

        public abstract long getOffset();

        public abstract int getSize();

        public abstract ResourceType<?> getType();

    }

    private final ImmutableMap<RId, Value> fileLookup;
    private final int fileCount;

    public FF2Index(ImmutableMap<RId, Value> fileLookup) {
        this.fileLookup = fileLookup;
        this.fileCount = fileLookup.values().stream().mapToInt(Value::getIndex).max().getAsInt() + 1;
    }

    public Value getData(RId resourceId) {
        return fileLookup.get(resourceId);
    }

    public int getFileCount() {
        return fileCount;
    }

}
