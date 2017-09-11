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

import com.facebook.yoga.YogaAlign;
import com.facebook.yoga.YogaFlexDirection;
import com.facebook.yoga.YogaJustify;
import com.flowpowered.math.matrix.Matrix4f;
import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Streams;
import com.google.common.eventbus.Subscribe;
import com.techshroom.unplanned.blitter.GraphicsContext;
import com.techshroom.unplanned.blitter.font.FontDefault;
import com.techshroom.unplanned.blitter.transform.TransformStack;
import com.techshroom.unplanned.core.util.Color;
import com.techshroom.unplanned.event.keyboard.KeyState;
import com.techshroom.unplanned.event.keyboard.KeyStateEvent;
import com.techshroom.unplanned.event.window.WindowResizeEvent;
import com.techshroom.unplanned.geometry.SidedVector4i;
import com.techshroom.unplanned.gui.model.Label;
import com.techshroom.unplanned.gui.model.Size;
import com.techshroom.unplanned.gui.model.SizeValue;
import com.techshroom.unplanned.gui.model.layout.YogaLayout;
import com.techshroom.unplanned.gui.model.parent.GroupElement;
import com.techshroom.unplanned.gui.model.parent.Panel;
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

    private Window window;
    private Matrix4f proj;
    private Vector2i windowSize;
    private RenderManager renderManager;

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

        GroupElement base = new Panel();
        base.setPreferredSize(size);
        Panel left = new Panel();
        Panel right = new Panel();
        Panel a = new Panel();
        Panel b = new Panel();
        Panel c = new Panel();
        Panel d = new Panel();

        a.setBackgroundColor(Color.RED);
        b.setBackgroundColor(Color.GREEN);
        c.setBackgroundColor(Color.fromString("#FEFDE7"));
        d.setBackgroundColor(Color.BLUE);
        left.setBackgroundColor(Color.WHITE);
        right.setBackgroundColor(Color.fromString("#AA00BB"));

        a.setPreferredSize(Size.of(SizeValue.percent(50), SizeValue.integer(0)));
        a.setMargin(SidedVector4i.all(5));
        b.setMargin(SidedVector4i.all(5));
        c.setMargin(SidedVector4i.all(5));
        d.setMargin(SidedVector4i.all(5));
        left.setMargin(SidedVector4i.all(10));
        right.setMargin(SidedVector4i.all(50));

        left.addChildren(a, b);
        right.addChildren(c, d);
        base.addChildren(left, right);

        Label label = Label.builder()
                .text("Whatever!")
                .font(FontDefault.getPlainDescriptor().withSize(50))
                .textSizer(ctx.getPen()).build();
        label.setPadding(new SidedVector4i(5, 5, 5, 5));
        label.setBackgroundColor(Color.RED);
        // base.addChild(label);

        // Flex that layout!
        base.setLayout(() -> {
            YogaLayout layout = new YogaLayout();
            layout.getRoot().setFlexDirection(YogaFlexDirection.ROW);
            layout.getRoot().setAlignContent(YogaAlign.CENTER);
            layout.getRoot().setJustifyContent(YogaJustify.CENTER);
            return layout;
        }, null);
        left.setLayout(() -> {
            YogaLayout layout = new YogaLayout();
            layout.getRoot().setAlignContent(YogaAlign.CENTER);
            layout.getRoot().setJustifyContent(YogaJustify.CENTER);
            return layout;
        }, layout -> {
            layout.modifyNode(a, data -> data.yogaNode().setAlignSelf(YogaAlign.CENTER));
        });
        right.setLayout(() -> {
            YogaLayout layout = new YogaLayout();
            layout.getRoot().setAlignContent(YogaAlign.CENTER);
            layout.getRoot().setJustifyContent(YogaJustify.CENTER);
            return layout;
        }, null);

        RootGuiElementRender renderer = new DefaultRootGuiElementRenderer();
        RenderManager guiRM = renderManager = new RenderManager(renderer, ctx);
        resize(WindowResizeEvent.create(window, size.getX(), size.getY()));

        while (!window.isCloseRequested()) {
            window.processEvents();
            ctx.clearGraphicsState();
            try (TransformStack stack = ctx.pushTransformer()) {
                stack.projection().set(proj);

                guiRM.render(base);
            }

            ctx.swapBuffers();
        }

        window.destroy();
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
        renderManager.setScale(windowSize.toDouble().div(UI_SIZE.toDouble()));
        proj = Matrix4f.createOrthographic(w, 0, 0, h, -1000, 1000);
    }

}
