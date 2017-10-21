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
package com.techshroom.unplanned.ecs;

import java.util.List;

import com.techshroom.unplanned.core.util.CompileGeneric;
import com.techshroom.unplanned.core.util.CompileGeneric.ClassCompileGeneric;
import com.techshroom.unplanned.core.util.FakeEnum;

public abstract class CFType<T> extends FakeEnum<CFType<T>> {

    public static List<CFType<?>> values() {
        @SuppressWarnings("rawtypes")
        ClassCompileGeneric<CFType> cg = CompileGeneric.specify(CFType.class);
        @SuppressWarnings("unchecked")
        List<CFType<?>> values = (List<CFType<?>>) values(cg);
        return values;
    }

    public static final CFType<Boolean> BOOLEAN = new CFType<Boolean>("BOOLEAN", Boolean.class, false) {};
    public static final CFType<Byte> BYTE = new CFType<Byte>("BYTE", Byte.class, (byte) 0) {};
    public static final CFType<Short> SHORT = new CFType<Short>("SHORT", Short.class, (short) 0) {};
    public static final CFType<Integer> INTEGER = new CFType<Integer>("INTEGER", Integer.class, 0) {};
    public static final CFType<Long> LONG = new CFType<Long>("LONG", Long.class, 0L) {};
    public static final CFType<String> STRING = new CFType<String>("STRING", String.class, "") {};

    public final Class<T> type;
    public final T defaultValue;

    private CFType(String name, Class<T> type, T defaultValue) {
        super(name);
        this.type = type;
        this.defaultValue = defaultValue;
    }

}
