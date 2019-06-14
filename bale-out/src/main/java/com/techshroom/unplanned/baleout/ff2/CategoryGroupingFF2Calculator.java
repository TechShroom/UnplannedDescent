/*
 * This file is part of unplanned-descent, licensed under the MIT License (MIT).
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

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.techshroom.unplanned.baleout.Resource;
import com.techshroom.unplanned.rp.RId;
import com.techshroom.unplanned.rp.ff2.FF2Index;

/**
 * {@link FF2Calculator} that attempts to group resources according to the
 * domain and category of their ID.
 */
public class CategoryGroupingFF2Calculator implements FF2Calculator {

    private static String key(RId id) {
        return id.getDomain() + ":" + id.getCategory();
    }

    @Override
    public FF2Index calculate(Map<RId, Path> resources, CalculationOptions options) throws IOException {
        Map<String, CalcGroup.Builder> groups = new HashMap<>();
        for (Entry<RId, Path> p : resources.entrySet()) {
            CalcGroup.Builder b = groups.computeIfAbsent(key(p.getKey()), k -> CalcGroup.builder());
            b.addResource(Resource.fromPath(p.getKey(), p.getValue()));
        }
        return FF2GroupIndexer.makeIndexFromGroups(groups.values().stream().map(b -> b.build()).iterator(), options);
    }

}
