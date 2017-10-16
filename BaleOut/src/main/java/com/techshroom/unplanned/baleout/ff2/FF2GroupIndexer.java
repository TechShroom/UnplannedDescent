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

import static com.google.common.collect.ImmutableList.toImmutableList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.techshroom.unplanned.baleout.Resource;
import com.techshroom.unplanned.rp.RId;
import com.techshroom.unplanned.rp.ff2.FF2Index;
import com.techshroom.unplanned.rp.ff2.FF2Index.Value;

public class FF2GroupIndexer {

    public static FF2Index makeIndexFromGroups(Iterator<CalcGroup> calcGroup, CalculationOptions options) {
        FF2GroupIndexer worker = new FF2GroupIndexer(calcGroup, options);
        worker.addGroupsToIndex();
        return worker.builder.build();
    }

    private final Iterator<CalcGroup> calcGroup;
    private final long maxSize;
    private final FF2Index.Builder builder = FF2Index.builder();
    private List<CalcGroup> parts;
    private byte fileCounter = -1;
    private long currentFileSize;

    private FF2GroupIndexer(Iterator<CalcGroup> calcGroup, CalculationOptions options) {
        this.calcGroup = calcGroup;
        maxSize = options.valueOf(CalculationOption.MAXIMUM_FILE_SIZE);
    }

    private void addGroupsToIndex() {
        resetForNext();
        while (calcGroup.hasNext()) {
            CalcGroup n = calcGroup.next();
            // write if we filled a file
            if (currentFileSize + n.getSize() > maxSize) {
                flushCurrentFile();
            }

            // write next part
            currentFileSize += n.getSize();
            parts.add(n);
        }
        // finish the last file
        flushCurrentFile();
    }

    private void resetForNext() {
        fileCounter++;
        parts = new ArrayList<>();
        currentFileSize = 0;
    }

    private void flushCurrentFile() {
        long offsetCounter = 0;
        for (Entry<RId, Resource> e : parts.stream()
                .flatMap(cg -> cg.getResources().entrySet().stream())
                .collect(toImmutableList())) {
            builder.putValue(e.getKey(), Value.create(fileCounter, offsetCounter, e.getValue().getSize()));
            offsetCounter += e.getValue().getSize();
        }

        resetForNext();
    }

}
