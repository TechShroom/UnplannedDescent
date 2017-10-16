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
package com.techshroom.unplanned.app;

import static com.google.common.collect.ImmutableList.toImmutableList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;
import com.techshroom.unplanned.core.util.CombinedId;
import com.techshroom.unplanned.rp.BasicRPCategory;
import com.techshroom.unplanned.rp.LangResource;
import com.techshroom.unplanned.rp.RId;
import com.techshroom.unplanned.rp.ResourceLoadException;
import com.techshroom.unplanned.rp.ResourcePack;
import com.techshroom.unplanned.rp.ResourcePackLoader;
import com.techshroom.unplanned.rp.ResourcePackLoaderType;
import com.techshroom.unplanned.rp.ResourcePackLoaders;

/**
 * An App is a representation of some application that uses Unplanned Descent.
 * It is used to obtain various sub-components that rely on common app IDs and
 * names.
 */
@AutoValue
public abstract class App {

    /**
     * Resource ID used when finding "generic" resources, like the default lang
     * files.
     */
    public static final String GENERIC_APP_RESOURCE_ID = "app";

    private static final Path DEFAULT_APP_BASE_DIR = Paths.get(".");

    public static Builder builder() {
        return new AutoValue_App.Builder()
                .RPLoaderType(ResourcePackLoaderType.FF2)
                .baseDir(DEFAULT_APP_BASE_DIR);
    }

    @AutoValue.Builder
    public interface Builder {

        Builder id(CombinedId id);

        Builder RPLoaderType(ResourcePackLoaderType type);

        Builder baseDir(Path baseDir);

        App build();

    }

    App() {
    }

    public abstract CombinedId getId();

    public abstract ResourcePackLoaderType getRPLoaderType();

    public abstract Path getBaseDir();

    public final ResourcePackLoader getRPLoader() {
        return ResourcePackLoaders.getLoader(getRPLoaderType());
    }

    @Memoized
    public ResourcePack getResourcePack() {
        Path baseRPDir = getFileSystem().getDir("resourcepacks", true);
        List<Path> paths;
        try (Stream<Path> iter = Files.list(baseRPDir)) {
            paths = iter.collect(toImmutableList());
        } catch (IOException e) {
            throw new ResourceLoadException(e);
        }
        return getRPLoader().load(getId().titleCase(), paths);
    }

    @Memoized
    public AppFileSystem getFileSystem() {
        return new AppFileSystem(getBaseDir());
    }

    public final RId rId(String rId) {
        return RId.parse(rId, getId().underscoreSeparated());
    }

    public final RId rId(String category, String identifier) {
        return RId.from(getId().underscoreSeparated(), category, identifier);
    }

    /**
     * Convenient accessor for the name, handles I18N.
     * 
     * <p>
     * The language file should be found at {@code ${DOMAIN}:lang/app}.
     * 
     * The key for the name is "name".
     * </p>
     * 
     * @return the translated name for the app
     */
    public final String getName() {
        ResourcePack pack = getResourcePack();
        RId rId = rId(BasicRPCategory.LANG.catStr, GENERIC_APP_RESOURCE_ID);
        return new LangResource(rId, pack).translate(Locale.getDefault(), "name");
    }

}
