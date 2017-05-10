/*
 * This file is part of UnplannedDescent, licensed under the MIT License (MIT).
 *
 * Copyright (c) TechShroom Studios <https://techshoom.com>
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

import static org.lwjgl.stb.STBRectPack.stbrp_init_target;
import static org.lwjgl.stb.STBRectPack.stbrp_pack_rects;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.stb.STBRPContext;
import org.lwjgl.stb.STBRPNode;
import org.lwjgl.stb.STBRPRect;
import org.lwjgl.system.MemoryStack;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.techshroom.unplanned.blitter.textures.TextureData;
import com.techshroom.unplanned.geometry.WHRectangle;

public class TexturePacker {

    /* note: packer adds 1px spacing around each texture! */
    public static Map<String, WHRectangle> pack(int width, int height, TextureCollection individualTextures) {
        try (MemoryStack stack = MemoryStack.stackPush()) {

            // make some rectangles
            STBRPRect.Buffer rects = STBRPRect.callocStack(individualTextures.getData().size(), stack);
            List<Entry<TextureData, String>> entriesOrdered = ImmutableList.copyOf(individualTextures.getData().entrySet());
            for (Entry<TextureData, String> e : entriesOrdered) {
                TextureData tex = e.getKey();
                STBRPRect rect = rects.get();
                rect.id(rects.position() - 1);
                // 1px spacing added on each side = +2px
                rect.w((short) (tex.getWidth() + 2));
                rect.h((short) (tex.getHeight() + 2));
            }
            rects.flip();

            // setup ctx
            STBRPContext ctx = STBRPContext.callocStack(stack);
            STBRPNode.Buffer nodes = STBRPNode.callocStack(width * 2, stack);
            stbrp_init_target(ctx, width, height, nodes);

            // pack rects
            stbrp_pack_rects(ctx, rects);

            // extract settings to map
            ImmutableMap.Builder<String, WHRectangle> b = ImmutableMap.builder();
            while (rects.hasRemaining()) {
                STBRPRect rect = rects.get();
                if (rect.was_packed() == 0) {
                    throw new IllegalArgumentException("Unable to pack all rectangles, w/h too small?");
                }
                b.put(entriesOrdered.get(rect.id()).getValue(), WHRectangle.of(rect.x(), rect.y(), rect.w(), rect.h()));
            }
            return b.build();
        }
    }

}
