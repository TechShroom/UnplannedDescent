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
import com.techshroom.unplanned.ecs.CompEntAssoc;
import com.techshroom.unplanned.ecs.Component;
import com.techshroom.unplanned.ecs.defaults.IntervalCSystem;

@AutoValue
public abstract class MovementSystem extends IntervalCSystem {

    public static MovementSystem create() {
        return new AutoValue_MovementSystem();
    }

    MovementSystem() {
    }

    @Override
    @Memoized
    public Set<Component> getComponents() {
        return ImmutableSet.of(Direction.INSTANCE, GridPosition.INSTANCE, SnekHeadMarker.INSTANCE);
    }

    @Override
    protected int getInterval() {
        return 10;
    }

    @Override
    public void processInterval(int entityId, CompEntAssoc assoc, long nanoDiff) {
        Vector2i dirVec = Direction.INSTANCE.get(assoc, entityId).unit;
        Vector2i currentLoc = GridPosition.INSTANCE.get(assoc, entityId);
        Vector2i newLoc = dirVec.add(currentLoc);

        setLocation(entityId, assoc, newLoc);
    }

    private void setLocation(int entityId, CompEntAssoc assoc, Vector2i loc) {
        Vector2i ourPrev = GridPosition.INSTANCE.get(assoc, entityId);

        GridPosition.INSTANCE.set(assoc, entityId, loc);

        int prevRef = assoc.get(entityId, SnekBodyParts.INSTANCE.getPrev());
        if (prevRef == 0) {
            PrevGridPosition.INSTANCE.set(assoc, entityId, ourPrev);
        } else {
            setLocation(prevRef, assoc, ourPrev);
        }
    }

}
