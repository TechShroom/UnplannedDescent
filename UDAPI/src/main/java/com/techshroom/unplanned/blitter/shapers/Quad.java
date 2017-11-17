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
package com.techshroom.unplanned.blitter.shapers;

import java.util.List;

import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector2i;
import com.google.common.collect.ImmutableList;
import com.techshroom.unplanned.blitter.Shape;
import com.techshroom.unplanned.blitter.Vertex;
import com.techshroom.unplanned.geometry.Plane;

/**
 * Shapes a quad, requires 4 points.
 */
public interface Quad extends SinglePlaneVertexShaper {

    /**
     * A pre-calculated argument for
     * {@link #shape(Plane, Vector2i, Vector2i, List)}, that uses the standard
     * method of laying out quad textures, putting 0 and 1 at the appropriate
     * corners.
     */
    List<Vector2d> SIMPLE_TEXTURE = ImmutableList.of(
            Vector2d.from(0, 0),
            Vector2d.from(1, 0),
            Vector2d.from(0, 1),
            Vector2d.from(1, 1));

    @Override
    default int getSize() {
        return 4;
    }

    default Shape shape(Plane plane, Vector2i a, Vector2i b, List<Vector2d> texture) {
        // a--d
        // | /|
        // |/ |
        // c--b
        Vector2i c = new Vector2i(a.getX(), b.getY());
        Vector2i d = new Vector2i(b.getX(), a.getY());
        return shape(ImmutableList.of(
                Vertex.at(plane.convertToVector3(a).toDouble()).texture(texture.get(0)).build(),
                Vertex.at(plane.convertToVector3(d).toDouble()).texture(texture.get(1)).build(),
                Vertex.at(plane.convertToVector3(c).toDouble()).texture(texture.get(2)).build(),
                Vertex.at(plane.convertToVector3(b).toDouble()).texture(texture.get(3)).build()));
    }

}
