package com.techshroom.unplanned.gui.model;

/**
 * {@link ParentElement} with the ability to add elements generically.
 */
public interface GroupElement extends ParentElement {

    void addChild(GuiElement element);

}
