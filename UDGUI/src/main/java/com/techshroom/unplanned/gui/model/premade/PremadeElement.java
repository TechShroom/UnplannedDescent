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
package com.techshroom.unplanned.gui.model.premade;

import java.util.Optional;

import com.techshroom.unplanned.gui.Dialog;
import com.techshroom.unplanned.gui.model.Size;
import com.techshroom.unplanned.gui.model.SizeValue;
import com.techshroom.unplanned.gui.model.parent.ParentElementBase;
import com.techshroom.unplanned.window.WindowSettings;

public abstract class PremadeElement<T> extends ParentElementBase {

    {
        setPreferredSize(Size.of(SizeValue.percent(100)));
    }

    private T result;

    public Optional<T> showInDialog(WindowSettings dialogSettings) {
        Dialog dialog = new Dialog(dialogSettings);
        dialog.getRootElement().addChild(this);
        dialog.show(rm -> {
        });
        return getResult();
    }

    protected void setResult(T result) {
        this.result = result;
    }

    protected T getRawResult() {
        return result;
    }

    public final Optional<T> getResult() {
        return Optional.ofNullable(result);
    }

}
