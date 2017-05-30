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
