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
package com.techshroom.unplanned.rp;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.techshroom.unplanned.core.util.CompileGeneric;
import com.techshroom.unplanned.core.util.CompileGeneric.ClassCompileGeneric;
import com.techshroom.unplanned.core.util.FakeEnum;
import com.techshroom.unplanned.core.util.IOSupplier;

/**
 * Pseudo-enum for a type of resource.
 */
public abstract class ResourceType<R extends Resource> extends FakeEnum<ResourceType<R>> {

    public static List<ResourceType<?>> values() {
        @SuppressWarnings("rawtypes")
        ClassCompileGeneric<ResourceType> cg = CompileGeneric.specify(ResourceType.class);
        @SuppressWarnings("unchecked")
        List<ResourceType<?>> values = (List<ResourceType<?>>) values(cg);
        return values;
    }

    public static final ResourceType<RawResource> RAW = new ResourceType<RawResource>("RAW") {

        @Override
        public RawResource create(ResourcePack creator, RId resourceId, IOSupplier<InputStream> streamCreator) throws IOException {
            return new RawResource(streamCreator.get());
        }
    };
    public static final ResourceType<LangResource> LANG = new ResourceType<LangResource>("LANG") {

        @Override
        public LangResource create(ResourcePack creator, RId resourceId, IOSupplier<InputStream> streamCreator) throws IOException {
            return new LangResource(resourceId, creator);
        }
    };

    private ResourceType(String name) {
        super(name);
    }

    public abstract R create(ResourcePack creator, RId resourceId, IOSupplier<InputStream> streamCreator) throws IOException;

}
