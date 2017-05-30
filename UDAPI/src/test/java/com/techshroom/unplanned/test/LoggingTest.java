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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.Test;

import com.techshroom.unplanned.core.util.LUtils;
import com.techshroom.unplanned.core.util.Logging;
import com.techshroom.unplanned.core.util.Logging.LoggingGroup;

public class LoggingTest {

    /**
     * Logging can limit the printing to certain groups.
     */
    @Test
    public void limitsGroups() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // fool LUtils into replacing the wrong streams so our life is easy.
        LUtils.init();
        PrintStream original = System.err;
        System.setErr(new PrintStream(out));
        for (Iterator<LoggingGroup> itr = LoggingGroup.ALL.iterator(); itr
                .hasNext();) {
            LoggingGroup g = itr.next();
            Logging.setValidGroups(g);
            Logging.log("YES", g);
            Set<LoggingGroup> groups = LoggingGroup.ALL.stream()
                    .filter(Predicate.isEqual(g).negate())
                    .collect(Collectors.toSet());
            for (LoggingGroup nonGroup : groups) {
                Logging.log("NO", nonGroup);
            }
        }
        // ensure all written
        System.err.flush();
        System.setErr(original);
        String str = out.toString();
        assertEquals("Logged wrong group: ", str, str.replace("NO", ""));
    }

}
