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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;

import java.util.List;

import com.flowpowered.math.vector.Vector3d;
import com.techshroom.unplanned.blitter.Shape;
import com.techshroom.unplanned.blitter.Vertex;

/**
 * For vertex shapers where all points should exist on a single plane.
 */
public interface SinglePlaneVertexShaper extends VertexShaper {

    /**
     * Computes normals for the shape by taking points on the plane defined by
     * the shape and taking the cross product.
     */
    @Override
    default Shape shape(List<Vertex> vertices) {
        checkArgument(vertices.size() == getSize(), "%s points are required", getSize());

        Vector3d v1 = vertices.get(0).getPosition().sub(vertices.get(1).getPosition());
        Vector3d v2 = vertices.get(0).getPosition().sub(vertices.get(2).getPosition());
        Vector3d normal = v1.cross(v2);

        List<Vertex> normalized = vertices.stream()
                .map(Vertex::toBuilder)
                .map(b -> b.normal(normal))
                .map(Vertex.Builder::build)
                .collect(toImmutableList());

        return shapeWithNormals(normalized);
    }

}
