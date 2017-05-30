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
package com.techshroom.unplanned.core.mod;

import java.util.stream.Collectors;

import com.google.common.collect.Multimap;
import com.techshroom.unplanned.core.util.UDStrings;

/**
 * Thrown when two or more mods have the same ID.
 */
public class ModIDConflictException extends RuntimeException {

    private static final long serialVersionUID = 8008495944986210861L;

    private static String
            generateConflictMessage(Multimap<String, ModMetadata> conflicts) {
        return conflicts.asMap().values().stream()
                .map(v -> "Mods " + UDStrings.prettyJoinAnd(v))
                .collect(Collectors.joining(", ", "Conflicts: ", ""));
    }

    private final Multimap<String, ModMetadata> conflictingIds;

    public ModIDConflictException(Multimap<String, ModMetadata> conflicts) {
        super("Mod ID conflict: " + generateConflictMessage(conflicts));
        this.conflictingIds = conflicts;
    }

    public Multimap<String, ModMetadata> getConflictingIds() {
        return this.conflictingIds;
    }

}
