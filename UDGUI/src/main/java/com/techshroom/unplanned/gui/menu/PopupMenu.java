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

import com.techshroom.unplanned.gui.model.Button;
import com.techshroom.unplanned.gui.model.layout.VBoxLayout;
import com.techshroom.unplanned.gui.model.parent.VBox;
import com.techshroom.unplanned.window.Window;
import com.techshroom.unplanned.window.WindowSettings;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * An extremely lightweight popup menu. The menu resides inside of a window that
 * is undecorated.
 */
public class PopupMenu {

    private static final WindowSettings POPUP_SETTINGS = WindowSettings.builder()
            .withDialogDefaults()
            .undecorated(true)
            .build();

    private final Menu menu;
    private final Window window;
    private PopupMenu submenu;
    
    private final ChangeListener<MenuItem> changeListener = new ChangeListener<MenuItem>() {
        
        @Override
        public void changed(ObservableValue<? extends MenuItem> observable, MenuItem oldValue, MenuItem newValue) {
        }
    };

    public PopupMenu(Menu menu) {
        this.menu = menu;
        this.window = POPUP_SETTINGS.createWindow();
        fillWindow();
    }

    private void fillWindow() {
        VBox menuItems = new VBox();
        menu.getMenuItems().forEach(item -> {
            Button button = new Button();
            button.setText(item.getName());
        });
    }

    public void setVisible(boolean visible) {
        boolean isVisible = window.isVisible();
        if (visible == isVisible) {
            return;
        }

        if (visible) {
            menu.getSelectionModel().selectedItemProperty().addListener(changeListener);
        } else {
            // de-select items
            menu.getSelectionModel().clearSelection();
            menu.getSelectionModel().selectedItemProperty().removeListener(changeListener);
        }
        window.setVisible(visible);
    }

}
