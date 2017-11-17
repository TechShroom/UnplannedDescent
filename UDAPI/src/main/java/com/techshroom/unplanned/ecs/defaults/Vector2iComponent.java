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
package com.techshroom.unplanned.ecs.defaults;

import java.util.Map;

import com.flowpowered.math.vector.Vector2i;
import com.techshroom.unplanned.core.util.Memoizer;
import com.techshroom.unplanned.ecs.CFType;
import com.techshroom.unplanned.ecs.CompEntAssoc;
import com.techshroom.unplanned.ecs.ComplexComponent;
import com.techshroom.unplanned.ecs.Component;
import com.techshroom.unplanned.ecs.ComponentField;

/**
 * Generic 2D vector component.
 */
public class Vector2iComponent extends ComplexComponent<Vector2i> {

    private final ComponentField<Integer> x = ComponentField.createNoId(getId(), "x", CFType.INTEGER);
    private final ComponentField<Integer> y = ComponentField.createNoId(getId(), "y", CFType.INTEGER);

    protected Vector2iComponent() {
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
    public Map<String, ComponentField<?>> getFields() {
        return Memoizer.memoize(this, "fields", t -> Component.makeFieldMap(x, y));
    }

}
