package com.techshroom.unplanned.gui.view;

import java.util.HashMap;
import java.util.Map;

import com.google.common.reflect.TypeToken;
import com.techshroom.unplanned.gui.model.GuiElement;
import com.techshroom.unplanned.gui.model.ParentElement;

/**
 * A simple implementation of {@link RootGuiElementRender}.
 * 
 * <p>
 * <ul>
 * <li>Renders each element using a specialized renderer added with
 * {@link #addRenderer(GuiElementRenderer, TypeToken)}</li>
 * <li>If an element does not have a renderer added, and it is a
 * {@link ParentElement}, each of its children are rendered according to these
 * rules, but not the parent.</li>
 * </ul>
 * <p>
 */
public class SimpleRootGuiElementRenderer implements RootGuiElementRender {

    private final Map<Class<? extends GuiElement>, GuiElementRenderer<GuiElement>> renderers = new HashMap<>();

    @Override
    public void render(GuiElement element) {
        GuiElementRenderer<GuiElement> renderer = renderers.get(element.getClass());
        renderer.render(element);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <GE extends GuiElement> void addRenderer(GuiElementRenderer<GE> renderer, Class<GE> type) {
        renderers.put(type, (GuiElementRenderer<GuiElement>) renderer);
    }

}
