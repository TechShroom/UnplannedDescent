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

import java.util.Map;

import javax.annotation.Nullable;

import com.techshroom.unplanned.blitter.GraphicsContext;
import com.techshroom.unplanned.gui.model.GuiElement;

public abstract class RenderJob<GE extends GuiElement> implements AutoCloseable {

    private final GraphicsContext context;
    private final GE element;
    private final Map<String, Object> state;
    private final Map<String, Object> renderCache;

    public RenderJob(GraphicsContext context, GE element, Map<String, Object> state, Map<String, Object> renderCache) {
        this.context = context;
        this.element = element;
        this.state = state;
        this.renderCache = renderCache;
    }

    public GraphicsContext getContext() {
        return context;
    }

    public GE getElement() {
        return element;
    }

    public Map<String, Object> getState() {
        return state;
    }

    @Nullable
    public <T> T getState(String key, Class<T> type) {
        return type.cast(state.get(key));
    }

    public void putState(String key, Object value) {
        state.put(key, value);
    }

    public Map<String, Object> getRenderCache() {
        return renderCache;
    }

    @Nullable
    public <T> T getRenderCache(String key, Class<T> type) {
        return type.cast(renderCache.get(key));
    }

    public void putRenderCache(String key, Object value) {
        renderCache.put(key, value);
    }

    public abstract <SGE extends GuiElement> RenderJob<SGE> createSubRenderJob(SGE element);

    @Override
    public void close() {
    }

}
