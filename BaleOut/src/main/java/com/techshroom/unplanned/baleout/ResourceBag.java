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
package com.techshroom.unplanned.baleout;

import java.io.InputStream;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.techshroom.unplanned.core.util.IOFunction;
import com.techshroom.unplanned.core.util.IOSupplier;
import com.techshroom.unplanned.rp.RId;

/**
 * Immutable provider of resources.
 */
public class ResourceBag {

    private final Map<RId, IOSupplier<InputStream>> streams;

    public ResourceBag(Map<RId, ? extends IOSupplier<InputStream>> streams) {
        this.streams = ImmutableMap.copyOf(streams);
    }

    public IOFunction<RId, InputStream> asFunction() {
        return i -> streams.get(i).get();
    }

    public Map<RId, IOSupplier<InputStream>> asMap() {
        return streams;
    }

}
