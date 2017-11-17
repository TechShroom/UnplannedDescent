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
package com.techshroom.unplanned.examples.pong;

import java.util.Set;

import com.flowpowered.math.vector.Vector2i;
import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;
import com.google.common.collect.ImmutableSet;
import com.techshroom.unplanned.blitter.pen.DigitalPen;
import com.techshroom.unplanned.ecs.CSystem;
import com.techshroom.unplanned.ecs.CompEntAssoc;
import com.techshroom.unplanned.ecs.Component;
import com.techshroom.unplanned.ecs.defaults.ColorComponent;

/**
 * Draws entities with the {@link Position} and {@link ColorComponent}
 * components.
 */
@AutoValue
public abstract class ColorDrawSystem implements CSystem {

    public static ColorDrawSystem create(DigitalPen digiPen) {
        return new AutoValue_ColorDrawSystem(digiPen);
    }

    ColorDrawSystem() {
    }

    public abstract DigitalPen digiPen();

    @Override
    @Memoized
    public Set<Component> getComponents() {
        return ImmutableSet.of(Position.INSTANCE, Size.INSTANCE, ColorComponent.INSTANCE);
    }

    @Override
    public void process(int entityId, CompEntAssoc assoc, long nanoDiff) {
        Vector2i pos = Position.INSTANCE.get(assoc, entityId);
        Vector2i size = Size.INSTANCE.get(assoc, entityId);
        digiPen().fill(digiPen().getInk(ColorComponent.INSTANCE.get(assoc, entityId)), () -> {
            digiPen().rect(pos.getX(), pos.getY(), size.getX(), size.getY());
        });
    }

}
