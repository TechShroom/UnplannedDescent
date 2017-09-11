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
package com.techshroom.unplanned.gui.model.layout;

import com.google.auto.value.AutoValue;
import com.techshroom.unplanned.gui.model.GuiElement;
import com.techshroom.unplanned.gui.model.PropertyKey;
import com.techshroom.unplanned.gui.model.parent.GroupElement;

public class FlexLayout implements Layout {

    @AutoValue
    public abstract static class FlexData {

        @AutoValue.Builder
        public interface Builder {

            FlexData build();

            default <E extends GuiElement> E addTo(E element) {
                return addFlexData(element, build());
            }

        }

        FlexData() {
        }

    }

    private static final PropertyKey<FlexData> PROP_FLEX_DATA = PropertyKey.of("flexLayoutFlexData");

    public static <E extends GuiElement> E addFlexData(E element, FlexData flexData) {
        element.setProperty(PROP_FLEX_DATA, flexData);
        return element;
    }

    @Override
    public void layout(GroupElement element) {
    }

}
