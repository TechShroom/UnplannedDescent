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

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.techshroom.unplanned.core.util.IOFunction;

/**
 * Represents a "loaded" resource. This may still be streamed from disk if it is
 * large enough, or it is specifically requested.
 * 
 * <p>
 * Only one of the {@code as*} or {@code use*} methods may be called. After the
 * first call, this resource becomes invalid, and all interaction must occur
 * with the new object.
 * </p>
 */
public class RawResource {

    private InputStream source;

    public RawResource(InputStream source) {
        this.source = source;
    }

    public <T> T useStream(IOFunction<InputStream, T> func) throws ResourceLoadException {
        checkState(source != null, "resource already used");
        try (InputStream s = source) {
            return func.apply(s);
        } catch (IOException e) {
            throw new ResourceLoadException(e);
        } finally {
            source = null;
        }
    }

    public <T> T useReader(IOFunction<Reader, T> func) throws ResourceLoadException {
        return useStream(s -> {
            return func.apply(new InputStreamReader(s, StandardCharsets.UTF_8));
        });
    }

    public String asString() throws ResourceLoadException {
        return useReader(CharStreams::toString);
    }

    public byte[] asBytes() throws ResourceLoadException {
        return useStream(ByteStreams::toByteArray);
    }
}
