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
import com.google.common.eventbus.EventBus;
import com.techshroom.unplanned.core.util.Color;
import com.techshroom.unplanned.geometry.SidedVector4i;
import com.techshroom.unplanned.geometry.WHRectangleI;
import com.techshroom.unplanned.gui.model.SizeValue.SVType;
import com.techshroom.unplanned.gui.model.parent.ParentElement;

/**
 * Part of a GUI. Stores layout information and model properties. Arbitrary
 * properties associates are made using
 * {@link #setProperty(PropertyKey, Object)}, and can be retrieved with
 * {@link #getProperty(PropertyKey)}.
 */
public interface GuiElement {

    EventBus getEventBus();

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

    boolean hasProperty(PropertyKey<?> key);

    <T> T getProperty(PropertyKey<T> key);

    <T> void setProperty(PropertyKey<T> key, T value);

    void removeProperty(PropertyKey<?> key);

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
    // this function is mostly internal
    boolean hasSize();

    Vector2i getSize();

    void setSize(@Nullable Vector2i size);

    default void setSize(int width, int height) {
        setSize(Vector2i.from(width, height));
    }

    // preferredSize
    Size<SizeValue> getPreferredSize();

    void setPreferredSize(Size<SizeValue> size);

    default void setPreferredSize(Vector2i size) {
        setPreferredSize(size.getX(), size.getY());
    }

    default void setPreferredSize(int width, int height) {
        setPreferredSize(GuiAssist.sizeFrom(width, height));
    }

    // maxSize
    Size<SizeValue> getMaxSize();

    void setMaxSize(Size<SizeValue> size);

    default void setMaxSize(Vector2i size) {
        setMaxSize(size.getX(), size.getY());
    }

    default void setMaxSize(int width, int height) {
        setMaxSize(GuiAssist.sizeFrom(width, height));
    }

    default void setNoMaxSize() {
        setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    // minSize
    Size<SizeValue> getMinSize();

    void setMinSize(Size<SizeValue> size);

    default void setMinSize(Vector2i size) {
        setMinSize(size.getX(), size.getY());
    }

    default void setMinSize(int width, int height) {
        setMinSize(GuiAssist.sizeFrom(width, height));
    }

    default void setNoMinSize() {
        setMinSize(Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    // padding
    SidedVector4i getPadding();

    void setPadding(SidedVector4i padding);

    default Vector2i getSizeWithPadding() {
        return getSize().add(getPadding().getAsWidthHeight());
    }

    default Vector2i getPreferredSizeWithPadding() {
        return solidifySize(getPreferredSize()).add(getPadding().getAsWidthHeight());
    }

    // margin
    SidedVector4i getMargin();

    void setMargin(SidedVector4i margin);

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

    // border

    Border getBorder();

    void setBorder(Border border);

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

    default WHRectangleI getBounds() {
        Vector2i sizePad = getSizeWithPadding();
        Vector2i pos = getRelativePosition();
        return WHRectangleI.of(pos.getX(), pos.getY(), sizePad.getX(), sizePad.getY());
    }

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

    /**
     * Calculates the generic layout size. This is the {@link #getSize() size}
     * if set, otherwise it is the preferred size.
     * 
     * @return the layout size
     */
    default Size<SizeValue> getLayoutSize() {
        Size<SizeValue> pref = getPreferredSize();
        if (!hasSize()) {
            return pref;
        }

        // prefer preferred if it uses percentages
        if (pref.width().type() == SVType.PERCENT || pref.height().type() == SVType.PERCENT) {
            return pref;
        }

        return GuiAssist.sizeFrom(getSize());
    }

    default Vector2i solidifySize(Size<SizeValue> size) {
        ParentElement parent = getParent();
        Vector2i parentSize = parent == null || !parent.hasSize() ? Vector2i.from(Integer.MAX_VALUE)
                : parent.getSize();
        int computedX = size.width().computeInteger(parentSize.getX(), parentSize.getY());
        int computedY = size.height().computeInteger(parentSize.getY(), parentSize.getX());
        return Vector2i.from(computedX, computedY);
    }

}
