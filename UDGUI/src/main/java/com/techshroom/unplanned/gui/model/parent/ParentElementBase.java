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

import com.flowpowered.math.vector.Vector2i;
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
    public GuiElement getElementAt(Vector2i pos) {
        if (!getBounds().contains(pos)) {
            throw new IllegalArgumentException("position out of bounds");
        }
        // TODO optimize maybe? dunno how slow it is...
        Vector2i adjPos = pos.sub(getRelativePosition());
        for (GuiElement element : children) {
            element.validate();
            if (element.getBounds().contains(adjPos)) {
                if (element instanceof ParentElement) {
                    return ((ParentElement) element).getElementAt(adjPos);
                }
                return element;
            }
        }
        return this;
    }

    @Override
    protected void onRevalidation() {
        super.onRevalidation();
        // finalize child state for layout
        for (GuiElement child : children) {
            child.validate();
        }
        layout();
    }

    /**
     * Perform the layout of all the children. Should not perform any special
     * layouts, like for child {@link ParentElement ParentElements}. This is
     * handled in {@link #layout()}.
     */
    protected void layoutChildren() {
    }

    protected void layout() {
        layoutChildren();
    }

}
