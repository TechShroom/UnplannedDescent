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
package com.techshroom.unplanned.baleout;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import com.techshroom.unplanned.core.util.Exit;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class JOptCommon {

    public static Optional<OptionSet> parse(OptionParser parser, String... args) throws IOException {
        OptionSpec<Void> helpSpec =
                parser.acceptsAll(Arrays.asList("h", "help"), "Displays this help.")
                        .forHelp();
        OptionSet opts;
        try {
            opts = parser.parse(args);
        } catch (OptionException e) {
            System.err.println("Error: " + e.getMessage() + "\n");
            displayHelp(parser);
            throw Exit.with(1);
        }

        if (opts.has(helpSpec)) {
            displayHelp(parser);
            return Optional.empty();
        }

        return Optional.of(opts);
    }

    private static void displayHelp(OptionParser parser) throws IOException {
        System.err.println("Usage:");
        parser.printHelpOn(System.err);
    }

    private JOptCommon() {
    }

}
