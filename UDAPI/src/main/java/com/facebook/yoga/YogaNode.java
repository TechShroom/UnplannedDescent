/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.facebook.yoga;

import static org.lwjgl.util.yoga.Yoga.YGNodeCalculateLayout;
import static org.lwjgl.util.yoga.Yoga.YGNodeCopyStyle;
import static org.lwjgl.util.yoga.Yoga.YGNodeFree;
import static org.lwjgl.util.yoga.Yoga.YGNodeInsertChild;
import static org.lwjgl.util.yoga.Yoga.YGNodeIsDirty;
import static org.lwjgl.util.yoga.Yoga.YGNodeMarkDirty;
import static org.lwjgl.util.yoga.Yoga.YGNodeNew;
import static org.lwjgl.util.yoga.Yoga.YGNodeNewWithConfig;
import static org.lwjgl.util.yoga.Yoga.YGNodePrint;
import static org.lwjgl.util.yoga.Yoga.YGNodeRemoveChild;
import static org.lwjgl.util.yoga.Yoga.YGNodeReset;
import static org.lwjgl.util.yoga.Yoga.YGNodeSetBaselineFunc;
import static org.lwjgl.util.yoga.Yoga.YGNodeSetMeasureFunc;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleGetAlignContent;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleGetAlignItems;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleGetAlignSelf;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleGetAspectRatio;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleGetBorder;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleGetDirection;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleGetDisplay;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleGetFlexBasis;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleGetFlexDirection;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleGetFlexGrow;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleGetFlexShrink;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleGetHeight;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleGetJustifyContent;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleGetMargin;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleGetMaxHeight;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleGetMaxWidth;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleGetMinHeight;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleGetMinWidth;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleGetOverflow;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleGetPadding;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleGetPosition;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleGetPositionType;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleGetWidth;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetAlignContent;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetAlignItems;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetAlignSelf;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetAspectRatio;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetBorder;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetDirection;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetDisplay;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetFlex;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetFlexBasis;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetFlexBasisAuto;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetFlexBasisPercent;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetFlexDirection;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetFlexGrow;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetFlexShrink;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetFlexWrap;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetHeight;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetHeightAuto;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetHeightPercent;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetJustifyContent;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetMargin;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetMarginAuto;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetMarginPercent;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetMaxHeight;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetMaxHeightPercent;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetMaxWidth;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetMaxWidthPercent;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetMinHeight;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetMinHeightPercent;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetMinWidth;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetMinWidthPercent;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetOverflow;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetPadding;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetPaddingPercent;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetPosition;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetPositionPercent;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetPositionType;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetWidth;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetWidthAuto;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetWidthPercent;
import static org.lwjgl.util.yoga.Yoga.YGPrintOptionsChildren;
import static org.lwjgl.util.yoga.Yoga.YGPrintOptionsLayout;
import static org.lwjgl.util.yoga.Yoga.YGPrintOptionsStyle;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.yoga.YGValue;

public class YogaNode {

    private YogaNode mParent;
    private List<YogaNode> mChildren;
    private YogaMeasureFunction mMeasureFunction;
    private YogaBaselineFunction mBaselineFunction;
    private long mNativePointer;
    private Object mData;

    /* Those flags needs be in sync with YGJNI.cpp */
    private final static int MARGIN = 1;
    private final static int PADDING = 2;
    private final static int BORDER = 4;

    private int mEdgeSetFlag = 0;

    private boolean mHasSetPosition = false;

    private float mWidth = YogaConstants.UNDEFINED;

    private float mHeight = YogaConstants.UNDEFINED;

    private float mTop = YogaConstants.UNDEFINED;

    private float mLeft = YogaConstants.UNDEFINED;

    private float mMarginLeft = 0;

    private float mMarginTop = 0;

    private float mMarginRight = 0;

    private float mMarginBottom = 0;

    private float mPaddingLeft = 0;

    private float mPaddingTop = 0;

    private float mPaddingRight = 0;

    private float mPaddingBottom = 0;

    private float mBorderLeft = 0;

    private float mBorderTop = 0;

    private float mBorderRight = 0;

    private float mBorderBottom = 0;

    private int mLayoutDirection = 0;

    private boolean mHasNewLayout = true;

    YogaNode(long pointer) {
        mNativePointer = pointer;
    }

    public YogaNode() {
        mNativePointer = YGNodeNew();
        if (mNativePointer == 0) {
            throw new IllegalStateException("Failed to allocate native memory");
        }
    }

    public YogaNode(YogaConfig config) {
        mNativePointer = YGNodeNewWithConfig(config.mNativePointer);
        if (mNativePointer == 0) {
            throw new IllegalStateException("Failed to allocate native memory");
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            YGNodeFree(mNativePointer);
        } finally {
            super.finalize();
        }
    }

    public void reset() {
        mEdgeSetFlag = 0;
        mHasSetPosition = false;
        mHasNewLayout = true;

        mWidth = YogaConstants.UNDEFINED;
        mHeight = YogaConstants.UNDEFINED;
        mTop = YogaConstants.UNDEFINED;
        mLeft = YogaConstants.UNDEFINED;
        mMarginLeft = 0;
        mMarginTop = 0;
        mMarginRight = 0;
        mMarginBottom = 0;
        mPaddingLeft = 0;
        mPaddingTop = 0;
        mPaddingRight = 0;
        mPaddingBottom = 0;
        mBorderLeft = 0;
        mBorderTop = 0;
        mBorderRight = 0;
        mBorderBottom = 0;
        mLayoutDirection = 0;

        mMeasureFunction = null;
        mBaselineFunction = null;
        mData = null;

        YGNodeReset(mNativePointer);
    }

    public int getChildCount() {
        return mChildren == null ? 0 : mChildren.size();
    }

    public YogaNode getChildAt(int i) {
        return mChildren.get(i);
    }

    public void addChildAt(YogaNode child, int i) {
        if (child.mParent != null) {
            throw new IllegalStateException("Child already has a parent, it must be removed first.");
        }

        if (mChildren == null) {
            mChildren = new ArrayList<>(4);
        }
        mChildren.add(i, child);
        child.mParent = this;
        YGNodeInsertChild(mNativePointer, child.mNativePointer, i);
    }

    public YogaNode removeChildAt(int i) {

        final YogaNode child = mChildren.remove(i);
        child.mParent = null;
        YGNodeRemoveChild(mNativePointer, child.mNativePointer);
        return child;
    }

    public @Nullable
            YogaNode getParent() {
        return mParent;
    }

    public int indexOf(YogaNode child) {
        return mChildren == null ? -1 : mChildren.indexOf(child);
    }

    public void calculateLayout(float width, float height) {
        YGNodeCalculateLayout(mNativePointer, width, height, getStyleDirection().intValue());
    }

    public boolean hasNewLayout() {
        return mHasNewLayout;
    }

    public void dirty() {
        YGNodeMarkDirty(mNativePointer);
    }

    public boolean isDirty() {
        return YGNodeIsDirty(mNativePointer);
    }

    public void copyStyle(YogaNode srcNode) {
        YGNodeCopyStyle(mNativePointer, srcNode.mNativePointer);
    }

    public void markLayoutSeen() {
        mHasNewLayout = false;
    }

    public YogaDirection getStyleDirection() {
        return YogaDirection.fromInt(YGNodeStyleGetDirection(mNativePointer));
    }

    public void setDirection(YogaDirection direction) {
        YGNodeStyleSetDirection(mNativePointer, direction.intValue());
    }

    public YogaFlexDirection getFlexDirection() {
        return YogaFlexDirection.fromInt(YGNodeStyleGetFlexDirection(mNativePointer));
    }

    public void setFlexDirection(YogaFlexDirection flexDirection) {
        YGNodeStyleSetFlexDirection(mNativePointer, flexDirection.intValue());
    }

    public YogaJustify getJustifyContent() {
        return YogaJustify.fromInt(YGNodeStyleGetJustifyContent(mNativePointer));
    }

    public void setJustifyContent(YogaJustify justifyContent) {
        YGNodeStyleSetJustifyContent(mNativePointer, justifyContent.intValue());
    }

    public YogaAlign getAlignItems() {
        return YogaAlign.fromInt(YGNodeStyleGetAlignItems(mNativePointer));
    }

    public void setAlignItems(YogaAlign alignItems) {
        YGNodeStyleSetAlignItems(mNativePointer, alignItems.intValue());
    }

    public YogaAlign getAlignSelf() {
        return YogaAlign.fromInt(YGNodeStyleGetAlignSelf(mNativePointer));
    }

    public void setAlignSelf(YogaAlign alignSelf) {
        YGNodeStyleSetAlignSelf(mNativePointer, alignSelf.intValue());
    }

    public YogaAlign getAlignContent() {
        return YogaAlign.fromInt(YGNodeStyleGetAlignContent(mNativePointer));
    }

    public void setAlignContent(YogaAlign alignContent) {
        YGNodeStyleSetAlignContent(mNativePointer, alignContent.intValue());
    }

    public YogaPositionType getPositionType() {
        return YogaPositionType.fromInt(YGNodeStyleGetPositionType(mNativePointer));
    }

    public void setPositionType(YogaPositionType positionType) {
        YGNodeStyleSetPositionType(mNativePointer, positionType.intValue());
    }

    public void setWrap(YogaWrap flexWrap) {
        YGNodeStyleSetFlexWrap(mNativePointer, flexWrap.intValue());
    }

    public YogaOverflow getOverflow() {
        return YogaOverflow.fromInt(YGNodeStyleGetOverflow(mNativePointer));
    }

    public void setOverflow(YogaOverflow overflow) {
        YGNodeStyleSetOverflow(mNativePointer, overflow.intValue());
    }

    public YogaDisplay getDisplay() {
        return YogaDisplay.fromInt(YGNodeStyleGetDisplay(mNativePointer));
    }

    public void setDisplay(YogaDisplay display) {
        YGNodeStyleSetDisplay(mNativePointer, display.intValue());
    }

    public void setFlex(float flex) {
        YGNodeStyleSetFlex(mNativePointer, flex);
    }

    public float getFlexGrow() {
        return YGNodeStyleGetFlexGrow(mNativePointer);
    }

    public void setFlexGrow(float flexGrow) {
        YGNodeStyleSetFlexGrow(mNativePointer, flexGrow);
    }

    public float getFlexShrink() {
        return YGNodeStyleGetFlexShrink(mNativePointer);
    }

    public void setFlexShrink(float flexShrink) {
        YGNodeStyleSetFlexShrink(mNativePointer, flexShrink);
    }

    public YogaValue getFlexBasis() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            YGValue value = YGValue.callocStack();
            YGNodeStyleGetFlexBasis(mNativePointer, value);
            return new YogaValue(value.value(), value.unit());
        }
    }

    public void setFlexBasis(float flexBasis) {
        YGNodeStyleSetFlexBasis(mNativePointer, flexBasis);
    }

    public void setFlexBasisPercent(float percent) {
        YGNodeStyleSetFlexBasisPercent(mNativePointer, percent);
    }

    public void setFlexBasisAuto() {
        YGNodeStyleSetFlexBasisAuto(mNativePointer);
    }

    public YogaValue getMargin(YogaEdge edge) {
        if (!((mEdgeSetFlag & MARGIN) == MARGIN)) {
            return YogaValue.UNDEFINED;
        }
        try (MemoryStack stack = MemoryStack.stackPush()) {
            YGValue value = YGValue.callocStack();
            YGNodeStyleGetMargin(mNativePointer, edge.intValue(), value);
            return new YogaValue(value.value(), value.unit());
        }
    }

    public void setMargin(YogaEdge edge, float margin) {
        mEdgeSetFlag |= MARGIN;
        YGNodeStyleSetMargin(mNativePointer, edge.intValue(), margin);
    }

    public void setMarginPercent(YogaEdge edge, float percent) {
        mEdgeSetFlag |= MARGIN;
        YGNodeStyleSetMarginPercent(mNativePointer, edge.intValue(), percent);
    }

    public void setMarginAuto(YogaEdge edge) {
        mEdgeSetFlag |= MARGIN;
        YGNodeStyleSetMarginAuto(mNativePointer, edge.intValue());
    }

    public YogaValue getPadding(YogaEdge edge) {
        if (!((mEdgeSetFlag & PADDING) == PADDING)) {
            return YogaValue.UNDEFINED;
        }
        try (MemoryStack stack = MemoryStack.stackPush()) {
            YGValue value = YGValue.callocStack();
            YGNodeStyleGetPadding(mNativePointer, edge.intValue(), value);
            return new YogaValue(value.value(), value.unit());
        }
    }

    public void setPadding(YogaEdge edge, float padding) {
        mEdgeSetFlag |= PADDING;
        YGNodeStyleSetPadding(mNativePointer, edge.intValue(), padding);
    }

    public void setPaddingPercent(YogaEdge edge, float percent) {
        mEdgeSetFlag |= PADDING;
        YGNodeStyleSetPaddingPercent(mNativePointer, edge.intValue(), percent);
    }

    public float getBorder(YogaEdge edge) {
        if (!((mEdgeSetFlag & BORDER) == BORDER)) {
            return YogaConstants.UNDEFINED;
        }
        return YGNodeStyleGetBorder(mNativePointer, edge.intValue());
    }

    public void setBorder(YogaEdge edge, float border) {
        mEdgeSetFlag |= BORDER;
        YGNodeStyleSetBorder(mNativePointer, edge.intValue(), border);
    }

    public YogaValue getPosition(YogaEdge edge) {
        if (!mHasSetPosition) {
            return YogaValue.UNDEFINED;
        }
        try (MemoryStack stack = MemoryStack.stackPush()) {
            YGValue value = YGValue.callocStack();
            YGNodeStyleGetPosition(mNativePointer, edge.intValue(), value);
            return new YogaValue(value.value(), value.unit());
        }
    }

    public void setPosition(YogaEdge edge, float position) {
        mHasSetPosition = true;
        YGNodeStyleSetPosition(mNativePointer, edge.intValue(), position);
    }

    public void setPositionPercent(YogaEdge edge, float percent) {
        mHasSetPosition = true;
        YGNodeStyleSetPositionPercent(mNativePointer, edge.intValue(), percent);
    }

    public YogaValue getWidth() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            YGValue value = YGValue.callocStack();
            YGNodeStyleGetWidth(mNativePointer, value);
            return new YogaValue(value.value(), value.unit());
        }
    }

    public void setWidth(float width) {
        YGNodeStyleSetWidth(mNativePointer, width);
    }

    public void setWidthPercent(float percent) {
        YGNodeStyleSetWidthPercent(mNativePointer, percent);
    }

    public void setWidthAuto() {
        YGNodeStyleSetWidthAuto(mNativePointer);
    }

    public YogaValue getHeight() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            YGValue value = YGValue.callocStack();
            YGNodeStyleGetHeight(mNativePointer, value);
            return new YogaValue(value.value(), value.unit());
        }
    }

    public void setHeight(float height) {
        YGNodeStyleSetHeight(mNativePointer, height);
    }

    public void setHeightPercent(float percent) {
        YGNodeStyleSetHeightPercent(mNativePointer, percent);
    }

    public void setHeightAuto() {
        YGNodeStyleSetHeightAuto(mNativePointer);
    }

    public YogaValue getMinWidth() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            YGValue value = YGValue.callocStack();
            YGNodeStyleGetMinWidth(mNativePointer, value);
            return new YogaValue(value.value(), value.unit());
        }
    }

    public void setMinWidth(float minWidth) {
        YGNodeStyleSetMinWidth(mNativePointer, minWidth);
    }

    public void setMinWidthPercent(float percent) {
        YGNodeStyleSetMinWidthPercent(mNativePointer, percent);
    }

    public YogaValue getMinHeight() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            YGValue value = YGValue.callocStack();
            YGNodeStyleGetMinHeight(mNativePointer, value);
            return new YogaValue(value.value(), value.unit());
        }
    }

    public void setMinHeight(float minHeight) {
        YGNodeStyleSetMinHeight(mNativePointer, minHeight);
    }

    public void setMinHeightPercent(float percent) {
        YGNodeStyleSetMinHeightPercent(mNativePointer, percent);
    }

    public YogaValue getMaxWidth() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            YGValue value = YGValue.callocStack();
            YGNodeStyleGetMaxWidth(mNativePointer, value);
            return new YogaValue(value.value(), value.unit());
        }
    }

    public void setMaxWidth(float maxWidth) {
        YGNodeStyleSetMaxWidth(mNativePointer, maxWidth);
    }

    public void setMaxWidthPercent(float percent) {
        YGNodeStyleSetMaxWidthPercent(mNativePointer, percent);
    }

    public YogaValue getMaxHeight() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            YGValue value = YGValue.callocStack();
            YGNodeStyleGetMaxHeight(mNativePointer, value);
            return new YogaValue(value.value(), value.unit());
        }
    }

    public void setMaxHeight(float maxheight) {
        YGNodeStyleSetMaxHeight(mNativePointer, maxheight);
    }

    public void setMaxHeightPercent(float percent) {
        YGNodeStyleSetMaxHeightPercent(mNativePointer, percent);
    }

    public float getAspectRatio() {
        return YGNodeStyleGetAspectRatio(mNativePointer);
    }

    public void setAspectRatio(float aspectRatio) {
        YGNodeStyleSetAspectRatio(mNativePointer, aspectRatio);
    }

    public float getLayoutX() {
        return mLeft;
    }

    public float getLayoutY() {
        return mTop;
    }

    public float getLayoutWidth() {
        return mWidth;
    }

    public float getLayoutHeight() {
        return mHeight;
    }

    public float getLayoutMargin(YogaEdge edge) {
        switch (edge) {
            case LEFT:
                return mMarginLeft;
            case TOP:
                return mMarginTop;
            case RIGHT:
                return mMarginRight;
            case BOTTOM:
                return mMarginBottom;
            case START:
                return getLayoutDirection() == YogaDirection.RTL ? mMarginRight : mMarginLeft;
            case END:
                return getLayoutDirection() == YogaDirection.RTL ? mMarginLeft : mMarginRight;
            default:
                throw new IllegalArgumentException("Cannot get layout margins of multi-edge shorthands");
        }
    }

    public float getLayoutPadding(YogaEdge edge) {
        switch (edge) {
            case LEFT:
                return mPaddingLeft;
            case TOP:
                return mPaddingTop;
            case RIGHT:
                return mPaddingRight;
            case BOTTOM:
                return mPaddingBottom;
            case START:
                return getLayoutDirection() == YogaDirection.RTL ? mPaddingRight : mPaddingLeft;
            case END:
                return getLayoutDirection() == YogaDirection.RTL ? mPaddingLeft : mPaddingRight;
            default:
                throw new IllegalArgumentException("Cannot get layout paddings of multi-edge shorthands");
        }
    }

    public float getLayoutBorder(YogaEdge edge) {
        switch (edge) {
            case LEFT:
                return mBorderLeft;
            case TOP:
                return mBorderTop;
            case RIGHT:
                return mBorderRight;
            case BOTTOM:
                return mBorderBottom;
            case START:
                return getLayoutDirection() == YogaDirection.RTL ? mBorderRight : mBorderLeft;
            case END:
                return getLayoutDirection() == YogaDirection.RTL ? mBorderLeft : mBorderRight;
            default:
                throw new IllegalArgumentException("Cannot get layout border of multi-edge shorthands");
        }
    }

    public YogaDirection getLayoutDirection() {
        return YogaDirection.fromInt(mLayoutDirection);
    }

    public void setMeasureFunction(YogaMeasureFunction measureFunction) {
        mMeasureFunction = measureFunction;
        YGNodeSetMeasureFunc(mNativePointer,
                (node, width, widthMode, height, heightMode) -> measure(width, widthMode, height, heightMode));
    }

    // Implementation Note: Why this method needs to stay final
    //
    // We cache the jmethodid for this method in Yoga code. This means that even
    // if a subclass
    // were to override measure, we'd still call this implementation from layout
    // code since the
    // overriding method will have a different jmethodid. This is final to
    // prevent that mistake.

    public final long measure(float width, int widthMode, float height, int heightMode) {
        if (!isMeasureDefined()) {
            throw new RuntimeException("Measure function isn't defined!");
        }

        return mMeasureFunction.measure(
                this,
                width,
                YogaMeasureMode.fromInt(widthMode),
                height,
                YogaMeasureMode.fromInt(heightMode));
    }

    public void setBaselineFunction(YogaBaselineFunction baselineFunction) {
        mBaselineFunction = baselineFunction;
        YGNodeSetBaselineFunc(mNativePointer, (node, width, height) -> baseline(width, height));
    }

    public final float baseline(float width, float height) {
        return mBaselineFunction.baseline(this, width, height);
    }

    public boolean isMeasureDefined() {
        return mMeasureFunction != null;
    }

    public void setData(Object data) {
        mData = data;
    }

    public Object getData() {
        return mData;
    }

    /**
     * Use the set logger (defaults to adb log) to print out the styles,
     * children, and computed layout of the tree rooted at this node.
     */
    public void print() {
        YGNodePrint(mNativePointer, YGPrintOptionsStyle | YGPrintOptionsChildren | YGPrintOptionsLayout);
    }
}
