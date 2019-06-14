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

package com.techshroom.unplanned.blitter.font;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

public class FontCache {

    private static String key(FontDescriptor fd) {
        return fd.getName() + "@" + fd.getLocation();
    }

    private final Map<String, Font> loadedFonts = new HashMap<>();
    private final FontLoader loader;

    public FontCache(FontLoader loader) {
        this.loader = loader;
    }

    public Font getOrLoadFont(FontDescriptor fd) {
        String key = key(fd);
        Font font = loadedFonts.get(key);
        if (font == null) {
            try {
                font = loader.loadFont(fd);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            loadedFonts.put(key, font);
        }
        return font;
    }

    public void deleteFont(FontDescriptor fd) {
        Font font = loadedFonts.remove(key(fd));
        if (font != null) {
            font.delete();
        }
    }

    public void deleteFont(Font font) {
        loadedFonts.values().remove(font);
        font.delete();
    }

}
