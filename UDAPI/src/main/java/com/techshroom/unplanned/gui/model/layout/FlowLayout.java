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
package com.techshroom.unplanned.gui.model.layout;

import java.util.Locale;
import java.util.OptionalInt;

import com.flowpowered.math.vector.Vector2i;
import com.techshroom.unplanned.geometry.SidedVector4i;
import com.techshroom.unplanned.gui.model.GuiElement;
import com.techshroom.unplanned.gui.model.parent.GroupElement;

public class FlowLayout implements Layout {

    public enum Alignment {
        /**
         * This value indicates that each row of elements should be
         * left-justified.
         */
        LEFT,

        /**
         * This value indicates that each row of elements should be centered.
         */
        CENTER,

        /**
         * This value indicates that each row of elements should be
         * right-justified.
         */
        RIGHT
    }

    private Alignment align;

    private int hgap;

    private int vgap;

    /**
     * If true, components will be aligned on their baseline.
     */
    private boolean alignOnBaseline;

    /**
     * Constructs a new <code>FlowLayout</code> with a centered alignment and a
     * default 5-unit horizontal and vertical gap.
     */
    public FlowLayout() {
        this(Alignment.CENTER, 5, 5);
    }

    /**
     * Constructs a new <code>FlowLayout</code> with the specified alignment and
     * a default 5-unit horizontal and vertical gap.
     * 
     * @param align
     *            the alignment value
     */
    public FlowLayout(Alignment align) {
        this(align, 5, 5);
    }

    /**
     * Creates a new flow layout manager with the indicated alignment and the
     * indicated horizontal and vertical gaps.
     * 
     * @param align
     *            the alignment value
     * @param hgap
     *            the horizontal gap between elements and between the elements
     *            and the borders of the <code>GroupElement</code>
     * @param vgap
     *            the vertical gap between elements and between the elements and
     *            the borders of the <code>GroupElement</code>
     */
    public FlowLayout(Alignment align, int hgap, int vgap) {
        this.align = align;
        this.hgap = hgap;
        this.vgap = vgap;
    }

    /**
     * Gets the alignment for this layout.
     * 
     * @return the alignment value for this layout
     */
    public Alignment getAlignment() {
        return align;
    }

    /**
     * Sets the alignment for this layout.
     * 
     * @param align
     *            the alignment
     */
    public void setAlignment(Alignment align) {
        this.align = align;
    }

    /**
     * Gets the horizontal gap between components and between the components and
     * the borders of the <code>GroupElement</code>
     *
     * @return the horizontal gap between components and between the components
     *         and the borders of the <code>GroupElement</code>
     */
    public int getHgap() {
        return hgap;
    }

    /**
     * Sets the horizontal gap between components and between the components and
     * the borders of the <code>GroupElement</code>.
     *
     * @param hgap
     *            the horizontal gap between components and between the
     *            components and the borders of the <code>GroupElement</code>
     */
    public void setHgap(int hgap) {
        this.hgap = hgap;
    }

    /**
     * Gets the vertical gap between components and between the components and
     * the borders of the <code>GroupElement</code>.
     *
     * @return the vertical gap between components and between the components
     *         and the borders of the <code>GroupElement</code>
     */
    public int getVgap() {
        return vgap;
    }

    /**
     * Sets the vertical gap between components and between the components and
     * the borders of the <code>GroupElement</code>.
     *
     * @param vgap
     *            the vertical gap between components and between the components
     *            and the borders of the <code>GroupElement</code>
     */
    public void setVgap(int vgap) {
        this.vgap = vgap;
    }

    /**
     * Sets whether or not components should be vertically aligned along their
     * baseline. Components that do not have a baseline will be centered. The
     * default is false.
     *
     * @param alignOnBaseline
     *            whether or not components should be vertically aligned on
     *            their baseline
     */
    public void setAlignOnBaseline(boolean alignOnBaseline) {
        this.alignOnBaseline = alignOnBaseline;
    }

    /**
     * Returns true if components are to be vertically aligned along their
     * baseline. The default is false.
     *
     * @return true if components are to be vertically aligned along their
     *         baseline
     */
    public boolean getAlignOnBaseline() {
        return alignOnBaseline;
    }

    /**
     * Centers the elements in the specified row, if there is any slack.
     * 
     * @param target
     *            the component which needs to be moved
     * @param x
     *            the x coordinate
     * @param y
     *            the y coordinate
     * @param width
     *            the width dimensions
     * @param height
     *            the height dimensions
     * @param rowStart
     *            the beginning of the row
     * @param rowEnd
     *            the the ending of the row
     * @param useBaseline
     *            Whether or not to align on baseline.
     * @param ascent
     *            Ascent for the components. This is only valid if useBaseline
     *            is true.
     * @param descent
     *            Ascent for the components. This is only valid if useBaseline
     *            is true.
     * @return actual row height
     */
    private int moveComponents(GroupElement target, int x, int y, int width, int height,
            int rowStart, int rowEnd, boolean ltr,
            boolean useBaseline, int[] ascent,
            int[] descent) {
        switch (align) {
            case LEFT:
                x += ltr ? 0 : width;
                break;
            case CENTER:
                x += width / 2;
                break;
            case RIGHT:
                x += ltr ? width : 0;
                break;
            default:
        }
        int maxAscent = 0;
        int nonbaselineHeight = 0;
        int baselineOffset = 0;
        if (useBaseline) {
            int maxDescent = 0;
            for (int i = rowStart; i < rowEnd; i++) {
                GuiElement m = target.getChildren().get(i);
                if (m.isVisible()) {
                    if (ascent[i] >= 0) {
                        maxAscent = Math.max(maxAscent, ascent[i]);
                        maxDescent = Math.max(maxDescent, descent[i]);
                    } else {
                        nonbaselineHeight = Math.max(m.getSize().getY(),
                                nonbaselineHeight);
                    }
                }
            }
            height = Math.max(maxAscent + maxDescent, nonbaselineHeight);
            baselineOffset = (height - maxAscent - maxDescent) / 2;
        }
        for (int i = rowStart; i < rowEnd; i++) {
            GuiElement m = target.getChildren().get(i);
            if (m.isVisible()) {
                int cy;
                if (useBaseline && ascent[i] >= 0) {
                    cy = y + baselineOffset + maxAscent - ascent[i];
                } else {
                    cy = y + (height - m.getSize().getY()) / 2;
                }
                if (ltr) {
                    m.setRelativePosition(x, cy);
                } else {
                    m.setRelativePosition(target.getSize().getX() - x - m.getSize().getX(), cy);
                }
                x += m.getSize().getX() + hgap;
            }
        }
        return height;
    }

    /**
     * Lays out the element. This method lets each <i>visible</i> element take
     * its preferred size by reshaping the elements in the target element in
     * order to satisfy the alignment of this <code>FlowLayout</code> object.
     *
     * @param target
     *            the specified element being laid out
     */
    @Override
    public void layout(GroupElement target) {
        SidedVector4i insets = target.getInsets();
        Vector2i size = target.getSize();
        int maxwidth = size.getX() - (insets.getLeft() + insets.getRight() + hgap * 2);
        int nmembers = target.getChildren().size();
        int x = 0;
        int y = insets.getTop() + vgap;
        int rowh = 0;
        int start = 0;

        boolean ltr = true;

        boolean useBaseline = getAlignOnBaseline();
        int[] ascent = null;
        int[] descent = null;

        if (useBaseline) {
            ascent = new int[nmembers];
            descent = new int[nmembers];
        }

        for (int i = 0; i < nmembers; i++) {
            GuiElement m = target.getChildren().get(i);
            if (m.isVisible()) {
                Vector2i d = m.getPreferredSize();
                m.setSize(d);

                if (useBaseline) {
                    OptionalInt baseline = m.getBaseline(d.getX(), d.getY());
                    if (baseline.isPresent()) {
                        int base = baseline.getAsInt();
                        ascent[i] = base;
                        descent[i] = d.getY() - base;
                    } else {
                        ascent[i] = -1;
                    }
                }
                if ((x == 0) || ((x + d.getX()) <= maxwidth)) {
                    if (x > 0) {
                        x += hgap;
                    }
                    x += d.getX();
                    rowh = Math.max(rowh, d.getY());
                } else {
                    rowh = moveComponents(target, insets.getLeft() + hgap, y,
                            maxwidth - x, rowh, start, i, ltr,
                            useBaseline, ascent, descent);
                    x = d.getX();
                    y += vgap + rowh;
                    rowh = d.getY();
                    start = i;
                }
            }
        }
        moveComponents(target, insets.getLeft() + hgap, y, maxwidth - x, rowh,
                start, nmembers, ltr, useBaseline, ascent, descent);
    }

    /**
     * Returns a string representation of this <code>FlowLayout</code> object
     * and its values.
     * 
     * @return a string representation of this layout
     */
    @Override
    public String toString() {
        return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + ",align=" + align.toString().toLowerCase(Locale.ENGLISH) + "]";
    }

}
