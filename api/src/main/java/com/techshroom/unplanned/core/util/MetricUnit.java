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

package com.techshroom.unplanned.core.util;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.concurrent.TimeUnit;

/**
 * {@link TimeUnit} for metric. Only used for integers, thus the lack of lower
 * prefixes.
 */
public enum MetricUnit {
    NONE(0),
    DECA(1),
    HECTO(2),
    KILO(3),
    MEGA(6),
    GIGA(9),
    TERA(12),
    PETA(15),
    EXA(18);

    private static long potToMult(int pot) {
        double mult = Math.pow(10, pot);
        checkArgument(mult <= Long.MAX_VALUE);
        return (long) mult;
    }

    public final int powerOfTen;
    public final long multiplier;

    MetricUnit(int powerOfTen) {
        this.powerOfTen = powerOfTen;
        this.multiplier = potToMult(powerOfTen);
    }

    public long convert(long input, MetricUnit target) {
        int potDiff = target.powerOfTen - powerOfTen;
        if (potDiff == 0) {
            return input;
        } else if (potDiff > 0) {
            // target is a larger unit, do division for a smaller value
            return input / potToMult(potDiff);
        } else {
            // target is a smaller unit, do multiplication for a larger value
            return input * potToMult(Math.abs(potDiff));
        }
    }
    
    public long toNone(long input) {
        return convert(input, NONE);
    }
}
