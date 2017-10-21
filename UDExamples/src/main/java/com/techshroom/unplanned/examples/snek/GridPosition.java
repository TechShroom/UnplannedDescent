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

@AutoValue
public abstract class GridPosition extends ComplexComponent<Vector2i> {

    public static final GridPosition INSTANCE = new AutoValue_GridPosition();

    private final ComponentField<Integer> x = ComponentField.createNoId(getId(), "x", CFType.INTEGER);
    private final ComponentField<Integer> y = ComponentField.createNoId(getId(), "y", CFType.INTEGER);

    GridPosition() {
    }

    @Override
    public void set(CompEntAssoc assoc, int entityId, Vector2i value) {
        assoc.set(entityId, x, value.getX());
        assoc.set(entityId, y, value.getY());
    }

    @Override
    public Vector2i get(CompEntAssoc assoc, int entityId) {
        int x = assoc.get(entityId, this.x);
        int y = assoc.get(entityId, this.y);
        return Vector2i.from(x, y);
    }

    @Override
    @Memoized
    public Map<String, ComponentField<?>> getFields() {
        return Component.makeFieldMap(x, y);
    }

}
