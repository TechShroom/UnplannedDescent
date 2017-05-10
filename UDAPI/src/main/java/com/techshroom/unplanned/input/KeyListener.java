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
package com.techshroom.unplanned.input;

import java.util.function.Consumer;

public interface KeyListener {
    
    static KeyListener pressed(Consumer<KeyEvent> delegate) {
        class KLPressed implements KeyListener {
            @Override
            public void onPressed(KeyEvent event) {
                delegate.accept(event);
            }
        }
        return new KLPressed();
    }
    
    static KeyListener released(Consumer<KeyEvent> delegate) {
        class KLReleased implements KeyListener {
            @Override
            public void onReleased(KeyEvent event) {
                delegate.accept(event);
            }
        }
        return new KLReleased();
    }
    
    static KeyListener tapped(Consumer<KeyEvent> delegate) {
        class KLTapped implements KeyListener {
            @Override
            public void onTapped(KeyEvent event) {
                delegate.accept(event);
            }
        }
        return new KLTapped();
    }
    
    static KeyListener repeated(Consumer<KeyEvent> delegate) {
        class KLRepeated implements KeyListener {
            @Override
            public void onRepeated(KeyEvent event) {
                delegate.accept(event);
            }
        }
        return new KLRepeated();
    }

    default void onPressed(KeyEvent event) {
    }

    default void onReleased(KeyEvent event) {
    }

    default void onTapped(KeyEvent event) {
    }

    default void onRepeated(KeyEvent event) {
    }

}