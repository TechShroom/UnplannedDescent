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

package com.techshroom.unplanned.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.techshroom.unplanned.core.util.MetricUnit;

public class MetricUnitTest {
    
    @Test
    public void testLoweringAccurracy() throws Exception {
        assertEquals(0, MetricUnit.NONE.convert(1, MetricUnit.DECA));
        assertEquals(1, MetricUnit.NONE.convert(10, MetricUnit.DECA));
        assertEquals(1, MetricUnit.NONE.convert(100, MetricUnit.HECTO));
    }
    
    @Test
    public void testRaisingAccuracy() throws Exception {
        assertEquals(10, MetricUnit.DECA.convert(1, MetricUnit.NONE));
        assertEquals(100, MetricUnit.HECTO.convert(1, MetricUnit.NONE));
        assertEquals(1000, MetricUnit.KILO.convert(1, MetricUnit.NONE));
    }
    
    @Test
    public void testNoConversion() throws Exception {
        assertEquals(0, MetricUnit.NONE.convert(0, MetricUnit.NONE));
        assertEquals(5, MetricUnit.NONE.convert(5, MetricUnit.NONE));
        assertEquals(1000, MetricUnit.NONE.convert(1000, MetricUnit.NONE));
    }

}
