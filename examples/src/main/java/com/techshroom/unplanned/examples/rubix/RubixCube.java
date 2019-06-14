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

package com.techshroom.unplanned.examples.rubix;

import com.google.auto.service.AutoService;
import com.techshroom.unplanned.examples.Example;
import com.techshroom.unplanned.window.Window;

@AutoService(Example.class)
public class RubixCube extends Example {

    private Window window;

    @Override
    public void run(Window window) {
        this.window = window;
        window.setTitle("Rubix Cube");

        window.getGraphicsContext().makeActiveContext();
        window.setVsyncOn(true);
        window.setVisible(true);

        CubeHandler handler = new CubeHandler();

        while (!window.isCloseRequested()) {
            window.processEvents();
            window.getGraphicsContext().clearGraphicsState();

            window.getGraphicsContext().swapBuffers();
        }

        window.destroy();
    }

}
