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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

import javax.annotation.Nullable;

import com.flowpowered.math.vector.Vector2i;
import com.techshroom.unplanned.core.util.Color;
import com.techshroom.unplanned.geometry.SidedVector4i;
import com.techshroom.unplanned.gui.model.parent.ParentElement;

public class GuiElementBase implements GuiElement, GuiElementInternal {

    private final Map<String, Object> properties = new HashMap<>();
    private boolean visible = true;
    private Vector2i pos = Vector2i.ZERO;
    private SidedVector4i padding = SidedVector4i.ZERO;
    private SidedVector4i margin = SidedVector4i.ZERO;
    private SidedVector4i insets = SidedVector4i.ZERO;
    @Nullable
    private Vector2i size;
    private Vector2i minSize = Vector2i.ZERO;
    private Vector2i maxSize = Vector2i.ZERO;
    private Vector2i preferredSize = Vector2i.ZERO;
    private Color foregroundColor = Color.BLACK;
    private Color backgroundColor = Color.GRAY;
    @Nullable
    private ParentElement parent;
    // valid is false to start out invalidated
    // we also set invalidatedSinceLastDrawNotification to true
    private boolean valid;
    private boolean invalidatedSinceLastDrawNotification = true;

    @Override
    public void invalidate() {
        valid = false;
        invalidatedSinceLastDrawNotification = true;
        if (parent != null) {
            parent.invalidate();
        }
    }

    @Override
    public void validate() {
        if (!valid) {
            onRevalidation();
            valid = true;
        }
    }

    protected void onRevalidation() {
        if (size == null) {
            size = preferredSize;
        }
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public boolean internalInvalidatedSinceLastDrawNotification() {
        return invalidatedSinceLastDrawNotification;
    }

    @Override
    public void internalDrawNotification() {
        invalidatedSinceLastDrawNotification = false;
    }

    @Override
    public <T> T getProperty(PropertyKey<T> key) {
        // assumed OK, could cause ClassCastEx later
        @SuppressWarnings("unchecked")
        T t = (T) properties.get(key.getKey());
        return t;
    }

    @Override
    public <T> void setProperty(PropertyKey<T> key, T value) {
        properties.put(key.getKey(), value);
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
        invalidate();
    }

    @Override
    public Vector2i getRelativePosition() {
        return pos;
    }

    @Override
    public void setRelativePosition(Vector2i pos) {
        this.pos = pos;
        invalidate();
    }

    @Override
    public SidedVector4i getPadding() {
        return padding;
    }

    @Override
    public void setPadding(SidedVector4i padding) {
        this.padding = padding;
        invalidate();
    }

    @Override
    public SidedVector4i getMargin() {
        return margin;
    }

    @Override
    public void setMargin(SidedVector4i margin) {
        this.margin = margin;
        invalidate();
    }

    @Override
    public SidedVector4i getInsets() {
        return insets;
    }

    @Override
    public void setInsets(SidedVector4i insets) {
        this.insets = insets;
        invalidate();
    }

    @Override
    public Vector2i getSize() {
        return checkNotNull(size, "invalid state, please validate before calling size");
    }

    @Override
    public void setSize(Vector2i size) {
        this.size = size;
        invalidate();
    }

    @Override
    public Vector2i getPreferredSize() {
        return preferredSize;
    }

    @Override
    public void setPreferredSize(Vector2i size) {
        // set size to null if it was equal to the previous preferred
        // this keeps the size <-> preferredSize ties
        if (this.size == this.preferredSize) {
            this.size = null;
        }
        this.preferredSize = size;
        invalidate();
    }

    @Override
    public Vector2i getMinSize() {
        return minSize;
    }

    @Override
    public void setMinSize(Vector2i minSize) {
        this.minSize = minSize;
        invalidate();
    }

    @Override
    public Vector2i getMaxSize() {
        return maxSize;
    }

    @Override
    public void setMaxSize(Vector2i maxSize) {
        this.maxSize = maxSize;
        invalidate();
    }

    @Override
    public OptionalInt getBaseline(int width, int height) {
        return OptionalInt.empty();
    }

    @Override
    public BaselineResizeBehavior getBaselineResizeBehavior() {
        return BaselineResizeBehavior.OTHER;
    }

    @Override
    public Color getForegroundColor() {
        return foregroundColor;
    }

    @Override
    public void setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    @Override
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public void setParent(@Nullable ParentElement element) {
        checkArgument(element.getChildren().contains(this), "parent does not contain this element");
        this.parent = element;
    }

    @Override
    @Nullable
    public ParentElement getParent() {
        return parent;
    }

}
