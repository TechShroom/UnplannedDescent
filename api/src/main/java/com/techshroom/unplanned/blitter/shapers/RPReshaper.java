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

package com.techshroom.unplanned.blitter.shapers;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.ImmutableList;
import com.techshroom.unplanned.blitter.Normals;
import com.techshroom.unplanned.blitter.Vertex;

class RPReshaper {

    static List<Vertex> reshape(Vector3d a, Vector3d b) {
        Vector3d min = a.min(b);
        Vector3d max = a.max(b);
        // Generate 1-8 using min/max
        ImmutableList.Builder<Vertex> l = ImmutableList.builder();

        l.add(Vertex.at(min.getX(), min.getY(), max.getZ()).build());
        l.add(Vertex.at(min.getX(), max.getY(), max.getZ()).build());
        l.add(Vertex.at(max.getX(), max.getY(), max.getZ()).build());
        l.add(Vertex.at(max.getX(), min.getY(), max.getZ()).build());
        l.add(Vertex.at(min.getX(), min.getY(), min.getZ()).build());
        l.add(Vertex.at(min.getX(), max.getY(), min.getZ()).build());
        l.add(Vertex.at(max.getX(), max.getY(), min.getZ()).build());
        l.add(Vertex.at(max.getX(), min.getY(), min.getZ()).build());

        return l.build();
    }

    static List<Vertex> addNormals(List<Vertex> eightPointCube, List<Vector2d> texture) {
        checkArgument(texture == null || texture.size() == 24, "24 texture points are required");
        checkArgument(eightPointCube.size() == 8, "8 points are required");

        return new RPReshaper(eightPointCube, texture).getVertices();
    }

    private final List<Vertex> original;
    private final List<Vector2d> texture;
    private ImmutableList.Builder<Vertex> l;
    private int textureIndex;

    private RPReshaper(List<Vertex> eightPointCube, List<Vector2d> texture) {
        this.original = eightPointCube;
        this.texture = texture;
    }

    private List<Vertex> getVertices() {
        this.l = ImmutableList.builder();

        // ..........1--------5..................
        // ..........|********|..................
        // ..........|LEFT****|..................
        // ..........|N=-,0,0 |..................
        // .1--------2--------6--------5--------1
        // .|********|********|********|********|
        // .|FRONT***|TOP*****|BACK****|BOTTOM**|
        // .|N=0,0,+ |N=0,+,0 |N=0,0,- |N=0,-,0 |
        // .4--------3--------7--------8--------4
        // ..........|********|..................
        // ..........|RIGHT***|..................
        // ..........|N=+,0,0 |..................
        // ..........4--------8..................
        // front: 1-2-3-4 -> 0,1,2,3 for indices
        addVs(Normals.FRONT, 1, 2, 3, 4);
        // right: 4-3-7-8 -> 4,5,6,7 for indices
        addVs(Normals.RIGHT, 4, 3, 7, 8);
        // bottom: 4-8-5-1 -> 8,9,10,11 for indices
        addVs(Normals.BOTTOM, 4, 8, 5, 1);
        // left: 5-6-2-1 -> 12,13,14,15 for indices
        addVs(Normals.LEFT, 5, 6, 2, 1);
        // top: 7-6-2-3 -> 16,17,18,19 for indices
        addVs(Normals.TOP, 7, 6, 2, 3);
        // back: 8-7-6-5 -> 20,21,22,23 for indices
        addVs(Normals.BACK, 8, 7, 6, 5);

        return l.build();
    }

    private void addVs(Vector3d normal, int... indices) {
        for (int index : indices) {
            addV(index, normal);
        }
    }

    private void addV(int index, Vector3d normal) {
        index--;
        Vertex.Builder builder = original.get(index).toBuilder();
        // add texture if we're supposed to
        if (texture != null) {
            builder.texture(texture.get(textureIndex));
            textureIndex++;
        }
        l.add(builder.normal(normal).build());
    }

}
