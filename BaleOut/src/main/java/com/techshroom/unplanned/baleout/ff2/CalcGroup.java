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
package com.techshroom.unplanned.baleout.ff2;

import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;
import com.google.common.collect.ImmutableMap;
import com.techshroom.unplanned.baleout.Resource;
import com.techshroom.unplanned.rp.RId;

/**
 * Helper class for {@link FF2Calculator} -- holds a group of resources.
 */
@AutoValue
public abstract class CalcGroup {

    public static Builder builder() {
        return new AutoValue_CalcGroup.Builder();
    }

    @AutoValue.Builder
    public interface Builder {

        ImmutableMap.Builder<RId, Resource> resourcesBuilder();
        
        default Builder addResource(Resource resource) {
            resourcesBuilder().put(resource.getId(), resource);
            return this;
        }

        CalcGroup build();

    }

    CalcGroup() {
    }

    public abstract ImmutableMap<RId, Resource> getResources();

    @Memoized
    public long getSize() {
        return getResources().values().stream().mapToLong(Resource::getSize).sum();
    }

}
