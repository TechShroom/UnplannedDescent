package com.techshroom.unplanned.gui.model;

import com.google.common.collect.ImmutableList;

/**
 * Parent of more {@link GuiElement GuiElements}.
 */
public interface ParentElement extends GuiElement {

    ImmutableList<GuiElement> getChildren();

}
