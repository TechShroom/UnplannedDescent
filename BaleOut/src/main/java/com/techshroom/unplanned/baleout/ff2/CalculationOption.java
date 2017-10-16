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
package com.techshroom.unplanned.baleout.ff2;

import java.util.List;

import com.techshroom.unplanned.core.util.CompileGeneric;
import com.techshroom.unplanned.core.util.CompileGeneric.ClassCompileGeneric;
import com.techshroom.unplanned.core.util.FakeEnum;
import com.techshroom.unplanned.core.util.MetricUnit;
import com.techshroom.unplanned.core.util.genericmap.GenericMapKey;

/**
 * Pseudo-enum for calculation options.
 */
public class CalculationOption<T> extends FakeEnum<CalculationOption<T>> implements GenericMapKey<T> {

    public static List<CalculationOption<?>> values() {
        @SuppressWarnings("rawtypes")
        ClassCompileGeneric<CalculationOption> cg = CompileGeneric.specify(CalculationOption.class);
        @SuppressWarnings("unchecked")
        List<CalculationOption<?>> values = (List<CalculationOption<?>>) values(cg);
        return values;
    }

    // defaults to 1GB
    public static final CalculationOption<Long> MAXIMUM_FILE_SIZE =
            new CalculationOption<>("MAXIMUM_FILE_SIZE", "maximumFileSize", Long.class, MetricUnit.GIGA.toNone(1));

    public final String camelCaseName;
    public final Class<T> type;
    public final T defaultValue;

    private CalculationOption(String name, String camelCaseName, Class<T> type, T defaultValue) {
        super(name);
        this.camelCaseName = camelCaseName;
        this.type = type;
        this.defaultValue = defaultValue;
    }

}
