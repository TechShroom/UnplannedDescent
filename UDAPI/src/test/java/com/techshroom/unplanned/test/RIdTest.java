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

import com.techshroom.unplanned.rp.RId;

public class RIdTest {

    @Test
    public void testRIdParse() {
        testRIdParseHelper("domain", "category", "identifier");
        testRIdParseHelper("domain", "pathed/category", "identifier");
        testRIdParseHelper("%CR4ZY&DOM11N!)({}[]", "aKw-=_13451!#@#Cat", "..,11?.,123ID");
    }

    private void testRIdParseHelper(String domain, String cat, String ident) {
        RId id = RId.parse(domain + ":" + cat + "/" + ident);
        assertEquals(domain, id.getDomain());
        assertEquals(cat, id.getCategory());
        assertEquals(ident, id.getIdentifier());
    }

    @Test
    public void testRIdParseFailures() throws Exception {
        testRIdParseFailHelper(":domain", "category", "ident");
        testRIdParseFailHelper("domain:", "category", "ident");
        testRIdParseFailHelper("domain", ":category", "ident");
        testRIdParseFailHelper("domain", "category:", "ident");
        testRIdParseFailHelper("domain", "category", ":ident");
        testRIdParseFailHelper("domain", "category", "ident:");

        assertRIdFail(() -> RId.parse("domain:this_is_ident_no_category"));
        assertRIdFail(() -> RId.parse("no_domain/for_you"));
    }

    @Test
    public void testRIdParseDefault() throws Exception {
        RId id = RId.parse("foo/bar", "zed");
        assertEquals("zed:foo/bar", id.toString());
    }

    private void testRIdParseFailHelper(String domain, String cat, String ident) {
        assertRIdFail(() -> RId.parse(domain + ":" + cat + "/" + ident));
    }

    @Test
    public void testColonDisallowed() throws Exception {
        assertRIdFail(() -> RId.from("colon:domain", "cat", "ident"));
        assertRIdFail(() -> RId.from("domain", "colon:cat", "ident"));
        assertRIdFail(() -> RId.from("domain", "cat", "colon:ident"));
    }

    private void assertRIdFail(Runnable action) {
        try {
            action.run();
            fail("Created RID!");
        } catch (IllegalArgumentException e) {
            // ok!
        }
    }

}
