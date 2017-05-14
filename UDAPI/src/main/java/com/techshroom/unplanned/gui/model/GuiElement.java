package com.techshroom.unplanned.gui.model;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector4i;

public interface GuiElement {

    boolean isVisible();

    void setVisible(boolean visible);

    Vector2i getPosition();

    void setPosition(Vector2i pos);

    Vector4i getPadding();

    void setPadding(Vector4i padding);

    Vector4i getMargin();

    void setMargin(Vector4i margin);

}
