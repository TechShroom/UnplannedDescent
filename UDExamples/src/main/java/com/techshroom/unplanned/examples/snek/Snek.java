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
package com.techshroom.unplanned.examples.snek;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector2i;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.Subscribe;
import com.techshroom.unplanned.blitter.GraphicsContext;
import com.techshroom.unplanned.blitter.pen.DigitalPen;
import com.techshroom.unplanned.core.util.Color;
import com.techshroom.unplanned.core.util.Sync;
import com.techshroom.unplanned.event.keyboard.KeyState;
import com.techshroom.unplanned.event.keyboard.KeyStateEvent;
import com.techshroom.unplanned.event.window.WindowResizeEvent;
import com.techshroom.unplanned.examples.Example;
import com.techshroom.unplanned.input.Key;
import com.techshroom.unplanned.window.Window;

@AutoService(Example.class)
public class Snek extends Example {

    private static final Vector2i GRID_SIZE = Vector2i.from(20);
    // sizeof cell, no border
    private static final Vector2i CELL_DIM = Vector2i.from(16, 16);
    private static final Vector2i BORDER_DIM = Vector2i.from(1);
    private static final Vector2i GRID_DIM = GRID_SIZE.mul(CELL_DIM.add(BORDER_DIM.mul(2)));

    private final List<GridObj> objects = new ArrayList<>();
    private Vector2d scale = Vector2d.ONE;
    private Window window;

    @Override
    public void run(Window window) {
        this.window = window;
        window.getEventBus().register(this);
        window.setTitle("Snek 2D");

        GraphicsContext ctx = window.getGraphicsContext();
        ctx.makeActiveContext();
        window.setVsyncOn(false);
        window.setVisible(true);
        onResize(WindowResizeEvent.create(window, window.getSize().getX(), window.getSize().getY()));

        DigitalPen pen = ctx.getPen();
        Sync sync = new Sync();

        while (!window.isCloseRequested()) {
            // sync to 3fps, SNEK SPEED
            sync.sync(3);
            window.processEvents();
            ctx.clearGraphicsState();

            pen.uncap();

            pen.scale(scale);

            pen.draw(this::drawGrid);
            pen.draw(this::drawGridContent);

            playSnek();

            pen.cap();

            ctx.swapBuffers();
        }

        window.getEventBus().unregister(this);
    }

    private static final Color COLOR_BORDER = Color.LIGHT_GRAY;
    private static final Map<String, Color> COLOR_MAP = ImmutableMap.of(
            "snek_head", Color.GREEN,
            "snek_body", Color.BLUE,
            "food", Color.RED);

    private void drawGrid(DigitalPen pen) {
        Vector2i stepVec = CELL_DIM.add(BORDER_DIM.mul(2));
        for (int i = 0; i < GRID_SIZE.getX(); i++) {
            for (int j = 0; j < GRID_SIZE.getY(); j++) {
                int x = i * stepVec.getX();
                int y = j * stepVec.getY();
                pen.stroke(() -> {
                    pen.setColor(COLOR_BORDER);
                    pen.rect(x, y, stepVec.getX(), stepVec.getY());
                });
            }
        }
    }

    private void drawGridContent(DigitalPen pen) {
        Vector2i stepVec = CELL_DIM.add(BORDER_DIM.mul(2));
        Vector2i size = CELL_DIM;
        for (GridObj obj : objects) {
            int x = obj.getX() * stepVec.getX() + BORDER_DIM.getX();
            int y = obj.getY() * stepVec.getY() + BORDER_DIM.getY();
            pen.fill(() -> {
                pen.setColor(COLOR_MAP.getOrDefault(obj.getId(), Color.WHITE));
                pen.rect(x, y, size.getX(), size.getY());
            });
        }
    }

    private enum Dir {
        U(Vector2i.from(0, -1)),
        D(Vector2i.from(0, 1)),
        L(Vector2i.from(-1, 0)),
        R(Vector2i.from(1, 0));

        public final Vector2i unit;

        Dir(Vector2i unit) {
            this.unit = unit;
        }

    }

    private final Random RNGESUS = new Random();
    private Dir dir = Dir.R;
    private SnekHead head = new SnekHead();
    {
        head.setX(GRID_SIZE.getX() / 2);
        head.setY(GRID_SIZE.getY() / 2);
        objects.add(head);
    }
    private Food activeFood;

    private void playSnek() {
        if (head == null) {
            return;
        }
        if (activeFood == null) {
            spawnFood();
        }
        head.move(dir.unit);
        if (outOfBounds() || hit(SnekBody.class)) {
            objects.remove(head);
            head = null;
            return;
        }
        if (hit(Food.class)) {
            objects.remove(activeFood);
            activeFood = null;
            SnekBody prev = head;
            while (prev.getPrev() != null) {
                prev = prev.getPrev();
            }
            SnekBody body = new SnekBody();
            prev.setPrev(body);
            objects.add(body);
        }
    }

    private boolean outOfBounds() {
        int x = head.getX();
        int y = head.getY();
        boolean insideX = 0 <= x && x < GRID_SIZE.getX();
        boolean insideY = 0 <= y && y < GRID_SIZE.getY();
        return !insideX || !insideY;
    }

    private boolean hit(Class<?> clazz) {
        for (GridObj obj : objects) {
            if (clazz.isInstance(obj) && obj != head) {
                if (obj.getX() == head.getX() && obj.getY() == head.getY()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void spawnFood() {
        activeFood = new Food();
        activeFood.setX(RNGESUS.nextInt(GRID_SIZE.getX()));
        activeFood.setY(RNGESUS.nextInt(GRID_SIZE.getY()));
        objects.add(activeFood);
    }

    @Subscribe
    public void onKey(KeyStateEvent event) {
        if (event.is(Key.ESCAPE, KeyState.PRESSED)) {
            window.setCloseRequested(true);
        }
        if (event.getState() == KeyState.RELEASED) {
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
                    break;
            }
        }
    }

    @Subscribe
    public void onResize(WindowResizeEvent event) {
        this.scale = event.getSize().toDouble().div(GRID_DIM.toDouble());
    }

}
