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
package com.techshroom.unplanned.rp;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Iterator;
import java.util.List;

import com.google.auto.value.AutoValue;
import com.google.common.base.Splitter;

/**
 * Resource identifier.
 * 
 * <p>
 * Format: {@code $DOMAIN:$CATEGORY/$IDENTIFIER}. No variable may contain a ':',
 * but {@code CATEGORY} may contain '/'.
 * </p>
 */
@AutoValue
public abstract class RId {

    private static final String PARSE_NO_DOMAIN = "ErrorParseNoDomain";

    public static RId parse(String rId) {
        RId parsed = parse(rId, PARSE_NO_DOMAIN);
        checkArgument(!parsed.getDomain().equals(PARSE_NO_DOMAIN), "rId must contain an domain if no default provided");
        return parsed;
    }

    private static final Splitter COLON = Splitter.on(':');
    private static final Splitter SLASH = Splitter.on('/');

    public static RId parse(String rId, String defaultDomain) {
        String domain;
        String category;
        String identifier;
        if (rId.contains(":")) {
            Iterator<String> parts = COLON.split(rId).iterator();
            domain = parts.next();
            rId = parts.next();
            checkArgument(!parts.hasNext(), "rId is not allowed to have multiple colons");
        } else {
            domain = defaultDomain;
        }
        List<String> parts = SLASH.splitToList(rId);
        checkArgument(parts.size() > 1, "rId must contain a category");
        identifier = parts.get(parts.size() - 1);
        category = String.join("/", parts.subList(0, parts.size() - 1));
        return from(domain, category, identifier);
    }

    public static RId from(String domain, String category, String identifier) {
        checkArgument(!domain.contains(":"), "domain cannot contain a colon");
        checkArgument(!category.contains(":"), "category cannot contain a colon");
        checkArgument(!identifier.contains(":"), "identifier cannot contain a colon");
        return new AutoValue_RId(domain, category, identifier);
    }

    RId() {
    }

    public abstract String getDomain();

    public abstract String getCategory();

    public abstract String getIdentifier();
    
    @Override
    public final String toString() {
        return getDomain() + ":" + getCategory() + "/" + getIdentifier();
    }

}
