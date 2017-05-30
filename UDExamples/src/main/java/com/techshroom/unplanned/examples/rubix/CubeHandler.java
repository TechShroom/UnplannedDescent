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

import java.util.Arrays;
import java.util.List;

import com.flowpowered.math.vector.Vector3i;
import com.techshroom.unplanned.geometry.Direction;

public class CubeHandler {

    // quads for each face
    private final List<Quad> quads = Arrays.asList(new Quad[54]);

    {
        // setup normal rubix cube

        List<Quad> face;
        // red top
        Vector3i red = new Vector3i(0xB7, 0x12, 0x34);
        face = getSide(Direction.UP);
        for (int i = 0; i < face.size(); i++) {
            face.set(i, new Quad(red));
        }

        // orange bottom
        Vector3i orange = new Vector3i(0xFF, 0x58, 0);
        face = getSide(Direction.DOWN);
        for (int i = 0; i < face.size(); i++) {
            face.set(i, new Quad(orange));
        }

        // white left
        Vector3i white = new Vector3i(255, 255, 255);
        face = getSide(Direction.LEFT);
        for (int i = 0; i < face.size(); i++) {
            face.set(i, new Quad(white));
        }

        // yellow right
        Vector3i yellow = new Vector3i(0xFF, 0xD5, 0);
        face = getSide(Direction.RIGHT);
        for (int i = 0; i < face.size(); i++) {
            face.set(i, new Quad(yellow));
        }

        // green front
        Vector3i green = new Vector3i(0, 0x9B, 0x48);
        face = getSide(Direction.FRONT);
        for (int i = 0; i < face.size(); i++) {
            face.set(i, new Quad(green));
        }

        // blue back
        Vector3i blue = new Vector3i(0, 0x46, 0xAD);
        face = getSide(Direction.BACK);
        for (int i = 0; i < face.size(); i++) {
            face.set(i, new Quad(blue));
        }
    }

    private void setFace(Direction dir, Vector3i color) {
        List<Quad> face = getSide(dir);
        for (int i = 0; i < face.size(); i++) {
            Quad q = new Quad(color);
            q.setRotation(dir.veci.toFloat());
            face.set(i, q);
        }
    }

    public List<Quad> getQuads() {
        return quads;
    }

    public List<Quad> getSide(Direction side) {
        int start = side.ordinal() * 9;
        return quads.subList(start, start + 9);
    }

}
