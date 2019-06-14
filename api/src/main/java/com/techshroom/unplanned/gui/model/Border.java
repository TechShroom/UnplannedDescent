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

package com.techshroom.unplanned.gui.model;

import com.google.auto.value.AutoValue;
import com.techshroom.unplanned.geometry.CornerVector4i;

@AutoValue
public abstract class Border {

    private static final Border ZERO = builder().radii(CornerVector4i.ZERO).autoBuild();

    public static Border zero() {
        return ZERO;
    }

    public static Builder builder() {
        return new AutoValue_Border.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder radii(CornerVector4i radii);

        abstract CornerVector4i getRadii();

        abstract Border autoBuild();

        public final Border build() {
            if (CornerVector4i.ZERO.equals(getRadii())) {
                return ZERO;
            }
            return autoBuild();
        }

    }

    Border() {
    }

    public abstract CornerVector4i getRadii();

}
