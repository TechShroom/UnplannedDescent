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

import com.techshroom.unplanned.gui.model.GuiAssist;
import com.techshroom.unplanned.gui.model.GuiElement;
import com.techshroom.unplanned.gui.model.Size;
import com.techshroom.unplanned.gui.model.SizeValue;
import com.techshroom.unplanned.gui.model.layout.Layout;

public abstract class GroupElementBase<L extends Layout> extends ParentElementBase implements GroupElement<L> {

    private boolean needsLayout = true;
    private boolean preferredSizeFromLayout = true;
    private L layout = initalLayout();

    protected abstract L initalLayout();

    @Override
    public void markLayoutDirty() {
        needsLayout = true;
    }

    @Override
    public void layoutIfNeeded() {
        if (needsLayout) {
            layout();
            needsLayout = false;
        }
    }

    @Override
    protected void onRevalidation() {
        if (preferredSizeFromLayout) {
            super.setPreferredSize(GuiAssist.sizeFrom(layout.computePreferredSize(this)));
        }
        super.onRevalidation();
    }

    @Override
    public L getLayout() {
        return this.layout;
    }

    @Override
    protected void layoutChildren() {
        layout.layout(this);
        // update children if needed
        for (GuiElement c : children) {
            if (c instanceof GroupElement) {
                ((GroupElement<?>) c).markLayoutDirty();
                ((GroupElement<?>) c).layoutIfNeeded();
            }
        }
    }

    protected void internalSetLayout(L layout) {
        for (int i = 0; i < children.size(); i++) {
            this.layout.onChildRemoved(children.get(i), i);
        }

        this.layout = layout;

        for (int i = 0; i < children.size(); i++) {
            this.layout.onChildAdded(children.get(i), i);
        }
        invalidate();
    }

    @Override
    public void addChild(GuiElement element) {
        children.add(element);
        layout.onChildAdded(element, children.size() - 1);
        invalidate();
    }

    @Override
    public void removeChild(GuiElement element) {
        int index = children.indexOf(element);
        children.remove(index);
        layout.onChildRemoved(element, index);
        invalidate();
    }

    @Override
    public void setPreferredSize(Size<SizeValue> size) {
        preferredSizeFromLayout = false;
        super.setPreferredSize(size);
    }

}
