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
package com.techshroom.unplanned.gui.model.parent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.techshroom.unplanned.gui.model.GuiElement;
import com.techshroom.unplanned.gui.model.GuiElementBase;

public class ParentElementBase extends GuiElementBase implements ParentElement {

    protected final List<GuiElement> children = new ArrayList<>();
    private final List<GuiElement> childrenView = Collections.unmodifiableList(children);

    @Override
    public List<GuiElement> getChildren() {
        return childrenView;
    }
    
    @Override
    protected void onRevalidation() {
        super.onRevalidation();
        layout();
    }

    /**
     * Perform the layout of all the children. Should not perform any special
     * layouts, like for child {@link ParentElement ParentElements}. This is
     * handled in {@link #layout()}.
     */
    protected void layoutChildren() {
    }

    private void layout() {
        for (GuiElement child : children) {
            if (child instanceof ParentElement) {
                ((ParentElement) child).validate();
            }
        }
        layoutChildren();
    }

}
