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
import java.util.WeakHashMap;

import com.techshroom.unplanned.blitter.GraphicsContext;
import com.techshroom.unplanned.core.util.LifecycleObject;
import com.techshroom.unplanned.gui.model.GuiElement;
import com.techshroom.unplanned.gui.model.GuiElementInternal;

/**
 * Manages the renders and stores state associated with each element.
 */
public final class RenderManager {

    private static final class RMRenderJob<GE extends GuiElement> extends RenderJob<GE> {

        private final RenderManager manager;

        public RMRenderJob(RenderManager manager, GraphicsContext context, GE element, Map<String, Object> state, Map<String, Object> renderCache) {
            super(context, element, state, renderCache);
            this.manager = manager;
        }

        @Override
        public <SGE extends GuiElement> RenderJob<SGE> createSubRenderJob(SGE element) {
            return new RMRenderJob<SGE>(manager, getContext(), element, manager.getState(element), manager.getRenderCache(element));
        }

        @Override
        public void close() {
            super.close();
            manager.putState(getElement(), getState());
            manager.putRenderCache(getElement(), getRenderCache());
        }

    }

    private final RootGuiElementRender renderer;
    private final Map<GuiElement, Map<String, Object>> stateStorage = new WeakHashMap<>();
    private final Map<GuiElement, Map<String, Object>> renderCacheStorage = new WeakHashMap<>();

    public RenderManager(RootGuiElementRender renderer) {
        this.renderer = renderer;
    }

    Map<String, Object> getState(GuiElement element) {
        return stateStorage.getOrDefault(element, new HashMap<>());
    }

    void putState(GuiElement element, Map<String, Object> state) {
        stateStorage.put(element, state);
    }

    Map<String, Object> getRenderCache(GuiElement element) {
        Map<String, Object> renderCache = renderCacheStorage.getOrDefault(element, new HashMap<>());
        if (element instanceof GuiElementInternal) {
            if (((GuiElementInternal) element).internalInvalidatedSinceLastDrawNotification()) {
                // invalidate render cache
                renderCache.forEach((k, v) -> {
                    if (v instanceof LifecycleObject) {
                        ((LifecycleObject) v).destroy();
                    }
                });
                renderCache.clear();
            }
        }
        if (!element.isValid()) {
            // validate before drawing
            element.validate();
        }
        return renderCache;
    }

    void putRenderCache(GuiElement element, Map<String, Object> renderCache) {
        if (element instanceof GuiElementInternal) {
            ((GuiElementInternal) element).internalDrawNotification();
        }
        renderCacheStorage.put(element, renderCache);
    }

    public void render(GraphicsContext context, GuiElement root) {
        if (root.isVisible()) {
            try (RMRenderJob<GuiElement> job = new RMRenderJob<GuiElement>(this, context, root, getState(root), getRenderCache(root))) {
                renderer.render(job);
            } finally {
                // post render, do clean up
                RenderUtility.performColorCacheCleanup();
            }
        }
    }

}
