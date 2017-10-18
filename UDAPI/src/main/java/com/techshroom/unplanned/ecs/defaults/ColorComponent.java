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

import java.util.Set;

import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;
import com.google.common.collect.ImmutableSet;
import com.techshroom.unplanned.core.util.Color;
import com.techshroom.unplanned.ecs.CFType;
import com.techshroom.unplanned.ecs.CompEntAssoc;
import com.techshroom.unplanned.ecs.ComplexComponent;
import com.techshroom.unplanned.ecs.ComponentField;

@AutoValue
public abstract class ColorComponent extends ComplexComponent<Color> {

    public static final ColorComponent INSTANCE = new AutoValue_ColorComponent();

    private final ComponentField<Integer> r = ComponentField.createNoId(getId(), "r", CFType.INTEGER);
    private final ComponentField<Integer> g = ComponentField.createNoId(getId(), "g", CFType.INTEGER);
    private final ComponentField<Integer> b = ComponentField.createNoId(getId(), "b", CFType.INTEGER);
    private final ComponentField<Integer> a = ComponentField.createNoId(getId(), "a", CFType.INTEGER);

    ColorComponent() {
    }

    @Override
    public void set(CompEntAssoc assoc, int entityId, Color color) {
        assoc.set(entityId, this.r, color.getRed());
        assoc.set(entityId, this.g, color.getGreen());
        assoc.set(entityId, this.b, color.getBlue());
        assoc.set(entityId, this.a, color.getAlpha());
    }

    @Override
    public Color get(CompEntAssoc assoc, int entityId) {
        int r = assoc.get(entityId, this.r);
        int g = assoc.get(entityId, this.g);
        int b = assoc.get(entityId, this.b);
        int a = assoc.get(entityId, this.a);
        return Color.fromInt(r, g, b, a);
    }

    public ComponentField<Integer> getR() {
        return r;
    }

    public ComponentField<Integer> getG() {
        return g;
    }

    public ComponentField<Integer> getB() {
        return b;
    }

    public ComponentField<Integer> getA() {
        return a;
    }

    @Override
    @Memoized
    public Set<ComponentField<?>> getFields() {
        return ImmutableSet.of(r, g, b, a);
    }

}