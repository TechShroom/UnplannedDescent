package com.techshroom.unplanned.gui.model;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector4i;

public class GuiElementBase implements GuiElement {

    private boolean visible;
    private Vector2i pos = Vector2i.ZERO;
    private Vector4i padding = Vector4i.ZERO;
    private Vector4i margin = Vector4i.ZERO;

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public Vector2i getPosition() {
        return pos;
    }

    @Override
    public void setPosition(Vector2i pos) {
        this.pos = pos;
    }

    @Override
    public Vector4i getPadding() {
        return padding;
    }

    @Override
    public void setPadding(Vector4i padding) {
        this.padding = padding;
    }

    @Override
    public Vector4i getMargin() {
        return margin;
    }

    @Override
    public void setMargin(Vector4i margin) {
        this.margin = margin;
    }

}
