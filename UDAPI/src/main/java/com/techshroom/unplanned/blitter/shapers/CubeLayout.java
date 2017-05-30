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
package com.techshroom.unplanned.blitter.shapers;

import java.util.List;

import com.flowpowered.math.vector.Vector2d;
import com.google.common.collect.ImmutableList;
import com.techshroom.unplanned.blitter.textures.TextureData;
import com.techshroom.unplanned.blitter.textures.map.TextureAtlas;
import com.techshroom.unplanned.blitter.textures.map.TextureCollection;

/**
 * Increases readability for most cube texture layouts.
 */
public final class CubeLayout {

    private final Vector2d[] vectors = new Vector2d[24];
    private final TextureCollection lookup;
    private final TextureAtlas source;

    public CubeLayout(TextureAtlas source, TextureCollection lookup) {
        this.source = source;
        this.lookup = lookup;
    }

    private void addPoints(String id, int from) {
        TextureData sizeInfo = lookup.get(id);
        double u1 = source.getUForId(id, 0);
        double u2 = source.getUForId(id, sizeInfo.getWidth() - 1);
        double v1 = source.getVForId(id, 0);
        double v2 = source.getVForId(id, sizeInfo.getHeight() - 1);
        vectors[from/**/] = new Vector2d(u1, v1);
        vectors[from + 1] = new Vector2d(u1, v2);
        vectors[from + 2] = new Vector2d(u2, v2);
        vectors[from + 3] = new Vector2d(u2, v1);
    }

    public CubeLayout all(String id) {
        return front(id).back(id).left(id).right(id).top(id).bottom(id);
    }

    public CubeLayout front(String id) {
        addPoints(id, 0);
        return this;
    }

    public CubeLayout back(String id) {
        addPoints(id, 20);
        return this;
    }

    public CubeLayout left(String id) {
        addPoints(id, 12);
        return this;
    }

    public CubeLayout right(String id) {
        addPoints(id, 4);
        return this;
    }

    public CubeLayout top(String id) {
        addPoints(id, 8);
        return this;
    }

    public CubeLayout bottom(String id) {
        addPoints(id, 16);
        return this;
    }

    public List<Vector2d> build() {
        return ImmutableList.copyOf(vectors);
    }

}
