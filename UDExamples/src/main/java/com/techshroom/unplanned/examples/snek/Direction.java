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
package com.techshroom.unplanned.examples.snek;

import java.util.Map;

import com.flowpowered.math.vector.Vector2i;
import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;
import com.techshroom.unplanned.ecs.CFType;
import com.techshroom.unplanned.ecs.CompEntAssoc;
import com.techshroom.unplanned.ecs.ComplexComponent;
import com.techshroom.unplanned.ecs.Component;
import com.techshroom.unplanned.ecs.ComponentField;
import com.techshroom.unplanned.examples.snek.Direction.Dir;

@AutoValue
public abstract class Direction extends ComplexComponent<Dir> {

    public enum Dir {
        U(Vector2i.from(0, -1)),
        D(Vector2i.from(0, 1)),
        L(Vector2i.from(-1, 0)),
        R(Vector2i.from(1, 0));

        public final Vector2i unit;

        Dir(Vector2i unit) {
            this.unit = unit;
        }

    }

    public static final Direction INSTANCE = new AutoValue_Direction();

    private final ComponentField<Byte> ordinal = ComponentField.createNoId(getId(), "ordinal", CFType.BYTE);

    Direction() {
    }

    @Override
    public void set(CompEntAssoc assoc, int entityId, Dir value) {
        assoc.set(entityId, ordinal, (byte) value.ordinal());
    }

    @Override
    public Dir get(CompEntAssoc assoc, int entityId) {
        int ord = assoc.get(entityId, this.ordinal);
        return Dir.values()[ord];
    }

    @Override
    @Memoized
    public Map<String, ComponentField<?>> getFields() {
        return Component.makeFieldMap(ordinal);
    }

}
