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
package com.techshroom.unplanned.gui;

import java.util.function.Consumer;

import com.techshroom.unplanned.blitter.GraphicsContext;
import com.techshroom.unplanned.gui.hooks.WindowHooks;
import com.techshroom.unplanned.gui.model.Size;
import com.techshroom.unplanned.gui.model.SizeValue;
import com.techshroom.unplanned.gui.model.parent.Panel;
import com.techshroom.unplanned.gui.view.DefaultRootGuiElementRenderer;
import com.techshroom.unplanned.gui.view.RenderManager;
import com.techshroom.unplanned.gui.view.RootGuiElementRender;
import com.techshroom.unplanned.window.Window;
import com.techshroom.unplanned.window.WindowSettings;

/**
 * A dialog is a special wrapper around a {@link Window} that allows for a less
 * game-centric usage.
 */
public class Dialog {

    private final WindowSettings window;
    private final Panel rootElement = new Panel();

    public Dialog(WindowSettings window) {
        this.window = window;

        rootElement.setPreferredSize(Size.of(SizeValue.percent(100)));
    }

    public Panel getRootElement() {
        return rootElement;
    }

    public void show(Consumer<RootGuiElementRender> renderMapper) {
        Window win = window.createWindow();
        GraphicsContext ctx = win.getGraphicsContext();
        ctx.makeActiveContext();
        win.setVsyncOn(true);
        win.setVisible(true);

        DefaultRootGuiElementRenderer rootRender = new DefaultRootGuiElementRenderer();
        renderMapper.accept(rootRender);
        RenderManager renderManager = new RenderManager(rootRender, ctx);
        win.getEventBus().register(this);
        
        WindowHooks hooks = WindowHooks.forWindow(win);

        hooks.getRootElement().setChild(rootElement);

        while (!win.isCloseRequested()) {
            win.processEvents();
            ctx.clearGraphicsState();

            renderManager.render(hooks.getRootElement());

            ctx.swapBuffers();
        }

        win.destroy();
    }

}
