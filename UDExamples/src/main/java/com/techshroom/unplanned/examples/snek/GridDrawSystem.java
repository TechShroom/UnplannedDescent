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
 * Draws entities with the {@link GridPosition} and ColorComponent components.
 */
@AutoValue
public abstract class GridDrawSystem implements CSystem {

    public static GridDrawSystem create(DigitalPen digiPen) {
        return new AutoValue_GridDrawSystem(digiPen);
    }

    GridDrawSystem() {
    }

    public abstract DigitalPen digiPen();

    @Override
    @Memoized
    public Set<Component> getComponents() {
        return ImmutableSet.of(GridPosition.INSTANCE, ColorComponent.INSTANCE);
    }

    @Override
    public void process(int entityId, CompEntAssoc assoc, long nanoDiff) {
        Vector2i stepVec = Snek.CELL_DIM.add(Snek.BORDER_DIM.mul(2));
        Vector2i size = Snek.CELL_DIM;
        Vector2i pos = GridPosition.INSTANCE.get(assoc, entityId);
        Vector2i rectPos = pos.mul(stepVec).add(Snek.BORDER_DIM);
        digiPen().fill(() -> {
            digiPen().setColor(ColorComponent.INSTANCE.get(assoc, entityId));
            digiPen().rect(rectPos.getX(), rectPos.getY(), size.getX(), size.getY());
        });
    }

}
