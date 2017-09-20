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

import com.flowpowered.math.vector.Vector2i;
import com.techshroom.unplanned.gui.model.GuiElement;
import com.techshroom.unplanned.gui.model.parent.GroupElement;

import javafx.scene.layout.Priority;

/**
 * Base layout for HBox and VBox.
 */
abstract class XBoxLayout extends DataBindingLayout<Priority> {

    private final double spacing;
    private final boolean fill;
    private final Alignment align;

    protected XBoxLayout(String key, double spacing, boolean fill, Alignment align) {
        super(key);
        this.spacing = spacing;
        this.fill = fill;
        this.align = align;
    }

    @Override
    public Vector2i computePreferredSize(GroupElement<?> element) {
        int mainSize = 0;
        int crossSize = 0;
        for (GuiElement child : element.getChildren()) {
            Vector2i prefPadMar = child.getPreferredSizeWithPadding().add(child.getMargin().getAsWidthHeight());
            mainSize += extractComponent(prefPadMar);
            crossSize = Math.max(crossSize, extractComponentCross(prefPadMar));
        }
        return setComponent(setComponentCross(Vector2i.ZERO, crossSize), mainSize);
    }

    @Override
    public void layout(GroupElement<?> element) {
        // sizes[] does not include spacing!
        double[] sizes = new double[element.getChildren().size()];
        double remaining = extractComponent(element.getSize());
        remaining = initialSizeElements(element, remaining, sizes);
        remaining = expandElements(Priority.ALWAYS, element, remaining, sizes);
        remaining = expandElements(Priority.SOMETIMES, element, remaining, sizes);
        layoutElements(element, remaining, extractComponentCross(element.getSize()), sizes);
    }

    private double initialSizeElements(GroupElement<?> element, double remaining, double[] sizes) {
        for (int i = 0; i < element.getChildren().size(); i++) {
            if (i > 0) {
                remaining -= spacing;
            }
            GuiElement e = element.getChildren().get(i);
            // try preferred size
            sizes[i] = extractComponent(LayoutAssist.getLayoutSize(e, e.getPreferredSize()));
            if (remaining < sizes[i]) {
                // use min instead.
                int componentMin = extractComponent(LayoutAssist.getLayoutSize(e, e.getMinSize()));
                sizes[i] = Math.max(componentMin, remaining);
            }
            remaining -= sizes[i];
        }
        return remaining;
    }

    private double expandElements(Priority priority, GroupElement<?> element, double remaining, double[] sizes) {
        if (remaining <= 0) {
            return remaining;
        }

        // find number to split over
        int splitNum = 0;
        for (GuiElement e : element.getChildren()) {
            if (getData(e) == priority) {
                splitNum++;
            }
        }
        if (splitNum > 0) {
            // tally up total size of all splitting components
            double remSave = remaining;
            double addSize = 0;
            for (int i = 0; i < element.getChildren().size(); i++) {
                GuiElement e = element.getChildren().get(i);

                if (getData(e) == priority) {
                    double size = Math.min(sizes[i] + remSave / splitNum, extractComponent(e.solidifySize(e.getMaxSize())));
                    double remSub = size - sizes[i];
                    remaining -= remSub;
                    addSize += size;
                }
            }
            // divy size up among elements
            for (int i = 0; i < element.getChildren().size(); i++) {
                GuiElement e = element.getChildren().get(i);

                if (getData(e) == priority) {
                    sizes[i] = Math.min(addSize / splitNum, extractComponent(e.solidifySize(e.getMaxSize())));
                    addSize -= sizes[i];
                    splitNum--;
                }
            }
            remaining = 0;
        }

        return remaining;
    }

    private void layoutElements(GroupElement<?> element, double remaining, double crossMax, double[] sizes) {
        double progress = initalProgress(element, remaining);
        for (int i = 0; i < element.getChildren().size(); i++) {
            GuiElement e = element.getChildren().get(i);
            double sz = sizes[i];
            Vector2i relPos = e.getRelativePosition();
            int margin = extractComponent(e.getMargin().getTopLeft());
            relPos = setComponent(relPos, (int) Math.round(progress + margin));
            // must adjust cross position for padding too!
            relPos = setComponentCross(relPos, extractComponent(e.getMargin().getTopLeft())
                    + extractComponent(element.getPadding().getTopLeft()));
            e.setRelativePosition(relPos);
            Vector2i sizeVec = setComponent(Vector2i.ZERO, (int) Math.round(sz));
            int size = extractComponent(LayoutAssist.layout2original(e, sizeVec));
            e.setSize(setComponent(e.getSize(), size));
            if (fill) {
                double adjMax = crossMax;
                adjMax -= extractComponentCross(e.getPadding().getAsWidthHeight().add(e.getMargin().getAsWidthHeight()));
                e.setSize(setComponentCross(e.getSize(), Math.max(0, (int) adjMax)));
            }
            progress += sz;
            progress += spacing;
        }
    }

    private double initalProgress(GroupElement<?> element, double remaining) {
        switch (align) {
            case CENTER:
                int padOffset = extractComponent(element.getPadding().getTopLeft());
                return remaining / 2 + padOffset;
            case START:
                return extractComponent(element.getPadding().getTopLeft());
            case END:
                return extractComponent(element.getPadding().getBottomRight());
            default:
                throw new IllegalStateException("Missing align case " + align);
        }
    }

    protected abstract int extractComponent(Vector2i vec);

    protected abstract Vector2i setComponent(Vector2i vec, int compValue);

    protected abstract int extractComponentCross(Vector2i vec);

    protected abstract Vector2i setComponentCross(Vector2i vec, int compValue);

}
