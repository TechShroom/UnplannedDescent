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
package com.techshroom.unplanned.gui.menu;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.SingleSelectionModel;

/**
 * A menu is a collection of {@link MenuItem MenuItems}, arranged vertically.
 */
public class Menu extends MenuItem {

    private final List<MenuItem> menuItems = new ArrayList<>();
    private SingleSelectionModel<MenuItem> selectionModel = new SingleSelectionModel<MenuItem>() {

        @Override
        protected MenuItem getModelItem(int index) {
            return menuItems.get(index);
        }

        @Override
        protected int getItemCount() {
            return menuItems.size();
        }
    };

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setSelectionModel(SingleSelectionModel<MenuItem> selectionModel) {
        this.selectionModel = selectionModel;
    }

    public SingleSelectionModel<MenuItem> getSelectionModel() {
        return selectionModel;
    }

}
