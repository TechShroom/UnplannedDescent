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

package com.techshroom.unplanned.blitter.textures.map;

import com.techshroom.unplanned.blitter.textures.TextureData;
import com.techshroom.unplanned.blitter.textures.TextureFormat;
import com.techshroom.unplanned.core.Settings;
import com.techshroom.unplanned.core.util.Maths;
import com.techshroom.unplanned.geometry.WHRectangleI;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Creates a texture atlas by combining multiple {@link TextureData}. U/V
 * positions for coordinates of the individual TextureData can be queried using
 * the instance.
 */
public final class TextureAtlas {

    public static TextureAtlas create(int maximumWidth, int maximumHeight, TextureCollection individualTextures) {
        // stitch together a huge TextureData from the individual ones
        Map<String, WHRectangleI> optimalPacking = TexturePacker.pack(maximumWidth, maximumHeight, individualTextures);

        // figure out the closest power of 2 for width and height
        int w = 0;
        int h = 0;
        for (WHRectangleI rect : optimalPacking.values()) {
            w = Math.max(rect.getX() + rect.getWidth(), w);
            h = Math.max(rect.getY() + rect.getHeight(), h);
        }

        TextureFormat format = findMostCommonFormat(individualTextures);
        w = Maths.nextPowerOfTwo(w);
        h = Maths.nextPowerOfTwo(h);

        ByteBuffer target = BufferUtils.createByteBuffer(w * h * format.channels);
        if (Settings.FILL_TEXTURES_WITH_DEBUG_COLOR) {
            fillWithDebug(target, w, h, format);
        }
        optimalPacking.forEach((id, rect) -> {
            // rx,ry,rw,rh are 1px too big on each side to fix mipmaps
            // this must be considered when copying!
            int rx = rect.getX();
            int ry = rect.getY();
            TextureData tex = individualTextures.get(id);
            ByteBuffer src = tex.getData();
            // copy each row in
            for (int tx = 0; tx < tex.getWidth(); tx++) {
                // TODO figure out order of input pixels...
                throw new AssertionError("Texture atlas currently unimplemented.");
                // get source col @ tx
//                int[] srcCol = src[tx];
                // to be copied into dest col @ rx + 1 (for 1px offset) + tx
//                int[] destCol = target[rx + 1 + tx];
                // copy from srcCol[:] to destCol[ry+1:th], again accounting for
                // 1px offset
//                System.arraycopy(srcCol, 0, destCol, ry + 1, tex.getHeight());
            }
        });

        // create new TD
        return new TextureAtlas(TextureData.wrap(w, h, target, format), optimalPacking);
    }

    private static TextureFormat findMostCommonFormat(TextureCollection individualTextures) {
        Map<TextureData, Integer> formats = new HashMap<>();
        for (TextureData textureData : individualTextures.getData().keySet()) {
            formats.compute(textureData, (k, i) -> (i == null ? 0 : i) + 1);
        }
        return formats.entrySet().stream()
            .max(Comparator.comparing(Map.Entry::getValue))
            .map(Map.Entry::getKey)
            .map(TextureData::getFormat)
            .orElse(TextureFormat.RGBA);
    }

    private static void fillWithDebug(ByteBuffer target, int width, int height, TextureFormat format) {
        for (int i = 0; i < width; i++) {
            for (int y = 0; y < height; y++) {
                int r = 0;// (int) (((i + 1) / (double) target.length) * 255);
                int g = (int) (((y + 1) / (double) height) * 255);
                format.putRGBA((r) | (g << 8) | (255 << 24), target);
            }
        }
    }

    private final TextureData data;
    private final Map<String, WHRectangleI> positions;

    private TextureAtlas(TextureData data, Map<String, WHRectangleI> positions) {
        this.data = data;
        this.positions = positions;
    }

    public TextureData getData() {
        return data;
    }

    // 1 is added to x/y in the following functions
    // to account for the 1px offset

    public double getUForId(String id, int x) {
        checkArgument(positions.containsKey(id), "no such texture loaded in atlas: %s", id);
        return (positions.get(id).getX() + 1 + x) / (double) data.getWidth();
    }

    public double getVForId(String id, int y) {
        checkArgument(positions.containsKey(id), "no such texture loaded in atlas: %s", id);
        return (positions.get(id).getY() + 1 + y) / (double) data.getHeight();
    }

}
