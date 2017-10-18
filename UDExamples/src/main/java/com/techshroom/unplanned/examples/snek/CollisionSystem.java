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
import com.techshroom.unplanned.core.util.Color;
import com.techshroom.unplanned.ecs.CompEntAssoc;
import com.techshroom.unplanned.ecs.Component;
import com.techshroom.unplanned.ecs.CSystem;
import com.techshroom.unplanned.ecs.defaults.ColorComponent;

/**
 * Handles collisions with body and {@link Edible}.
 */
@AutoValue
public abstract class CollisionSystem implements CSystem {

    public static CollisionSystem create() {
        return new AutoValue_CollisionSystem();
    }

    CollisionSystem() {
    }

    @Override
    @Memoized
    public Set<Component> getComponents() {
        return ImmutableSet.of(GridPosition.INSTANCE, SnekBody.INSTANCE);
    }

    @Override
    public void process(int entityId, CompEntAssoc assoc) {
        if (!assoc.get(entityId, SnekBody.INSTANCE.getHead())) {
            return;
        }
        // we only process collisions using the head
        Vector2i pos = GridPosition.INSTANCE.get(assoc, entityId);
        if (outOfBounds(pos)) {
            kill(entityId, assoc);
            return;
        }
        if (hit(entityId, Edible.INSTANCE, assoc)) {
            addBody(entityId, assoc);
        }
        if (hit(entityId, SnekBody.INSTANCE, assoc)) {
            kill(entityId, assoc);
            return;
        }
    }

    private void kill(int entityId, CompEntAssoc assoc) {
        int prev = entityId;
        while (prev != 0) {
            int k = prev;
            prev = assoc.get(k, SnekBody.INSTANCE.getPrev());
            assoc.remove(k);
        }
    }

    private void addBody(int entityId, CompEntAssoc assoc) {
        int tail = entityId;
        int prev;
        while ((prev = assoc.get(tail, SnekBody.INSTANCE.getPrev())) != 0) {
            tail = prev;
        }
        int newBody = assoc.newEntity(GridPosition.INSTANCE, PrevGridPosition.INSTANCE, SnekBody.INSTANCE, ColorComponent.INSTANCE);
        // assign position to tail previous
        GridPosition.INSTANCE.set(assoc, newBody, PrevGridPosition.INSTANCE.get(assoc, tail));
        ColorComponent.INSTANCE.set(assoc, newBody, Color.GREEN);
        assoc.set(entityId, SnekBody.INSTANCE.getPrev(), newBody);
    }

    private boolean hit(int originator, Component comp, CompEntAssoc assoc) {
        Vector2i ourPos = GridPosition.INSTANCE.get(assoc, originator);
        return assoc.getEntities(comp).anySatisfy(ent -> {
            Vector2i theirPos = GridPosition.INSTANCE.get(assoc, ent);
            return ent != originator && ourPos.equals(theirPos);
        });
    }

    private boolean outOfBounds(Vector2i pos) {
        int x = pos.getX();
        int y = pos.getY();
        boolean insideX = 0 <= x && x < Snek.GRID_SIZE.getX();
        boolean insideY = 0 <= y && y < Snek.GRID_SIZE.getY();
        return !insideX || !insideY;
    }

}
