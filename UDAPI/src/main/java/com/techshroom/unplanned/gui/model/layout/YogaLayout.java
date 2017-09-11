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

import static com.google.common.base.Preconditions.checkState;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.ToDoubleFunction;

import com.facebook.yoga.YogaConfig;
import com.facebook.yoga.YogaEdge;
import com.facebook.yoga.YogaMeasureMode;
import com.facebook.yoga.YogaMeasureOutput;
import com.facebook.yoga.YogaNode;
import com.flowpowered.math.vector.Vector2i;
import com.google.auto.value.AutoValue;
import com.google.common.eventbus.Subscribe;
import com.techshroom.unplanned.core.util.FloatConsumer;
import com.techshroom.unplanned.geometry.SidedVector4i;
import com.techshroom.unplanned.gui.event.ElementRevalidationEvent;
import com.techshroom.unplanned.gui.model.GuiElement;
import com.techshroom.unplanned.gui.model.Labeled;
import com.techshroom.unplanned.gui.model.PropertyKey;
import com.techshroom.unplanned.gui.model.Size;
import com.techshroom.unplanned.gui.model.SizeValue;
import com.techshroom.unplanned.gui.model.SizeValue.SVType;
import com.techshroom.unplanned.gui.model.layout.YogaLayout.FlexData;
import com.techshroom.unplanned.gui.model.parent.GroupElement;

public class YogaLayout extends DataBindingLayout<FlexData> {

    @AutoValue
    public abstract static class FlexData {

        public static Builder builder() {
            return new AutoValue_YogaLayout_FlexData.Builder();
        }

        @AutoValue.Builder
        public interface Builder {

            Builder yogaNode(YogaNode yogaNode);

            FlexData build();

            default <E extends GuiElement> E addTo(YogaLayout layout, E element) {
                return layout.bindData(element, build());
            }

        }

        FlexData() {
        }

        public abstract YogaNode yogaNode();

    }

    private static final class YLDirtyListener {

        private final YogaLayout yogaLayout;
        private final GuiElement element;

        YLDirtyListener(YogaLayout yogaLayout, GuiElement element) {
            this.yogaLayout = yogaLayout;
            this.element = element;
        }

        @Subscribe
        public void onRevalidation(ElementRevalidationEvent event) {
            yogaLayout.getData(element).yogaNode().dirty();
        }

    }

    private static final YogaConfig config = new YogaConfig();
    static {
        config.setLogger((node, level, message) -> {
            System.err.print(message);
        });
    }

    private final PropertyKey<YLDirtyListener> propDirtyListener = PropertyKey.unique("yogaLayoutDirtyListener");
    private final YogaNode root = new YogaNode(config);

    public YogaLayout() {
        super("yogaLayoutFlexData");
    }

    public YogaNode getRoot() {
        return root;
    }

    private void copySided(SidedVector4i vec, BiConsumer<YogaEdge, Float> sideSetter) {
        sideSetter.accept(YogaEdge.TOP, (float) vec.getTop());
        sideSetter.accept(YogaEdge.BOTTOM, (float) vec.getBottom());
        sideSetter.accept(YogaEdge.LEFT, (float) vec.getLeft());
        sideSetter.accept(YogaEdge.RIGHT, (float) vec.getRight());
    }

    private SidedVector4i copySided(ToDoubleFunction<YogaEdge> sideGetter) {
        double top = sideGetter.applyAsDouble(YogaEdge.TOP);
        double bottom = sideGetter.applyAsDouble(YogaEdge.BOTTOM);
        double left = sideGetter.applyAsDouble(YogaEdge.LEFT);
        double right = sideGetter.applyAsDouble(YogaEdge.RIGHT);
        return new SidedVector4i(top, right, bottom, left);
    }

    @Override
    protected FlexData getInitialValue(GuiElement element) {
        YogaNode node = new YogaNode(config);
        copyElementData(element, node);

        return FlexData.builder().yogaNode(node).build();
    }

    private void copyElementData(GuiElement element, YogaNode node) {
        node.setData(element);
        // we like flex grow a lot, so we default it to on
        node.setFlexGrow(1);
        Size<SizeValue> size = element.getLayoutSize();
        Size<SizeValue> max = element.getMaxSize();
        Size<SizeValue> min = element.getMinSize();

        copySizeValue(size.width(), node::setWidth, node::setWidthPercent);
        copySizeValue(size.height(), node::setHeight, node::setHeightPercent);

        copySizeValue(max.width(), node::setMaxWidth, node::setMaxWidthPercent);
        copySizeValue(max.height(), node::setMaxHeight, node::setMaxHeightPercent);

        copySizeValue(min.width(), node::setMinWidth, node::setMinWidthPercent);
        copySizeValue(min.height(), node::setMinHeight, node::setMinHeightPercent);

        copySided(element.getMargin(), node::setMargin);
        copySided(element.getPadding(), node::setPadding);

        if (element instanceof Labeled) {
            // hook up yoga's measure function
            ynMeasureLabel(node, (Labeled) element);
        }
    }

    private void copySizeValue(SizeValue sizeValue, FloatConsumer set, FloatConsumer setPercent) {
        if (sizeValue.type() == SVType.INTEGER && sizeValue.value() != 0) {
            set.accept((float) sizeValue.value());
        } else if (sizeValue.type() == SVType.PERCENT) {
            setPercent.accept((float) sizeValue.value());
        }
    }

    private void ynMeasureLabel(YogaNode node, Labeled element) {
        YLDirtyListener listener = new YLDirtyListener(this, element);
        element.getEventBus().register(listener);
        // stick it in props to retain a reference
        element.setProperty(propDirtyListener, listener);
        node.setMeasureFunction((
                YogaNode thisNode,
                float width,
                YogaMeasureMode widthMode,
                float height,
                YogaMeasureMode heightMode) -> {
            // i dunno what to do here...
            // guess i'll just ignore input!
            element.validate();
            Vector2i size = element.getSize();
            return YogaMeasureOutput.make(size.getX(), size.getY());
        });
    }

    public void modifyNode(GuiElement element, Consumer<FlexData> dataConsumer) {
        FlexData data = getData(element);
        checkState(data != null, "element %s is not a part of this layout yet!", element);
        dataConsumer.accept(data);
    }

    @Override
    public void onChildAdded(GuiElement element, int index) {
        super.onChildAdded(element, index);
        root.addChildAt(getData(element).yogaNode(), index);
    }

    @Override
    public void onChildRemoved(GuiElement element, int index) {
        root.removeChildAt(index);
        element.removeProperty(propDirtyListener);
        super.onChildRemoved(element, index);
    }

    @Override
    public void layout(GroupElement element) {
        copyElementData(element, root);

        Vector2i size = element.solidifySize(element.getLayoutSize());
        root.calculateLayout(size.getX(), size.getY());

        // now copy it back! pretty simple...
        recursiveCopyback(root);
    }

    private void recursiveCopyback(YogaNode node) {
        // this data copy shouldn't happen to the root node, it's controlled by
        // its parent.
        if (node != root) {
            Object data = node.getData();
            if (!(data instanceof GuiElement)) {
                return;
            }
            GuiElement e = (GuiElement) data;
            e.setSize((int) node.getLayoutWidth(), (int) node.getLayoutHeight());
            e.setRelativePosition((int) node.getLayoutX(), (int) node.getLayoutY());
            e.setMargin(copySided(node::getLayoutMargin));
            e.setPadding(copySided(node::getLayoutPadding));
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            recursiveCopyback(node.getChildAt(i));
        }
    }

}
