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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.techshroom.unplanned.core.util.UDStrings;

public class StringsTest {

    @Test
    public void testCountEmptyString() throws Exception {
        assertEquals(0, UDStrings.count("", ' '));
    }

    @Test
    public void testCountNotPresent() throws Exception {
        assertEquals(0, UDStrings.count("foo", ' '));
    }

    @Test
    public void testCount() throws Exception {
        assertEquals(1, UDStrings.count(" ", ' '));
    }

    @Test
    public void testNumberToChar() throws Exception {
        for (char c = '0', i = 0; c <= '9' && i <= 9; c++, i++) {
            assertEquals(i, UDStrings.getNumForChar(c));
        }
    }

    @Test
    public void testCharToNumber() throws Exception {
        for (char c = '0', i = 0; c <= '9' && i <= 9; c++, i++) {
            assertEquals(c, UDStrings.getCharForNum((byte) i));
        }
    }

    @Test
    public void testPrettyJoin() throws Exception {
        String join = "~";
        String lastPrefix = "#";
        assertEquals("1", UDStrings.prettyJoin(join, lastPrefix, "1"));
        assertEquals("1 # 2", UDStrings.prettyJoin(join, lastPrefix, "1", "2"));
        assertEquals("1~ 2~ # 3",
                UDStrings.prettyJoin(join, lastPrefix, "1", "2", "3"));
        assertEquals("1~ 2~ 3~ 4~ # 5",
                UDStrings.prettyJoin(join, lastPrefix, "1", "2", "3", "4", "5"));
    }

    @Test
    public void testPrettyJoinAnd() throws Exception {
        assertEquals("1", UDStrings.prettyJoinAnd("1"));
        assertEquals("1 and 2", UDStrings.prettyJoinAnd("1", "2"));
        assertEquals("1, 2, and 3", UDStrings.prettyJoinAnd("1", "2", "3"));
        assertEquals("1, 2, 3, 4, and 5",
                UDStrings.prettyJoinAnd("1", "2", "3", "4", "5"));
    }

    @Test
    public void testPrettyJoinOr() throws Exception {
        assertEquals("1", UDStrings.prettyJoinOr("1"));
        assertEquals("1 or 2", UDStrings.prettyJoinOr("1", "2"));
        assertEquals("1, 2, or 3", UDStrings.prettyJoinOr("1", "2", "3"));
        assertEquals("1, 2, 3, 4, or 5",
                UDStrings.prettyJoinOr("1", "2", "3", "4", "5"));
    }

}
