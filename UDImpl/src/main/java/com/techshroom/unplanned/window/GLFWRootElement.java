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
package com.techshroom.unplanned.window;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.Iterables;
import com.google.common.eventbus.Subscribe;
import com.techshroom.unplanned.event.window.WindowResizeEvent;
import com.techshroom.unplanned.gui.model.GuiElement;
import com.techshroom.unplanned.gui.model.parent.ParentElementBase;
import com.techshroom.unplanned.gui.model.parent.RootElement;

class GLFWRootElement extends ParentElementBase implements RootElement {

    @Subscribe
    public void onWindowResize(WindowResizeEvent event) {
        setSize(event.getSize());
        setMinSize(event.getSize());
        setMaxSize(event.getSize());
        setPreferredSize(event.getSize());
        // tell children to re-format themselves if needed
        children.forEach(GuiElement::invalidate);
    }

    @Override
    public void setChild(GuiElement child) {
        GuiElement prev = Iterables.getFirst(children, null);
        if (prev != null) {
            prev.setParent(null);
            children.remove(0);
        }
        if (child != null) {
            children.add(child);
            child.setParent(this);
        }
    }

    @Override
    public GuiElement getChild() {
        checkState(children.size() > 0, "no child has been set");
        GuiElement child = children.get(0);
        return child;
    }

}
