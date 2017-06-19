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
package com.techshroom.unplanned.examples.rubix;

import java.util.IdentityHashMap;
import java.util.Map;

import com.flowpowered.math.vector.Vector3i;
import com.techshroom.unplanned.blitter.Drawable;
import com.techshroom.unplanned.blitter.GraphicsContext;
import com.techshroom.unplanned.blitter.Shape;
import com.techshroom.unplanned.blitter.textures.Downscaling;
import com.techshroom.unplanned.blitter.textures.Texture;
import com.techshroom.unplanned.blitter.textures.TextureSettings;
import com.techshroom.unplanned.blitter.textures.TextureWrap;
import com.techshroom.unplanned.blitter.textures.Upscaling;
import com.techshroom.unplanned.blitter.textures.loader.ColorTextureLoader;
import com.techshroom.unplanned.blitter.textures.loader.StandardTextureLoaders;
import com.techshroom.unplanned.blitter.textures.map.TextureAtlas;
import com.techshroom.unplanned.blitter.textures.map.TextureCollection;
import com.techshroom.unplanned.core.util.Color;
import com.techshroom.unplanned.core.util.LifecycleObject;

public class CubeHandlerRenderer implements Drawable, LifecycleObject {

    // cube of cubes
    // each cube is 75x75x75
    // each cube is 5px apart
    // 3*75 + 2*5 = whatever, that's w/h
    private static final int CUBE_SIDE = 75;
    private static final int CUBE_PAD = 5;
    private static final int ENTIRE_CUBE_SIDE = 3 * CUBE_SIDE + 2 * CUBE_PAD;

    private static String colorKey(Vector3i color) {
        return color.toString();
    }

    private final GraphicsContext ctx;
    private final CubeHandler model;

    private final TextureCollection textures = TextureCollection.of();
    // there are always 54 cubes
    private final Map<Quad, Shape> shapes = new IdentityHashMap<>(54);
    private Texture colorTexture;

    public CubeHandlerRenderer(GraphicsContext ctx, CubeHandler model) {
        this.ctx = ctx;
        this.model = model;
    }

    @Override
    public void initialize() {
        ColorTextureLoader ctl = StandardTextureLoaders.RGBA_COLOR_LOADER;
        // extract common colors
        model.getQuads().forEach(c -> {
            Vector3i color = c.getColor();
            String colorKey = colorKey(color);
            if (!textures.getData().containsValue(colorKey)) {
                textures.put(ctl.load(Color.fromInt(color.getX(), color.getY(), color.getZ(), 0xFF)), colorKey);
            }
        });
        TextureAtlas atlas = TextureAtlas.create(512, 512, textures);

        colorTexture = ctx.getTextureProvider().load(atlas.getData(), TextureSettings.builder()
                .downscaling(Downscaling.NEAREST)
                .upscaling(Upscaling.NEAREST)
                .textureWrapping(TextureWrap.CLAMP_TO_EDGE)
                .build());
        colorTexture.initialize();

        model.getQuads().forEach(quad -> {
            // shapes.put(quad, ctx.getShapes().quad().shape(Plane.XY, a, b,
            // texture));
        });
    }

    @Override
    public void destroy() {
        colorTexture.destroy();
    }

    @Override
    public void draw() {

    }

}
