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
package com.techshroom.unplanned.blitter.textures.map;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.techshroom.unplanned.blitter.textures.TextureData;

/**
 * A collection of {@link TextureData}, keyed by Strings.
 */
public class TextureCollection {

    public static TextureCollection of() {
        return of(HashBiMap.create());
    }

    public static TextureCollection of(BiMap<TextureData, String> data) {
        checkArgument(data != null, "data is null");
        return new TextureCollection(data);
    }

    private final BiMap<TextureData, String> data;

    private TextureCollection(BiMap<TextureData, String> data) {
        this.data = data;
    }

    public BiMap<TextureData, String> getData() {
        return data;
    }

    public void put(TextureData key, String value) {
        data.inverse().put(value, key);
    }

    public String get(TextureData key) {
        return data.get(key);
    }

    public TextureData get(String key) {
        return data.inverse().get(key);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + data.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TextureCollection other = (TextureCollection) obj;
        if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.equals(other.data))
            return false;
        return true;
    }

}
