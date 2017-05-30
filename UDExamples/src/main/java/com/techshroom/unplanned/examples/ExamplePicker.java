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
import com.google.common.collect.Streams;
import com.google.common.eventbus.Subscribe;
import com.techshroom.unplanned.blitter.GraphicsContext;
import com.techshroom.unplanned.blitter.matrix.Matrices;
import com.techshroom.unplanned.event.Event;
import com.techshroom.unplanned.event.window.WindowResizeEvent;
import com.techshroom.unplanned.window.Window;
import com.techshroom.unplanned.window.WindowSettings;

public class ExamplePicker {

    private static final Map<String, Example> EXAMPLES;
    static {
        EXAMPLES = Streams.stream(ServiceLoader.load(Example.class))
                .collect(toImmutableMap(Example::getName, Function.identity()));
    }

    private Matrix4f proj;
    private Vector2i windowSize;

    public static void main(String[] args) {
        new ExamplePicker().run();
    }

    public void run() {
        Event.BUS.register(this);

        Window window = WindowSettings.builder()
                .screenSize(1024, 768)
                .title("Example Picker")
                .build().createWindow();

        GraphicsContext ctx = window.getGraphicsContext();

        ctx.makeActiveContext();
        window.setVsyncOn(true);
        window.setVisible(true);

        Vector2i size = window.getSize();
        resize(WindowResizeEvent.create(window, size.getX(), size.getY()));

        while (!window.isCloseRequested()) {
            window.processEvents();
            ctx.clearGraphicsState();

            ctx.swapBuffers();
        }

        window.destroy();
    }

    @Subscribe
    public void resize(WindowResizeEvent event) {
        windowSize = event.getSize();
        int w = windowSize.getX();
        int h = windowSize.getY();
        proj = Matrices.orthographicProjection(w, h, -1000, 1000);
    }

}
