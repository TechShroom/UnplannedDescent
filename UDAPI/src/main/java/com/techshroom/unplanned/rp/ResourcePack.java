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
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * Resource pack representation.
 */
public interface ResourcePack {

    default <R extends Resource> R loadResource(RId id, ResourceType<R> type) throws ResourceLoadException {
        if (getComponentPacks().isEmpty()) {
            throw new ResourceNotFoundException(id, "no component packs");
        }
        // special case lang files -- they like to look at the sub-components
        if (type == ResourceType.LANG) {
            try {
                return type.create(this, id, () -> {
                    throw new IOException("Irresponsible lang file...");
                });
            } catch (IOException e) {
                // lang files shouldn't need streams.
                throw new AssertionError("Not possible if everything is working correctly.", e);
            }
        }
        ImmutableMap.Builder<ResourcePack, ResourceLoadException> suppressed = ImmutableMap.builder();
        boolean allNotFound = true;
        for (ResourcePack pack : getComponentPacks()) {
            try {
                
            } catch (ResourceLoadException e) {
                suppressed.put(pack, e);
                if (!(e instanceof ResourceNotFoundException)) {
                    allNotFound = false;
                }
            }
        }
        ImmutableMap<ResourcePack, ResourceLoadException> errors = suppressed.build();
        if (allNotFound) {
            // just collect reasons
            String reasons = errors.entrySet().stream()
                    .map(e -> Maps.immutableEntry(e.getKey().getId(), (ResourceNotFoundException) e.getValue()))
                    .map(e -> {
                        // $PACK_ID=$REASON
                        return e.getKey() + "=" + e.getValue().getReason();
                    }).collect(Collectors.joining("\n"));
            throw new ResourceNotFoundException(id, "Component Pack Reasons: [" + reasons + "]");
        } else {
            // collect messages!
            String reasons = errors.entrySet().stream()
                    .map(e -> Maps.immutableEntry(e.getKey().getId(), e.getValue()))
                    .map(e -> {
                        // $PACK_ID=$MESSAGE
                        return e.getKey() + "=" + e.getValue().getMessage();
                    }).collect(Collectors.joining("\n"));
            throw new ResourceNotFoundException(id, "Component Pack Errors: [" + reasons + "]");
        }
    }
    
    String getId();

    /**
     * Returns the component packs of this resource pack.
     * 
     * <p>
     * Component packs are used to resolve resources. Starting from the first
     * pack, the list is iterated over until the correct resource is found. A
     * resource pack may have no component packs if it implements loadResource
     * in a different way.
     * </p>
     * 
     * @return the component packs
     */
    ImmutableList<ResourcePack> getComponentPacks();

}
