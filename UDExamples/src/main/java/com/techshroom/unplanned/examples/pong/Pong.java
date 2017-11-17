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
package com.techshroom.unplanned.examples.pong;

import java.util.concurrent.TimeUnit;

import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector2i;
import com.google.auto.service.AutoService;
import com.google.common.eventbus.Subscribe;
import com.techshroom.unplanned.blitter.GraphicsContext;
import com.techshroom.unplanned.blitter.pen.DigitalPen;
import com.techshroom.unplanned.core.util.Color;
import com.techshroom.unplanned.core.util.Sync;
import com.techshroom.unplanned.core.util.time.Timer;
import com.techshroom.unplanned.ecs.CompEntAssoc;
import com.techshroom.unplanned.ecs.ObjectCEAFactory;
import com.techshroom.unplanned.ecs.defaults.RemovalSystem;
import com.techshroom.unplanned.event.keyboard.KeyState;
import com.techshroom.unplanned.event.keyboard.KeyStateEvent;
import com.techshroom.unplanned.event.queue.EventQueue;
import com.techshroom.unplanned.event.queue.guava.GuavaEventQueue;
import com.techshroom.unplanned.event.window.WindowResizeEvent;
import com.techshroom.unplanned.examples.Example;
import com.techshroom.unplanned.examples.snek.Direction.Dir;
import com.techshroom.unplanned.input.Key;
import com.techshroom.unplanned.window.Window;

@AutoService(Example.class)
public class Pong extends Example {

    public static final Vector2i SCREEN_SIZE = Vector2i.from(20, 40);
    
    public static final EventQueue EVENTS = new GuavaEventQueue();

    private CompEntAssoc assoc;
    private Vector2d scale = Vector2d.ONE;
    private Window window;

    @Override
    public void run(Window window) {
        this.window = window;
        window.getEventBus().register(this);
        window.setTitle("Pong");

        GraphicsContext ctx = window.getGraphicsContext();
        ctx.makeActiveContext();
        window.setVsyncOn(false);
        window.setVisible(true);
        onResize(WindowResizeEvent.create(window, window.getSize().getX(), window.getSize().getY()));

        DigitalPen pen = ctx.getPen();
        Sync sync = new Sync();

        assoc = ObjectCEAFactory.$.builder()
                .systems(
                        ColorDrawSystem.create(pen),
                        CollisionSystem.create(),
                        MovementSystem.create(),
                        RemovalSystem.create())
                .components(
                        PaddleMarker.INSTANCE)
                .build();

        addPaddles();

        long lastNanos = Timer.getInstance().getValue(TimeUnit.NANOSECONDS);

        while (!window.isCloseRequested()) {
            sync.sync(60);
            window.processEvents();
            ctx.clearGraphicsState();

            pen.uncap();

            pen.scale(scale);

            long diff = Timer.getInstance().getValue(TimeUnit.NANOSECONDS) - lastNanos;
            lastNanos += diff;
            assoc.tick(diff);

            pen.cap();

            ctx.swapBuffers();
        }

        window.getEventBus().unregister(this);
    }

    private int playerPaddle;

    private void addPaddles() {

    }

    @Subscribe
    public void onKey(KeyStateEvent event) {
        if (event.is(Key.ESCAPE, KeyState.PRESSED)) {
            window.setCloseRequested(true);
            return;
        }
        EVENTS.post(event);
        if (event.getState() == KeyState.RELEASED) {
            Dir dir;
            switch (event.getKey()) {
                case A:
                case LEFT:
                    dir = Dir.L;
                    break;
                case D:
                case RIGHT:
                    dir = Dir.R;
                    break;
                case W:
                case UP:
                    dir = Dir.U;
                    break;
                case S:
                case DOWN:
                    dir = Dir.D;
                    break;
                default:
                    return;
            }
            Velocity.INSTANCE.set(assoc, playerPaddle, dir.unit);
        }
    }

    @Subscribe
    public void onResize(WindowResizeEvent event) {
        this.scale = event.getSize().toDouble().div(SCREEN_SIZE.toDouble());
    }

}
