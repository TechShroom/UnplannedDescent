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
package com.techshroom.unplanned.window.dialog;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_MIDDLE;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_TOP;
import static org.lwjgl.nanovg.NanoVG.NVG_CW;
import static org.lwjgl.nanovg.NanoVG.nvgArc;
import static org.lwjgl.nanovg.NanoVG.nvgBeginFrame;
import static org.lwjgl.nanovg.NanoVG.nvgBeginPath;
import static org.lwjgl.nanovg.NanoVG.nvgCreateFontMem;
import static org.lwjgl.nanovg.NanoVG.nvgEndFrame;
import static org.lwjgl.nanovg.NanoVG.nvgFill;
import static org.lwjgl.nanovg.NanoVG.nvgFillColor;
import static org.lwjgl.nanovg.NanoVG.nvgFontFaceId;
import static org.lwjgl.nanovg.NanoVG.nvgFontSize;
import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.nanovg.NanoVG.nvgRoundedRect;
import static org.lwjgl.nanovg.NanoVG.nvgSave;
import static org.lwjgl.nanovg.NanoVG.nvgStroke;
import static org.lwjgl.nanovg.NanoVG.nvgStrokeColor;
import static org.lwjgl.nanovg.NanoVG.nvgText;
import static org.lwjgl.nanovg.NanoVG.nvgTextAlign;
import static org.lwjgl.nanovg.NanoVG.nvgTextBounds;
import static org.lwjgl.nanovg.NanoVGGL3.NVG_ANTIALIAS;
import static org.lwjgl.nanovg.NanoVGGL3.NVG_DEBUG;
import static org.lwjgl.nanovg.NanoVGGL3.nvgCreate;
import static org.lwjgl.nanovg.NanoVGGL3.nvgDelete;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_STENCIL_TEST;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.system.MemoryStack;

import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector4f;
import com.google.common.eventbus.Subscribe;
import com.techshroom.unplanned.blitter.GraphicsContext;
import com.techshroom.unplanned.core.util.Color;
import com.techshroom.unplanned.core.util.GLErrorCheck;
import com.techshroom.unplanned.core.util.NVGUtil;
import com.techshroom.unplanned.event.keyboard.KeyState;
import com.techshroom.unplanned.event.keyboard.KeyStateEvent;
import com.techshroom.unplanned.event.mouse.MouseButtonEvent;
import com.techshroom.unplanned.geometry.WHRectangleF;
import com.techshroom.unplanned.input.Key;
import com.techshroom.unplanned.input.Mouse;
import com.techshroom.unplanned.window.Window;
import com.techshroom.unplanned.window.WindowSettings;

public class GLOptionPane {

    private static NVGColor color(String hex) {
        Vector4f color = Color.fromString(hex).asVector4f();
        return NVGColor.calloc().r(color.getX()).g(color.getY()).b(color.getZ()).a(color.getW());
    }

    // #3366CC
    private static final NVGColor NVG_SELECTED_COLOR = color("#36C");
    private static final NVGColor NVG_HOVER_COLOR = color("#f55");
    // black
    private static final NVGColor NVG_DEFAULT_COLOR = color("#000");
    // a nice grey
    private static final NVGColor NVG_RB_OUTER_COLOR = color("#999");

    private static final float RADIO_BUTTON_RADIUS = 15f / 2;
    private static final float RADIO_BUTTON_INNER_RADIUS = 10f / 2;

    private static final float PROMPT_FONT_SIZE = 24;
    private static final float CHOICE_FONT_SIZE = 18;

    private static final int PROMPT_ALIGN = NVG_ALIGN_CENTER | NVG_ALIGN_TOP;
    private static final int CHOICE_ALIGN = NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE;
    private static final int BUTTON_ALIGN = NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE;

    private static final float INITIAL_Y_OFFSET = 25;

    private static final float BUTTON_PADDING = 5;
    private static final float BUTTON_WIDTH = 50;
    // we want the inner space to be 20px, so we account for the padding outside
    private static final float BUTTON_HEIGHT = 20 + BUTTON_PADDING * 2;
    private static final float BUTTON_MARGIN = 10;

    private static final ByteBuffer SANS_DATA;
    static {
        try {
            SANS_DATA = NVGUtil.loadFont("noto/NotoSansUI-Regular.ttf");
        } catch (IOException e) {
            throw new Error("failed to load GLOptionPane font", e);
        }
    }

    private static float toRadF(double degrees) {
        return (float) Math.toDegrees(degrees);
    }

    @Nullable
    public static String showInputDialog(String title, String prompt, List<String> choices, String initial) {
        return showInputDialog(title, prompt, choices, initial, Function.identity());
    }

    @Nullable
    public static <T> T showInputDialog(String title, String prompt, List<T> choices, T initial, Function<T, String> toString) {
        int initIndex = choices.indexOf(initial);
        checkArgument(initIndex != -1, "initial not in choices!");

        long openCtx = glfwGetCurrentContext();
        GLOptionPane pane = new GLOptionPane(title, prompt, choices.stream().map(toString).collect(toImmutableList()), initIndex);
        try {
            int resultIndex = pane.show();
            return resultIndex == -1 ? null : choices.get(resultIndex);
        } finally {
            pane.destroy();
            glfwMakeContextCurrent(openCtx);
        }
    }

    private static float choicePosition(int index) {
        // offset from prompt by font * 2
        // offset from each choice by font * 1.25
        return INITIAL_Y_OFFSET + PROMPT_FONT_SIZE * 2 + index * (CHOICE_FONT_SIZE * 1.25f);
    }

    private final String prompt;
    private final List<String> choices;
    private final Window win;
    private final GraphicsContext ctx;

    private List<Vector2f> choiceSize;

    private boolean selfGeneratedClose;
    private int selected;
    private long nvg;

    private GLOptionPane(String title, String prompt, List<String> choices, int initial) {
        this.prompt = prompt;
        this.choices = choices;
        this.selected = initial;

        // regular initialization
        // draw up a new window
        win = WindowSettings.builder()
                .resizable(false)
                .title(title)
                .screenSize(600, (int) (choicePosition(choices.size()) + 20 + BUTTON_HEIGHT + BUTTON_MARGIN))
                .msaa(true)
                .alwaysOnTop(true)
                .build().createWindow();
        ctx = win.getGraphicsContext();
    }

    private int show() {
        ctx.makeActiveContext();
        GLErrorCheck.check();
        win.setVsyncOn(true);
        win.setVisible(true);

        win.getEventBus().register(this);

        glEnable(GL_STENCIL_TEST);
        glDisable(GL_DEPTH_TEST);

        GLErrorCheck.check();

        // nano time!
        nvg = nvgCreate(NVG_ANTIALIAS | NVG_DEBUG);
        checkState(nvg != 0, "failed to create nvg");

        int sans = nvgCreateFontMem(nvg, "sans", SANS_DATA, 0);
        checkState(sans != -1, "sans failed to load");
        try (MemoryStack stack = MemoryStack.stackPush()) {
            nvgFontFaceId(nvg, sans);

            nvgFontSize(nvg, CHOICE_FONT_SIZE);
            nvgTextAlign(nvg, CHOICE_ALIGN);
            choiceSize = choices.stream()
                    .map(s -> {
                        FloatBuffer b = stack.callocFloat(4);
                        nvgTextBounds(nvg, 0, 0, s, b);
                        return new Vector2f(b.get(2) - b.get(0), b.get(3) - b.get(1));
                    })
                    .collect(toImmutableList());
            glClearColor(1, 1, 1, 1);

            while (!win.isCloseRequested()) {
                win.processEvents();
                Vector2i fbSize = win.getFramebufferSize();
                Vector2i winSize = win.getSize();
                Vector2d cursor = win.getMouse().getPosition();

                ctx.clearGraphicsState();
                nvgBeginFrame(nvg, winSize.getX(), winSize.getY(), fbSize.getX() / winSize.getX());
                nvgFillColor(nvg, NVG_DEFAULT_COLOR);
                nvgStrokeColor(nvg, NVG_DEFAULT_COLOR);

                // prompt - centered
                nvgFontSize(nvg, PROMPT_FONT_SIZE);
                nvgTextAlign(nvg, PROMPT_ALIGN);
                nvgText(nvg, (winSize.getX() / 2.0f), INITIAL_Y_OFFSET, prompt);

                nvgFontSize(nvg, CHOICE_FONT_SIZE);
                nvgTextAlign(nvg, CHOICE_ALIGN);
                for (int i = 0; i < choices.size(); i++) {
                    WHRectangleF bounds = makeChoiceBounds(winSize, i, choiceSize.get(i));
                    WHRectangleF bb = makeChoiceBB(bounds);
                    drawChoice(winSize, cursor, i, choices.get(i), bounds, bb, selected == i);
                }

                drawButton(makeOkButtonBounds(winSize), "OK");

                nvgEndFrame(nvg);

                ctx.swapBuffers();
            }
            win.setVisible(false);
            if (!selfGeneratedClose) {
                selected = -1;
            }
        } finally {
            nvgDelete(nvg);
            nvg = 0;
            glDisable(GL_STENCIL_TEST);
            glEnable(GL_DEPTH_TEST);
        }

        return selected;
    }

    @Subscribe
    public void onKey(KeyStateEvent event) {
        if (event.is(Key.ESCAPE, KeyState.RELEASED)) {
            selected = -1;
            selfGeneratedClose = true;
            win.setCloseRequested(true);
        }
    }

    @Subscribe
    public void onClick(MouseButtonEvent event) {
        final Vector2i winSize = win.getSize();
        final Vector2f mousePos = win.getMouse().getPosition().toFloat();
        if (event.isDown() && event.getButton() == Mouse.PRIMARY_MOUSE_BUTTON) {
            // click, potentially in a choice bounds
            for (int i = 0; i < choices.size(); i++) {
                WHRectangleF bb = makeChoiceBB(makeChoiceBounds(winSize, i, choiceSize.get(i)));
                if (bb.contains(mousePos)) {
                    selected = i;
                    return;
                }
            }
            // check button
            if (makeOkButtonBounds(winSize).contains(mousePos)) {
                selfGeneratedClose = true;
                win.setCloseRequested(true);
                return;
            }
        }
    }

    private WHRectangleF makeOkButtonBounds(Vector2i winSize) {
        return WHRectangleF.of(winSize.getX() - BUTTON_WIDTH - BUTTON_MARGIN,
                winSize.getY() - BUTTON_HEIGHT - BUTTON_MARGIN, BUTTON_WIDTH, BUTTON_HEIGHT);
    }

    private WHRectangleF makeChoiceBounds(Vector2i winSize, int index, Vector2f size) {
        final float xMin = winSize.getX() / 2f;
        final float yMin = choicePosition(index);
        return WHRectangleF.of(xMin, yMin, size.getX(), size.getY());
    }

    private WHRectangleF makeChoiceBB(WHRectangleF bounds) {
        final float bbxMin = bounds.getX() - RADIO_BUTTON_RADIUS * 2 - 5;
        final float bbyMin = bounds.getY() - 12;
        final float bbxMax = bounds.getX() + bounds.getWidth() + 5;
        final float bbyMax = bounds.getY() + bounds.getHeight() - 12;
        return WHRectangleF.fromXY(bbxMin, bbyMin, bbxMax, bbyMax);
    }

    private void drawButton(WHRectangleF bounds, String text) {
        nvgSave(nvg);

        nvgFontSize(nvg, CHOICE_FONT_SIZE);
        nvgStrokeColor(nvg, NVG_DEFAULT_COLOR);

        if (bounds.contains(win.getMouse().getPosition().toFloat())) {
            nvgFillColor(nvg, NVG_HOVER_COLOR);
        } else {
            nvgFillColor(nvg, NVG_RB_OUTER_COLOR);
        }
        // internal color
        nvgBeginPath(nvg);
        nvgRoundedRect(nvg, bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), 5);
        nvgFill(nvg);

        // surrounding rect
        nvgBeginPath(nvg);
        nvgRoundedRect(nvg, bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), 5);
        nvgStroke(nvg);

        // text
        nvgFillColor(nvg, NVG_DEFAULT_COLOR);
        // set the text in the very center
        nvgTextAlign(nvg, BUTTON_ALIGN);
        nvgText(nvg, bounds.getX() + bounds.getWidth() / 2, bounds.getY() + bounds.getHeight() / 2, text);

        nvgRestore(nvg);
    }

    private void drawChoice(Vector2i winSize, Vector2d cursor, int index, String choice, WHRectangleF bounds, WHRectangleF bb, boolean selected) {
        final float xMin = bounds.getX();
        final float yMin = bounds.getY();
        // nvgBeginPath(nvg);
        // nvgRect(nvg, bbxMin, bbyMin, bbxMax - bbxMin, bbyMax - bbyMin);
        // nvgStroke(nvg);
        nvgFillColor(nvg, NVG_RB_OUTER_COLOR);
        nvgBeginPath(nvg);
        float rbX = xMin - RADIO_BUTTON_RADIUS - 5;
        float rbY = yMin - RADIO_BUTTON_RADIUS / 2 - 1;
        nvgArc(nvg, rbX, rbY, RADIO_BUTTON_RADIUS, 0, toRadF(360), NVG_CW);
        nvgFill(nvg);

        if (bb.contains(cursor.toFloat())) {
            nvgFillColor(nvg, NVG_HOVER_COLOR);
        } else if (selected) {
            nvgFillColor(nvg, NVG_SELECTED_COLOR);
        } else {
            nvgFillColor(nvg, NVG_DEFAULT_COLOR);
        }

        nvgBeginPath(nvg);
        nvgArc(nvg, rbX, rbY, RADIO_BUTTON_INNER_RADIUS, 0, toRadF(360), NVG_CW);
        nvgFill(nvg);

        nvgText(nvg, xMin, rbY, choice);
        nvgFillColor(nvg, NVG_DEFAULT_COLOR);
    }

    private void destroy() {
        win.setVisible(false);
        win.destroy();
    }

}
