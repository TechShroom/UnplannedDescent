package com.techshroom.unplanned.gui.view;

import com.techshroom.unplanned.gui.model.GuiElement;

/**
 * Handles rendering the GUI.
 */
public interface GuiElementRenderer<GE extends GuiElement> {

    /**
     * Renders the entire element at the current position of the element.
     * 
     * @param element
     */
    void render(GE element);

}
