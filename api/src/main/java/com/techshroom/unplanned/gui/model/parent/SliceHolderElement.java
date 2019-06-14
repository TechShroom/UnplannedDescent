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

package com.techshroom.unplanned.gui.model.parent;

import static com.google.common.base.Preconditions.checkElementIndex;

import com.flowpowered.math.vector.Vector2i;
import com.techshroom.unplanned.gui.model.layout.Layout;

/**
 * Holds slices ({@link Panel Panels}) that are painted on top of eachother. The
 * slices should not be moved or re-sized, as they are locked to the parent size
 * and location.
 * 
 * <p>
 * Note: Padding and margin, which are layout-related properties, have no effect
 * on the slices.
 * </p>
 */
public class SliceHolderElement extends GroupElementBase<Layout> {

    private static final Layout SLICE_LAYOUT = element -> {
        element.getChildren().forEach(c -> {
            c.setSize(element.getSize());
            c.setRelativePosition(Vector2i.ZERO);
        });
    };
    
    @Override
    protected Layout initalLayout() {
        return SLICE_LAYOUT;
    }

    public Panel addSlice() {
        Panel panel = new Panel();
        children.add(panel);
        return panel;
    }

    public Panel getSlice(int index) {
        checkElementIndex(index, children.size(), "panel index");
        return (Panel) children.get(index);
    }

    // removeSlice is not offered because it would make tracking index tricky
    public void cleanSlices() {
        children.clear();
    }

}
