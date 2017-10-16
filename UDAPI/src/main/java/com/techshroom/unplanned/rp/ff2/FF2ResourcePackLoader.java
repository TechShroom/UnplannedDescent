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
package com.techshroom.unplanned.rp.ff2;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.techshroom.unplanned.rp.RId;
import com.techshroom.unplanned.rp.ResourceLoadException;
import com.techshroom.unplanned.rp.ResourcePack;
import com.techshroom.unplanned.rp.ResourcePackLoaderType;
import com.techshroom.unplanned.rp.SimpleResourcePackLoader;

public class FF2ResourcePackLoader implements SimpleResourcePackLoader {

    @Override
    public ResourcePackLoaderType getType() {
        return ResourcePackLoaderType.FF2;
    }

    @Override
    public ResourcePack load(Path resourcePackFile) {
        // the "file" is actually a folder: [index, 0,1,2,3,...]
        FF2Index index = loadIndex(resourcePackFile.resolve("index"));
        for (int i = 0; i < index.getFileCount(); i++) {
            if (!Files.exists(resourcePackFile.resolve(String.valueOf(i)))) {
                throw new ResourceLoadException("Missing part of resource pack: " + i);
            }
        }
        return new FF2ResourcePack(resourcePackFile, index);
    }

    private FF2Index loadIndex(Path indexFile) {
        FF2Index.Builder idx = FF2Index.builder();
        try (DataInputStream data = new DataInputStream(new BufferedInputStream(Files.newInputStream(indexFile)))) {
            int entries = data.readInt();
            for (int i = 0; i < entries; i++) {
                RId id = RId.parse(data.readUTF());
                byte index = data.readByte();
                long offset = data.readLong();
                int size = data.readInt();
                idx.putValue(id, FF2Index.Value.create(index, offset, size));
            }
        } catch (IOException e) {
            throw new ResourceLoadException(e);
        }
        return idx.build();
    }

}
