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

public enum BaselineResizeBehavior {
    /**
     * Indicates the baseline remains fixed relative to the y-origin. That is,
     * <code>getBaseline</code> returns the same value regardless of the height
     * or width. For example, a <code>Label</code> containing non-empty text
     * with a vertical alignment of <code>TOP</code> should have a baseline type
     * of <code>CONSTANT_ASCENT</code>.
     */
    CONSTANT_ASCENT,

    /**
     * Indicates the baseline remains fixed relative to the height and does not
     * change as the width is varied. That is, for any height H the difference
     * between H and <code>getBaseline(w, H)</code> is the same. For example, a
     * <code>Label</code> containing non-empty text with a vertical alignment of
     * <code>BOTTOM</code> should have a baseline type of
     * <code>CONSTANT_DESCENT</code>.
     */
    CONSTANT_DESCENT,

    /**
     * Indicates the baseline remains a fixed distance from the center of the
     * component. That is, for any height H the difference between
     * <code>getBaseline(w, H)</code> and <code>H / 2</code> is the same (plus
     * or minus one depending upon rounding error).
     * <p>
     * Because of possible rounding errors it is recommended you ask for the
     * baseline with two consecutive heights and use the return value to
     * determine if you need to pad calculations by 1. The following shows how
     * to calculate the baseline for any height:
     * 
     * <pre>
     * Vector2i preferredSize = component.getPreferredSize();
     * int baseline = getBaseline(preferredSize.getX(),
     *         preferredSize.getY());
     * int nextBaseline = getBaseline(preferredSize.getX(),
     *         preferredSize.getY() + 1);
     * // Amount to add to height when calculating where baseline
     * // lands for a particular height:
     * int padding = 0;
     * // Where the baseline is relative to the mid point
     * int baselineOffset = baseline - height / 2;
     * if (preferredSize.getY() % 2 == 0 &amp;&amp;
     *         baseline != nextBaseline) {
     *     padding = 1;
     * } else if (preferredSize.getY() % 2 == 1 &amp;&amp;
     *         baseline == nextBaseline) {
     *     baselineOffset--;
     *     padding = 1;
     * }
     * // The following calculates where the baseline lands for
     * // the height z:
     * int calculatedBaseline = (z + padding) / 2 + baselineOffset;
     * </pre>
     */
    CENTER_OFFSET,

    /**
     * Indicates the baseline resize behavior can not be expressed using any of
     * the other constants. This may also indicate the baseline varies with the
     * width of the component. This is also returned by components that do not
     * have a baseline.
     */
    OTHER
}