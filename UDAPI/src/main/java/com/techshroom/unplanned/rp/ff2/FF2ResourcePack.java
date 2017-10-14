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

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import com.google.common.io.ByteStreams;
import com.techshroom.unplanned.core.util.files.FileRetentionPolicies;
import com.techshroom.unplanned.core.util.files.FileRetentionPolicy;
import com.techshroom.unplanned.rp.NoComponentResourcePack;
import com.techshroom.unplanned.rp.RId;
import com.techshroom.unplanned.rp.Resource;
import com.techshroom.unplanned.rp.ResourceLoadException;
import com.techshroom.unplanned.rp.ResourceType;

/**
 * FF2 resource pack.
 */
class FF2ResourcePack implements NoComponentResourcePack {

    // we are free to change this as performance metrics prove which is best
    private final FileRetentionPolicy fileManager = FileRetentionPolicies.singleMemoryMappedFile();

    private final Path folderSource;
    private final FF2Index index;

    public FF2ResourcePack(Path folderSource, FF2Index index) {
        this.folderSource = folderSource;
        this.index = index;
    }

    @Override
    public String getId() {
        return folderSource.getFileName().toString();
    }

    @Override
    public <R extends Resource> R loadResource(RId id, ResourceType<R> type) throws ResourceLoadException {
        FF2Index.Value val = index.getData(id);
        checkArgument(val.getType() == type, "wrong type %s, expected %s", type, val.getType());
        try {
            return type.create(this, id, () -> {
                Path file = folderSource.resolve(String.valueOf(val.getIndex()));
                InputStream stream = fileManager.openStream(file);
                stream.skip(val.getOffset());
                return ByteStreams.limit(stream, val.getSize());
            });
        } catch (IOException e) {
            throw new ResourceLoadException(e);
        }
    }

}
