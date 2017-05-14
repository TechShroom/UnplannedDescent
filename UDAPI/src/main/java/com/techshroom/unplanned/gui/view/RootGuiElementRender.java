package com.techshroom.unplanned.gui.view;

import com.google.common.reflect.TypeToken;
import com.techshroom.unplanned.gui.model.GuiElement;

/**
 * Renderer for a root of an element tree. It can be passed any GUI element that
 * it knows how to render. New renders can be mapped by calling
 * {@link #addRenderer(GuiElementRenderer, TypeToken)}.
 */
public interface RootGuiElementRender extends GuiElementRenderer<GuiElement> {

    <GE extends GuiElement> void addRenderer(GuiElementRenderer<GE> renderer, Class<GE> type);

}
