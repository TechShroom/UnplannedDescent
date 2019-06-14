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

package com.techshroom.unplanned.baleout;

import java.util.Map;

import com.google.auto.value.AutoValue;
import com.techshroom.unplanned.baleout.MapValueConverter.ValueWrapper;

import joptsimple.ValueConverter;

public abstract class MapValueConverter<T> implements ValueConverter<ValueWrapper<T>> {
    
    @AutoValue
    public abstract static class ValueWrapper<T> {
        
        public static <T> ValueWrapper<T> wrap(String key, T value) {
            return new AutoValue_MapValueConverter_ValueWrapper<>(key, value);
        }
        
        ValueWrapper() {
        }
        
        public abstract String getKey();
        
        public abstract T getValue();
        
        @Override
        public final String toString() {
            return getKey();
        }
        
    }

    private final Map<String, T> values;

    protected MapValueConverter(Map<String, T> values) {
        this.values = values;
    }

    @Override
    public ValueWrapper<T> convert(String value) {
        return ValueWrapper.wrap(value, values.get(value));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends ValueWrapper<T>> valueType() {
        return (Class<? extends ValueWrapper<T>>) (Object) ValueWrapper.class;
    }

    @Override
    public String valuePattern() {
        return String.join("|", values.keySet());
    }

}
