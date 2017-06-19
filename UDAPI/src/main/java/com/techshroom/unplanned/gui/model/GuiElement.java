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
package com.techshroom.unplanned.gui.model;

import java.util.OptionalInt;

import javax.annotation.Nullable;

import com.flowpowered.math.vector.Vector2i;
import com.techshroom.unplanned.core.util.Color;
import com.techshroom.unplanned.geometry.SidedVector4i;
import com.techshroom.unplanned.gui.model.parent.ParentElement;

/**
 * Part of a GUI. Stores layout information and model properties. Arbitrary
 * properties associates are made using
 * {@link #setProperty(PropertyKey, Object)}, and can be retrieved with
 * {@link #getProperty(PropertyKey)}.
 */
public interface GuiElement {

    /**
     * Mark the element as in potential need of re-layout. This automatically
     * occurs when layout-related properties are changed.
     */
    void invalidate();

    /**
     * Re-layout this element, if it has been {@link #invalidate() invalidated}.
     */
    void validate();

    boolean isValid();

    // generic properties

    <T> T getProperty(PropertyKey<T> key);

    <T> void setProperty(PropertyKey<T> key, T value);

    // visible
    boolean isVisible();

    void setVisible(boolean visible);

    // relativePosition
    Vector2i getRelativePosition();

    void setRelativePosition(Vector2i pos);

    default void setRelativePosition(int x, int y) {
        setRelativePosition(new Vector2i(x, y));
    }

    // size
    Vector2i getSize();

    void setSize(Vector2i size);

    default void setSize(int width, int height) {
        setSize(new Vector2i(width, height));
    }

    // preferredSize
    Vector2i getPreferredSize();

    void setPreferredSize(Vector2i size);

    default void setPreferredSize(int width, int height) {
        setPreferredSize(new Vector2i(width, height));
    }

    // maxSize
    Vector2i getMaxSize();

    void setMaxSize(Vector2i size);

    default void setMaxSize(int width, int height) {
        setMaxSize(new Vector2i(width, height));
    }

    default void setNoMaxSize() {
        setMaxSize(new Vector2i(Integer.MAX_VALUE, Integer.MAX_VALUE));
    }

    // minSize
    Vector2i getMinSize();

    void setMinSize(Vector2i size);

    default void setMinSize(int width, int height) {
        setMinSize(new Vector2i(width, height));
    }

    default void setNoMinSize() {
        setMinSize(new Vector2i(Integer.MIN_VALUE, Integer.MIN_VALUE));
    }

    // padding
    SidedVector4i getPadding();

    void setPadding(SidedVector4i padding);

    // margin
    SidedVector4i getMargin();

    void setMargin(SidedVector4i margin);

    // insets
    SidedVector4i getInsets();

    void setInsets(SidedVector4i insets);

    // baseline
    OptionalInt getBaseline(int width, int height);

    // baselineResizeBehaviour
    BaselineResizeBehavior getBaselineResizeBehavior();

    // foregroundColor
    Color getForegroundColor();

    void setForegroundColor(Color foregroundColor);

    // backgroundColor
    Color getBackgroundColor();

    void setBackgroundColor(Color backgroundColor);

    // parent
    /**
     * Notifies this element that it is now a child of {@code element}. The
     * parent must contain this element in its
     * {@link ParentElement#getChildren() children}.
     * 
     * @param element
     *            the new parent element
     */
    void setParent(@Nullable ParentElement element);

    @Nullable
    ParentElement getParent();

    /**
     * Calculates the <em>absolute</em> position of this element. This takes the
     * absolute position of the parent and adds this element's relative
     * position.
     * 
     * @return the absolute position of the element
     */
    default Vector2i getAbsolutePosition() {
        ParentElement e = getParent();
        if (e == null) {
            return getRelativePosition();
        }
        return e.getAbsolutePosition().add(getRelativePosition());
    }

}
