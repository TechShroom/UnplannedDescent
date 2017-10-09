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
package com.techshroom.unplanned.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.techshroom.unplanned.core.util.UX;

public class UXTest {
    
    @Test
    public void testUserInputIsTrue() {
        assertTrue(UX.userInputIsTrue("y"));
        assertTrue(UX.userInputIsTrue("yes"));
        assertTrue(UX.userInputIsTrue("true"));
        assertTrue(UX.userInputIsTrue("1"));
        // test case-insensitive
        assertTrue(UX.userInputIsTrue("Y"));
        // test negative cases
        assertFalse(UX.userInputIsTrue("n"));
        assertFalse(UX.userInputIsTrue("no"));
        assertFalse(UX.userInputIsTrue("false"));
        assertFalse(UX.userInputIsTrue("0"));
        assertFalse(UX.userInputIsTrue(""));
        assertFalse(UX.userInputIsTrue("garbage"));
        assertFalse(UX.userInputIsTrue("truth is for me!"));
    }
    
    @Test
    public void testUserInputIsFalse() {
        assertTrue(UX.userInputIsFalse("n"));
        assertTrue(UX.userInputIsFalse("no"));
        assertTrue(UX.userInputIsFalse("false"));
        assertTrue(UX.userInputIsFalse("0"));
        // test case-insensitive
        assertTrue(UX.userInputIsFalse("N"));
        // test negative cases
        assertFalse(UX.userInputIsFalse("y"));
        assertFalse(UX.userInputIsFalse("yes"));
        assertFalse(UX.userInputIsFalse("true"));
        assertFalse(UX.userInputIsFalse("1"));
        assertFalse(UX.userInputIsFalse(""));
        assertFalse(UX.userInputIsFalse("garbage"));
        assertFalse(UX.userInputIsFalse("lies are for you!"));
    }

}
