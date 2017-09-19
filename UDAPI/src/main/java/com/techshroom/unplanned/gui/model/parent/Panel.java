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
package com.techshroom.unplanned.gui.model.parent;

import javax.swing.JPanel;

import com.techshroom.unplanned.gui.model.layout.HBoxLayout;
import com.techshroom.unplanned.gui.model.layout.VBoxLayout;

/**
 * Like {@link JPanel}.
 */
public class Panel extends GroupElementBase implements GroupElement {

    public static Panel hBox() {
        return hBox(0);
    }

    public static Panel hBox(double spacing) {
        Panel panel = new Panel();
        panel.setLayout(new HBoxLayout(spacing));
        return panel;
    }

    public static Panel vBox() {
        return vBox(0);
    }

    public static Panel vBox(double spacing) {
        Panel panel = new Panel();
        panel.setLayout(new VBoxLayout(spacing));
        return panel;
    }

}
