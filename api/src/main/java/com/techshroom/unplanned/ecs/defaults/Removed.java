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

package com.techshroom.unplanned.ecs.defaults;

import java.util.Map;

import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;
import com.techshroom.unplanned.ecs.CFType;
import com.techshroom.unplanned.ecs.CompEntAssoc;
import com.techshroom.unplanned.ecs.ComplexComponent;
import com.techshroom.unplanned.ecs.Component;
import com.techshroom.unplanned.ecs.ComponentField;

@AutoValue
public abstract class Removed extends ComplexComponent<Boolean> {

    public static final Removed INSTANCE = new AutoValue_Removed();

    private final ComponentField<Boolean> removed = ComponentField.createNoId(getId(), "removed", CFType.BOOLEAN);

    Removed() {
    }

    @Override
    public void set(CompEntAssoc assoc, int entityId, Boolean removed) {
        assoc.set(entityId, this.removed, removed);
    }

    @Override
    public Boolean get(CompEntAssoc assoc, int entityId) {
        return assoc.get(entityId, this.removed);
    }

    @Override
    @Memoized
    public Map<String, ComponentField<?>> getFields() {
        return Component.makeFieldMap(removed);
    }
}
