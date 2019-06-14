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

import static org.junit.Assert.assertArrayEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.techshroom.unplanned.core.util.Arrays2;

/**
 * Test class for Arrays2.
 * 
 * @author Kenzie Togami
 */
public class Arrays2Test {

    private static int[] SPL_original, SPL_normalcp, SPL_reversecp,
            SPL_bigstepcp, SPL_rbigstepcp;

    /**
     * Setup for test.
     * 
     * @throws Exception
     */
    @BeforeClass
    public static void setUp() throws Exception {
        SPL_original = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        SPL_normalcp = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        SPL_reversecp = new int[] { 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 };
        SPL_bigstepcp = new int[] { 0, 2, 4, 6, 8 };
        SPL_rbigstepcp = new int[] { 9, 7, 5, 3, 1 };
    }

    /**
     * Arrays2 can create an array.
     */
    @Test
    public void createsArray() {
        assertArrayEquals(new Object[1], Arrays2.newArray(Object.class, 1));
    }

    /**
     * Arrays2 can create a primitive array.
     */
    @Test
    public void createsPrimitiveArray() {
        assertArrayEquals(new int[1], Arrays2.newPrimitiveArray(int.class, 1));
    }

    /**
     * Arrays2 can splice an array.
     */
    @Test
    public void splicesArray() {
        // check that no in place mods occur
        int[] test, defensiveCopy = SPL_original.clone();
        // splice [:]: copy
        test = Arrays2.splice(SPL_original);
        assertArrayEquals(defensiveCopy, SPL_original);
        assertArrayEquals(SPL_normalcp, test);
        // splice [::-1]: reverse copy
        test = Arrays2.splice(SPL_original, Arrays2.SPLICE_DEFAULT,
                Arrays2.SPLICE_DEFAULT, -1);
        assertArrayEquals(defensiveCopy, SPL_original);
        assertArrayEquals(SPL_reversecp, test);
        // splice [::2]: every other object
        test = Arrays2.splice(SPL_original, Arrays2.SPLICE_DEFAULT,
                Arrays2.SPLICE_DEFAULT, 2);
        assertArrayEquals(defensiveCopy, SPL_original);
        assertArrayEquals(SPL_bigstepcp, test);
        // splice [::-2]: reverse every other object
        test = Arrays2.splice(SPL_original, Arrays2.SPLICE_DEFAULT,
                Arrays2.SPLICE_DEFAULT, -2);
        assertArrayEquals(defensiveCopy, SPL_original);
        assertArrayEquals(SPL_rbigstepcp, test);
    }
}
