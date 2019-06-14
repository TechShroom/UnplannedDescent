/*
 * This file is part of unplanned-descent, licensed under the MIT License (MIT).
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

import java.util.Deque;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Throwables;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.Maps;
import com.techshroom.unplanned.blitter.GraphicsContext;
import com.techshroom.unplanned.blitter.pen.DigitalPen;
import com.techshroom.unplanned.blitter.textures.Downscaling;
import com.techshroom.unplanned.blitter.textures.Texture;
import com.techshroom.unplanned.blitter.textures.TextureData;
import com.techshroom.unplanned.blitter.textures.TextureSettings;
import com.techshroom.unplanned.blitter.textures.TextureWrap;
import com.techshroom.unplanned.blitter.textures.Upscaling;
import com.techshroom.unplanned.blitter.textures.loader.StandardTextureLoaders;
import com.techshroom.unplanned.core.util.Color;
import com.techshroom.unplanned.gui.model.GuiElement;

public final class RenderUtility {

    public static String key(Class<?> clazz, String name) {
        return clazz.getName() + "." + name;
    }

    private static final TextureSettings COLOR_CACHE_SETTINGS = TextureSettings.builder()
            .downscaling(Downscaling.NEAREST)
            .upscaling(Upscaling.NEAREST)
            .textureWrapping(TextureWrap.REPEAT)
            .build();

    private static final Deque<Texture> REMOVED_TEXTURES = new ConcurrentLinkedDeque<>();
    private static final Cache<Entry<String, Color>, Texture> COLOR_CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.MINUTES)
            .maximumSize(64)
            .removalListener((RemovalNotification<Entry<String, Color>, Texture> ntf) -> {
                REMOVED_TEXTURES.addLast(ntf.getValue());
            })
            .build();

    private static Entry<String, Color> colorCacheKey(GraphicsContext ctx, Color color) {
        return Maps.immutableEntry(ctx.getUniqueId(), color);
    }

    public static Texture getColorTexture(GraphicsContext ctx, Color color) {
        try {
            return COLOR_CACHE.get(colorCacheKey(ctx, color), () -> {
                TextureData data = StandardTextureLoaders.RGBA_COLOR_LOADER.load(color);
                Texture texture = ctx.getTextureProvider().load(data, COLOR_CACHE_SETTINGS);
                texture.initialize();
                return texture;
            });
        } catch (ExecutionException e) {
            Throwable t = e.getCause();
            Throwables.throwIfUnchecked(t);
            throw new RuntimeException(t);
        }
    }

    public static void evokeColorTexture(GraphicsContext ctx, Color color) {
        COLOR_CACHE.invalidate(colorCacheKey(ctx, color));
    }

    public static void performColorCacheCleanup() {
        for (Iterator<Texture> iterator = REMOVED_TEXTURES.iterator(); iterator.hasNext();) {
            Texture texture = iterator.next();
            texture.destroy();
            iterator.remove();
        }
    }

    public static void applyStandardTransform(GuiElement e, DigitalPen pen, boolean forContent) {
        pen.translate(e.getRelativePosition().toDouble());
        if (forContent) {
            pen.translate(e.getPadding().getTopLeft().toDouble());
        }
    }

    private RenderUtility() {
    }
}
