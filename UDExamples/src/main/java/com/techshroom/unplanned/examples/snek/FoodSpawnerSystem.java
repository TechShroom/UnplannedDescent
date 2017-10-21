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

import java.util.Random;
import java.util.Set;

import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;
import com.google.common.collect.ImmutableSet;
import com.techshroom.unplanned.core.util.Color;
import com.techshroom.unplanned.ecs.CSystem;
import com.techshroom.unplanned.ecs.CompEntAssoc;
import com.techshroom.unplanned.ecs.Component;

@AutoValue
public abstract class FoodSpawnerSystem implements CSystem {

    public static FoodSpawnerSystem create() {
        return new AutoValue_FoodSpawnerSystem();
    }

    FoodSpawnerSystem() {
    }

    @Override
    @Memoized
    public Set<Component> getComponents() {
        return ImmutableSet.of(Edible.INSTANCE);
    }

    @Override
    public void process(int entityId, CompEntAssoc assoc, long nanoDiff) {
    }

    @Override
    public void processList(CompEntAssoc assoc, long nanoDiff) {
        if (assoc.getEntities(Edible.INSTANCE).isEmpty()) {
            spawnFood(assoc);
        }
    }

    private final Random RNGESUS = new Random();

    private void spawnFood(CompEntAssoc assoc) {
        FoodPlan.start()
                .color(Color.BLUE)
                .gridPositionX(RNGESUS.nextInt(Snek.GRID_SIZE.getX()))
                .gridPositionY(RNGESUS.nextInt(Snek.GRID_SIZE.getY()))
                .build(assoc);
    }
}
