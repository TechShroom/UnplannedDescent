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

import com.techshroom.unplanned.core.util.CombinedId;

public class CombinedIdTest {

    @Test
    public void testFromTitle() throws Exception {
        CombinedId id = CombinedId.fromTitle("TitleArbitrary");
        assertEquals("TitleArbitrary", id.titleCase());
        assertEquals("titleArbitrary", id.camelCase());
        assertEquals("title.arbitrary", id.dotSeparated());
        assertEquals("title_arbitrary", id.underscoreSeparated());

        id = CombinedId.fromTitle("Fickle");
        assertEquals("Fickle", id.titleCase());
        assertEquals("fickle", id.camelCase());
        assertEquals("fickle", id.dotSeparated());
        assertEquals("fickle", id.underscoreSeparated());

        id = CombinedId.fromTitle("ABC");
        assertEquals("ABC", id.titleCase());
        assertEquals("aBC", id.camelCase());
        assertEquals("abc", id.dotSeparated());
        assertEquals("abc", id.underscoreSeparated());

        id = CombinedId.fromTitle("NowYouKnowMyIDs");
        assertEquals("NowYouKnowMyIDs", id.titleCase());
        assertEquals("nowYouKnowMyIDs", id.camelCase());
        assertEquals("now.you.know.my.ids", id.dotSeparated());
        assertEquals("now_you_know_my_ids", id.underscoreSeparated());
    }

}
