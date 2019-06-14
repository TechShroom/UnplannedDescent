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

package com.techshroom.unplanned.gui.model.layout;

import com.techshroom.unplanned.gui.model.GuiElement;
import com.techshroom.unplanned.gui.model.PropertyKey;

public abstract class DataBindingLayout<D> implements Layout {

    private final PropertyKey<D> propertyKey;

    protected DataBindingLayout(String key) {
        propertyKey = PropertyKey.unique(key);
    }

    protected D getInitialValue(GuiElement element) {
        return null;
    }

    @Override
    public void onChildAdded(GuiElement element, int index) {
        if (element.hasProperty(propertyKey)) {
            return;
        }
        D init = getInitialValue(element);
        if (init != null) {
            element.setProperty(propertyKey, init);
        }
    }

    @Override
    public void onChildRemoved(GuiElement element, int index) {
        element.removeProperty(propertyKey);
    }

    public <E extends GuiElement> E bindData(E element, D data) {
        element.setProperty(propertyKey, data);
        return element;
    }

    protected D getData(GuiElement element) {
        return element.getProperty(propertyKey);
    }

    protected PropertyKey<D> getPropertyKey() {
        return propertyKey;
    }

}
