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
package com.techshroom.unplanned.blitter.pen;

import static org.lwjgl.nanovg.NanoVG.nvgBeginFrame;
import static org.lwjgl.nanovg.NanoVG.nvgBeginPath;
import static org.lwjgl.nanovg.NanoVG.nvgEndFrame;
import static org.lwjgl.nanovg.NanoVG.nvgFill;
import static org.lwjgl.nanovg.NanoVG.nvgFillColor;
import static org.lwjgl.nanovg.NanoVG.nvgRect;
import static org.lwjgl.nanovg.NanoVG.nvgRoundedRect;
import static org.lwjgl.nanovg.NanoVG.nvgStroke;
import static org.lwjgl.nanovg.NanoVG.nvgStrokeColor;

import org.lwjgl.nanovg.NVGColor;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector4f;
import com.google.common.eventbus.Subscribe;
import com.techshroom.unplanned.blitter.GLGraphicsContext;
import com.techshroom.unplanned.blitter.font.Font;
import com.techshroom.unplanned.blitter.font.FontDefault;
import com.techshroom.unplanned.core.util.Color;
import com.techshroom.unplanned.event.window.WindowFramebufferResizeEvent;
import com.techshroom.unplanned.event.window.WindowResizeEvent;

public class NVGPen implements DigitalPen {

    private static NVGColor allocateNvgColor(Color color) {
        Vector4f c = color.asVector4f();
        return NVGColor.calloc().r(c.getX()).g(c.getY()).b(c.getZ()).a(c.getW());
    }

    private final GLGraphicsContext graphics;
    private Vector2i winSize;
    private Vector2i fbSize;
    private Color color = Color.BLACK;
    private NVGColor nvgColor = allocateNvgColor(color);
    private Font font;

    public NVGPen(GLGraphicsContext graphics) {
        this.graphics = graphics;
        winSize = graphics.getWindow().getSize();
        fbSize = graphics.getWindow().getFramebufferSize();
        this.graphics.getWindow().getEventBus().register(this);
    }

    @Subscribe
    public void onWindowResize(WindowResizeEvent event) {
        winSize = event.getSize();
    }

    @Subscribe
    public void onFramebufferResize(WindowFramebufferResizeEvent event) {
        fbSize = event.getSize();
    }

    public void initialize() {
        this.font = FontDefault.loadPlain(graphics);
    }

    public void destroy() {
        this.font.delete();
        this.font = null;
    }

    private long ctx() {
        return this.graphics.getNanoVgContext();
    }

    @Override
    public void uncap() {
        nvgBeginFrame(ctx(), winSize.getX(), winSize.getY(), fbSize.getX() / winSize.getX());
    }

    @Override
    public void cap() {
        nvgEndFrame(ctx());
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
        this.nvgColor.free();
        this.nvgColor = allocateNvgColor(color);
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setFont(Font font) {
        this.font = font;
    }

    @Override
    public Font getFont() {
        return font;
    }

    @Override
    public void begin() {
        nvgBeginPath(ctx());
    }

    @Override
    public void fill() {
        nvgFillColor(ctx(), nvgColor);
        nvgFill(ctx());
    }

    @Override
    public void stroke() {
        nvgStrokeColor(ctx(), nvgColor);
        nvgStroke(ctx());
    }
    
    @Override
    public void rect(float x, float y, float w, float h) {
        nvgRect(ctx(), x, y, w, h);
    }
    
    @Override
    public void roundedRect(float x, float y, float w, float h, float r) {
        nvgRoundedRect(ctx(), x, y, w, h, r);
    }

}
