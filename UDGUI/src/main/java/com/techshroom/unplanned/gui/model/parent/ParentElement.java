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

import java.util.List;

import com.flowpowered.math.vector.Vector2i;
import com.techshroom.unplanned.gui.model.GuiElement;

/**
 * Parent of more {@link GuiElement GuiElements}.
 */
public interface ParentElement extends GuiElement {

    /**
     * An unmodifiable list of children. It may be changed by the element itself
     * via other methods.
     * 
     * @return a view into the children of this element
     */
    List<GuiElement> getChildren();

    /**
     * Finds the inner-most element at {@code pos}. This will recurse into other
     * parent elements.
     * 
     * @param pos
     *            the position to find an element at
     * @return the element found, may be this element
     * @throws IllegalArgumentException
     *             if the position is outside of the bounds of this parent
     */
    GuiElement getElementAt(Vector2i pos);

}
