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
package com.techshroom.unplanned.ecs;

import java.util.function.BiFunction;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

import com.techshroom.unplanned.ecs.CEAFactory.Builder;

public class CEAFactoryBuilderBase implements CEAFactory.Builder {

    private final BiFunction<ImmutableList<CSystem>, ImmutableList<Component>, CompEntAssoc> buildMethod;

    private ImmutableList<CSystem> systems = Lists.immutable.empty();
    private ImmutableList<Component> components = Lists.immutable.empty();

    public CEAFactoryBuilderBase(BiFunction<ImmutableList<CSystem>, ImmutableList<Component>, CompEntAssoc> buildMethod) {
        this.buildMethod = buildMethod;
    }

    @Override
    public Builder systems(CSystem... systems) {
        this.systems = Lists.immutable.of(systems);
        return this;
    }

    @Override
    public Builder systems(Iterable<CSystem> systems) {
        this.systems = Lists.immutable.ofAll(systems);
        return this;
    }

    @Override
    public Builder components(Component... components) {
        this.components = Lists.immutable.of(components);
        return this;
    }

    @Override
    public Builder components(Iterable<Component> components) {
        this.components = Lists.immutable.ofAll(components);
        return this;
    }

    @Override
    public CompEntAssoc build() {
        return buildMethod.apply(systems, components);
    }

}
