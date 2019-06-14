/*
 * This file is part of unplanned-descent, licensed under the MIT License (MIT).
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.slf4j.Logger;

import com.flowpowered.math.vector.Vector2d;
import com.techshroom.unplanned.blitter.GraphicsContext;
import com.techshroom.unplanned.blitter.pen.DigitalPen;
import com.techshroom.unplanned.core.util.Logging;
import com.techshroom.unplanned.gui.model.GuiElement;
import com.techshroom.unplanned.gui.model.GuiElementInternal;
import com.techshroom.unplanned.gui.model.SizeValue.SVType;

/**
 * Manages the renders and stores state associated with each element.
 */
public final class RenderManager {

    private static final Logger LOGGER = Logging.getLogger();

    private static final class RMRenderJob<GE extends GuiElement> extends RenderJob<GE> {

        private final RenderManager manager;

        public RMRenderJob(RenderManager manager, GraphicsContext context, GE element, Map<String, Object> state, Map<String, RCache<Object>> renderCache) {
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
    private final GraphicsContext context;
    private final Map<GuiElement, Map<String, Object>> stateStorage = new WeakHashMap<>();
    private final Map<GuiElement, Map<String, RCache<Object>>> renderCacheStorage = new WeakHashMap<>();
    private Vector2d scale = Vector2d.ONE;

    public RenderManager(RootGuiElementRender renderer, GraphicsContext context) {
        this.renderer = renderer;
        this.context = context;
    }

    Map<String, Object> getState(GuiElement element) {
        return stateStorage.getOrDefault(element, new HashMap<>());
    }

    void putState(GuiElement element, Map<String, Object> state) {
        stateStorage.put(element, state);
    }

    Map<String, RCache<Object>> getRenderCache(GuiElement element) {
        Map<String, RCache<Object>> renderCache = renderCacheStorage.getOrDefault(element, new HashMap<>());
        if (element instanceof GuiElementInternal) {
            if (((GuiElementInternal) element).internalInvalidatedSinceLastDrawNotification()) {
                // invalidate render cache
                renderCache.forEach((k, v) -> {
                    v.destroy();
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

    void putRenderCache(GuiElement element, Map<String, RCache<Object>> renderCache) {
        if (element instanceof GuiElementInternal) {
            ((GuiElementInternal) element).internalDrawNotification();
        }
        renderCacheStorage.put(element, renderCache);
    }

    public Vector2d getScale() {
        return scale;
    }

    public void setScale(Vector2d scale) {
        this.scale = scale;
    }

    public void render(GuiElement root) {
        sanityCheckRoot(root);
        if (root.isVisible()) {
            DigitalPen pen = context.getPen();
            pen.uncap();
            try (RMRenderJob<GuiElement> job = new RMRenderJob<GuiElement>(this, context, root, getState(root), getRenderCache(root))) {
                pen.draw(() -> {
                    pen.scale(scale);
                    renderer.render(job);
                });
            } finally {
                // post render, do clean up
                RenderUtility.performColorCacheCleanup();
                pen.cap();
            }
        }
    }

    private static final Set<GuiElement> sanityCheckCache = Collections.newSetFromMap(new WeakHashMap<>());

    private void sanityCheckRoot(GuiElement root) {
        if (!sanityCheckCache.add(root)) {
            return;
        }
        if (root.getParent() == null) {
            if (root.getPreferredSize().width().type() == SVType.PERCENT ||
                    root.getPreferredSize().height().type() == SVType.PERCENT) {
                LOGGER.warn("[Sanity] Warning: root element has percent based size without a parent");
            }
        }
    }

}
