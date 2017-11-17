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

import static com.google.common.base.Preconditions.checkArgument;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_BASELINE;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_BOTTOM;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_MIDDLE;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_RIGHT;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_TOP;
import static org.lwjgl.nanovg.NanoVG.NVG_IMAGE_NEAREST;
import static org.lwjgl.nanovg.NanoVG.NVG_IMAGE_PREMULTIPLIED;
import static org.lwjgl.nanovg.NanoVG.nvgBeginFrame;
import static org.lwjgl.nanovg.NanoVG.nvgBeginPath;
import static org.lwjgl.nanovg.NanoVG.nvgEndFrame;
import static org.lwjgl.nanovg.NanoVG.nvgFill;
import static org.lwjgl.nanovg.NanoVG.nvgImagePattern;
import static org.lwjgl.nanovg.NanoVG.nvgRect;
import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.nanovg.NanoVG.nvgRoundedRect;
import static org.lwjgl.nanovg.NanoVG.nvgRoundedRectVarying;
import static org.lwjgl.nanovg.NanoVG.nvgSave;
import static org.lwjgl.nanovg.NanoVG.nvgScale;
import static org.lwjgl.nanovg.NanoVG.nvgStroke;
import static org.lwjgl.nanovg.NanoVG.nvgText;
import static org.lwjgl.nanovg.NanoVG.nvgTextAlign;
import static org.lwjgl.nanovg.NanoVG.nvgTranslate;
import static org.lwjgl.nanovg.NanoVGGL3.NVG_IMAGE_NODELETE;
import static org.lwjgl.nanovg.NanoVGGL3.nvglCreateImageFromHandle;

import org.lwjgl.nanovg.NVGPaint;

import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector2i;
import com.google.common.eventbus.Subscribe;
import com.techshroom.unplanned.blitter.GLGraphicsContext;
import com.techshroom.unplanned.blitter.font.Font;
import com.techshroom.unplanned.blitter.font.FontDefault;
import com.techshroom.unplanned.blitter.font.NVGFont;
import com.techshroom.unplanned.blitter.textures.GLTexture;
import com.techshroom.unplanned.blitter.textures.Texture;
import com.techshroom.unplanned.blitter.textures.TextureData;
import com.techshroom.unplanned.core.util.Color;
import com.techshroom.unplanned.event.window.WindowFramebufferResizeEvent;
import com.techshroom.unplanned.event.window.WindowResizeEvent;

public class NVGPen implements DigitalPen {

    private final GLGraphicsContext graphics;
    private Vector2i winSize;
    private Vector2i fbSize;
    private NVGFont font;

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
        setFont(FontDefault.loadPlain(graphics));
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
    public void save() {
        nvgSave(ctx());
    }

    @Override
    public void restore() {
        nvgRestore(ctx());
    }

    @Override
    public void scale(Vector2d scale) {
        nvgScale(ctx(), (float) scale.getX(), (float) scale.getY());
    }

    @Override
    public void translate(Vector2d vec) {
        nvgTranslate(ctx(), (float) vec.getX(), (float) vec.getY());
    }

    @Override
    public PenInk getInk(Color color) {
        return new NVGColorInk(color);
    }

    @Override
    public PenInk getInk(int w, int h, Texture texture) {
        checkArgument(texture instanceof GLTexture, "texture must be an OpenGL texture, not %s", texture.getClass());
        GLTexture glTexture = (GLTexture) texture;
        TextureData data = glTexture.getData();
        int glId = glTexture.getTextureId();
        NVGPaint paint = NVGPaint.create();
        int img = nvglCreateImageFromHandle(ctx(), glId, data.getWidth(), data.getHeight(), NVG_IMAGE_NODELETE | NVG_IMAGE_PREMULTIPLIED | NVG_IMAGE_NEAREST);
        nvgImagePattern(ctx(), 0, 0, w, h, 0, img, 1, paint);
        return new NVGPaintInk(paint);
    }

    @Override
    public void setFont(Font font) {
        checkArgument(font instanceof NVGFont, "font must be an NVG font, not %s", font.getClass());
        this.font = (NVGFont) font;
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
    public void fill(PenInk ink) {
        validateInk(ink).applyFill(ctx());
        nvgFill(ctx());
    }

    @Override
    public void stroke(PenInk ink) {
        validateInk(ink).applyStroke(ctx());
        nvgStroke(ctx());
    }

    private NVGInk validateInk(PenInk ink) {
        checkArgument(ink instanceof NVGInk, "polluted ink detected!", ink);
        return (NVGInk) ink;
    }

    @Override
    public void rect(float x, float y, float w, float h) {
        nvgRect(ctx(), x, y, w, h);
    }

    @Override
    public void roundedRect(float x, float y, float w, float h, float r) {
        nvgRoundedRect(ctx(), x, y, w, h, r);
    }

    @Override
    public void roundedRectVarying(float x, float y, float w, float h, float topLeft, float topRight, float bottomLeft, float bottomRight) {
        nvgRoundedRectVarying(ctx(), x, y, w, h, topLeft, topRight, bottomLeft, bottomRight);
    }

    @Override
    public void textAlignment(TextAlignmentH alignHorizontal, TextAlignmentV alignVertical) {
        int nvgAlign = convertAlignment(alignHorizontal) | convertAlignment(alignVertical);
        nvgTextAlign(ctx(), nvgAlign);
    }

    private int convertAlignment(TextAlignmentH textAlignment) {
        switch (textAlignment) {
            case LEFT:
                return NVG_ALIGN_LEFT;
            case CENTER:
                return NVG_ALIGN_CENTER;
            case RIGHT:
                return NVG_ALIGN_RIGHT;
            default:
                throw new IllegalArgumentException(textAlignment.name());
        }
    }

    private int convertAlignment(TextAlignmentV textAlignment) {
        switch (textAlignment) {
            case TOP:
                return NVG_ALIGN_TOP;
            case MIDDLE:
                return NVG_ALIGN_MIDDLE;
            case BOTTOM:
                return NVG_ALIGN_BOTTOM;
            case BASELINE:
                return NVG_ALIGN_BASELINE;
            default:
                throw new IllegalArgumentException(textAlignment.name());
        }
    }

    @Override
    public void fillText(int x, int y, PenInk ink, String text) {
        font.setAsCurrentFont();
        validateInk(ink).applyFill(ctx());
        nvgText(ctx(), x, y, text);
    }

}
