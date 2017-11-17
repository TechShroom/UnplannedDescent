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
package com.techshroom.unplanned.examples;

import static com.google.common.collect.ImmutableMap.toImmutableMap;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Function;

import com.flowpowered.math.matrix.Matrix4f;
import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Streams;
import com.google.common.eventbus.Subscribe;
import com.techshroom.unplanned.blitter.GraphicsContext;
import com.techshroom.unplanned.blitter.transform.TransformStack;
import com.techshroom.unplanned.core.util.Color;
import com.techshroom.unplanned.event.keyboard.KeyState;
import com.techshroom.unplanned.event.keyboard.KeyStateEvent;
import com.techshroom.unplanned.event.window.WindowResizeEvent;
import com.techshroom.unplanned.geometry.SidedVector4i;
import com.techshroom.unplanned.gui.event.ActionEvent;
import com.techshroom.unplanned.gui.hooks.WindowHooks;
import com.techshroom.unplanned.gui.model.Button;
import com.techshroom.unplanned.gui.model.Size;
import com.techshroom.unplanned.gui.model.SizeValue;
import com.techshroom.unplanned.gui.model.layout.VBoxLayout;
import com.techshroom.unplanned.gui.model.parent.HBox;
import com.techshroom.unplanned.gui.model.parent.VBox;
import com.techshroom.unplanned.gui.view.DefaultRootGuiElementRenderer;
import com.techshroom.unplanned.gui.view.RenderManager;
import com.techshroom.unplanned.gui.view.RootGuiElementRender;
import com.techshroom.unplanned.input.Key;
import com.techshroom.unplanned.window.Window;
import com.techshroom.unplanned.window.WindowSettings;

public class ExamplePicker {

    private static final Map<String, Example> EXAMPLES;
    static {
        EXAMPLES = Streams.stream(ServiceLoader.load(Example.class))
                .collect(toImmutableMap(Example::getName, Function.identity()));
    }

    private static final Vector2i UI_SIZE = new Vector2i(1024, 768);

    private Example runExample = null;
    private Window window;
    private Matrix4f proj;
    private Vector2i windowSize;

    public static void main(String[] args) {
        new ExamplePicker().run();
    }

    public void run() {
        window = WindowSettings.builder()
                .screenSize(UI_SIZE)
                .title("Example Picker")
                .msaa(true)
                .build().createWindow();
        window.getEventBus().register(this);

        GraphicsContext ctx = window.getGraphicsContext();

        ctx.makeActiveContext();
        ctx.setLight(new Vector3d(0, 0, 10000), Vector3d.ONE);
        window.setVsyncOn(true);
        window.setVisible(true);

        Vector2i size = window.getSize();

        HBox base = new HBox();
        base.setBackgroundColor(Color.GREEN);
        WindowHooks hooks = WindowHooks.forWindow(window);
        hooks.getRootElement().setChild(base);
        base.setPreferredSize(Size.of(SizeValue.percent(100), SizeValue.percent(100)));
        VBox buttonList = new VBox();
        buttonList.setLayout(VBoxLayout.builder().fillWidth(false).spacing(10).build());
        buttonList.setPadding(SidedVector4i.all(10));
        base.addChild(buttonList);

        EXAMPLES.forEach((name, ex) -> {
            Button b = new Button();
            b.setText(name);
            b.setTextSizer(hooks.getTextSizer());
            b.getEventBus().register(new Object() {

                @Subscribe
                public void onAction(ActionEvent event) {
                    runExample = ex;
                }
            });
            buttonList.addChild(b);
        });

        RootGuiElementRender renderer = new DefaultRootGuiElementRenderer();
        RenderManager guiRM = new RenderManager(renderer, ctx);
        resize(WindowResizeEvent.create(window, size.getX(), size.getY()));

        while (!window.isCloseRequested()) {
            window.processEvents();
            ctx.clearGraphicsState();
            try (TransformStack stack = ctx.pushTransformer()) {
                stack.projection().set(proj);

                guiRM.render(base);
            }

            ctx.swapBuffers();

            if (runExample != null) {
                runExample();
            }
        }

        window.destroy();
    }

    private void runExample() {
        window.getEventBus().unregister(this);
        try {
            runExample.run(window);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            runExample = null;
            window.getEventBus().register(this);
            window.setVisible(true);
            window.setCloseRequested(false);
            window.setSize(UI_SIZE);
        }
    }

    @Subscribe
    public void key(KeyStateEvent event) {
        if (event.is(Key.ESCAPE, KeyState.PRESSED)) {
            window.setCloseRequested(true);
        }
    }

    @Subscribe
    public void resize(WindowResizeEvent event) {
        windowSize = event.getSize();
        int w = windowSize.getX();
        int h = windowSize.getY();
        proj = Matrix4f.createOrthographic(w, 0, 0, h, -1000, 1000);
    }

}
