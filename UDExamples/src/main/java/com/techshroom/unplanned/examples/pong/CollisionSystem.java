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
import com.techshroom.unplanned.ecs.CSystem;
import com.techshroom.unplanned.ecs.CompEntAssoc;
import com.techshroom.unplanned.ecs.Component;
import com.techshroom.unplanned.ecs.defaults.Removed;
import com.techshroom.unplanned.examples.snek.GridPosition;
import com.techshroom.unplanned.geometry.WHRectangleI;

/**
 * Moves stuff according to direction.
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
        return ImmutableSet.of(BallMarker.INSTANCE, Position.INSTANCE, Size.INSTANCE, Velocity.INSTANCE);
    }

    @Override
    public void process(int entityId, CompEntAssoc assoc, long nanoDiff) {
        WHRectangleI bb = getBB(entityId, assoc);
        if (outOfBounds(bb)) {
            if (outOfBoundsBounce(bb)) {
                
            } else {
                Removed.INSTANCE.set(assoc, entityId, true);
                if (bb.getX() < 0) {
                    // award points to AI
//                    Pong.EVENTS.post(new AwardPointsEvent());
                }
                return;
            }
        }
        if (hit(entityId, PaddleMarker.INSTANCE, assoc) != 0) {
            Velocity.INSTANCE.modify(assoc, entityId, vec -> vec.mul(-1, 1));
        }
    }

    private WHRectangleI getBB(int entityId, CompEntAssoc assoc) {
        Vector2i pos = Position.INSTANCE.get(assoc, entityId);
        Vector2i size = Size.INSTANCE.get(assoc, entityId);
        return WHRectangleI.of(pos.getX(), pos.getY(), size.getX(), size.getY());
    }

    private int hit(int originator, Component comp, CompEntAssoc assoc) {
        Vector2i ourPos = GridPosition.INSTANCE.get(assoc, originator);
        return assoc.getEntities(comp).detectIfNone(ent -> {
            Vector2i theirPos = GridPosition.INSTANCE.get(assoc, ent);
            return ent != originator && ourPos.equals(theirPos);
        }, 0);
    }

    private static final WHRectangleI OFF_SCREEN_TOP = WHRectangleI.of(0, 0, Pong.SCREEN_SIZE.getX(), -Pong.SCREEN_SIZE.getY());
    private static final WHRectangleI OFF_SCREEN_BOT = WHRectangleI.of(0, Pong.SCREEN_SIZE.getY(), Pong.SCREEN_SIZE.getX(), Pong.SCREEN_SIZE.getY());

    private boolean outOfBoundsBounce(WHRectangleI bb) {
        return OFF_SCREEN_TOP.intersects(bb) || OFF_SCREEN_BOT.intersects(bb);
    }

    private static final WHRectangleI SCREEN = WHRectangleI.of(0, 0, Pong.SCREEN_SIZE.getX(), Pong.SCREEN_SIZE.getY());

    private boolean outOfBounds(WHRectangleI rect) {
        return !rect.intersects(SCREEN);
    }

}
