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

package com.techshroom.unplanned.ecs;

import java.util.UUID;

import com.google.auto.value.AutoValue;
import com.techshroom.unplanned.core.util.UUID5;

/**
 * Field information for a component.
 */
@AutoValue
public abstract class ComponentField<T> {

    public static <T> ComponentField<T> createNoId(UUID owner, String name, CFType<T> type) {
        return create(UUID5.create(owner, name), name, type);
    }

    public static <T> ComponentField<T> create(UUID id, String name, CFType<T> type) {
        return new AutoValue_ComponentField<>(id, name, type);
    }

    ComponentField() {
    }

    public abstract UUID getId();

    public abstract String getName();

    public abstract CFType<T> getType();

}
