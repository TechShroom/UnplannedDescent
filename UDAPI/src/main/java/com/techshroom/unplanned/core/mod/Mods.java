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
package com.techshroom.unplanned.core.mod;

import static com.techshroom.unplanned.core.util.Functional.printErrors;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.techshroom.unplanned.core.util.ClassPathHack;
import com.techshroom.unplanned.core.util.CompileGeneric;
import com.techshroom.unplanned.core.util.LUtils;
import com.techshroom.unplanned.core.util.Logging;
import com.techshroom.unplanned.core.util.Logging.LoggingGroup;

/**
 * Access point for mods. This is the class that handles all loading for mods.
 * 
 * @author Kenzie Togami
 */
public final class Mods {

    private static final Mod NULL_MOD = new SkeletalMod(
            ModMetadata.builder().idAndName("null").version("0.0.0")
                    .buildNumber(0).targetGameVersion("any").build()) {
        // Nothing to implement.
    };
    private static Multimap<ModProvider, Mod> providers;
    private static boolean loadedMods = false;

    /**
     * Load any mods in the class path, injecting the mods in the mod folder
     * into the classpath.
     */
    public static void findAndLoad() {
        if (hasLoadedMods()) {
            Logging.log("Reloading mods, may cause troubles",
                    LoggingGroup.WARNING);
            providers.values().forEach(Mod::unload);
            providers.clear();
        }
        Logging.log("UD Mod System starting...", LoggingGroup.DEBUG);
        if (!injectModsFolder()) {
            Logging.log(
                    "Not searching "
                            + new File(LUtils.ROOT, "mods").getAbsolutePath()
                            + " because it doesn't exist or is not a directory.",
                    LoggingGroup.WARNING);
        }
        List<ModProvider> providerList = ModLoader.loadModsFromClasspath();
        Logging.log("Loaded mod providers from classpath.", LoggingGroup.DEBUG);
        Multimap<String, ModMetadata> ids = ArrayListMultimap.create();
        providerList.forEach(
                p -> ids.put(p.getMetadata().getId(), p.getMetadata()));
        if (ids.keySet().stream().map(ids::get).anyMatch(x -> x.size() > 1)) {
            Set<String> toRemove = ids.asMap().entrySet().stream()
                    .filter(e -> e.getValue().size() < 2).map(Entry::getKey)
                    .collect(Collectors.toSet());
            toRemove.forEach(ids::removeAll);
            Logging.log("ID Conflicts detected, load failed",
                    LoggingGroup.ERROR);
            throw new ModIDConflictException(ids);
        }
        Logging.log("Loaded provider list, dumping", LoggingGroup.JUNK);
        providerList
                .forEach(x -> Logging.log("PROVIDER: " + x, LoggingGroup.JUNK));
        Logging.log("Creating mods via providers...", LoggingGroup.DEBUG);
        providers =
                HashMultimap
                        .create(Multimaps
                                .forMap(providerList.stream()
                                        .collect(Collectors.toMap(
                                                Function.identity(),
                                                printErrors(
                                                        p -> p.getModCreator()
                                                                .call(),
                                                        () -> NULL_MOD)))));
        providers.asMap().entrySet().stream()
                .filter(e -> e.getValue() == NULL_MOD).map(Entry::getKey)
                .forEach(providers::removeAll);
        Logging.log("Loading mods...", LoggingGroup.DEBUG);
        providers.values().stream().forEach(printErrors(Mod::load,
                CompileGeneric.<Consumer<Mod>> specify(Consumer.class)));
        loadedMods = true;
        Logging.log("UD Mod System loaded.", LoggingGroup.DEBUG);
    }

    private static boolean injectModsFolder() {
        String topLevel = LUtils.ROOT;
        File mods = new File(topLevel, "mods");
        if (!loadDirectory(mods)) {
            return false;
        }
        System.err.println("Injected '" + mods + "/**' into classpath.");
        return true;
    }

    private static boolean loadDirectory(File dir) {
        if (!dir.exists() || dir.isFile()) {
            return false;
        }

        File[] files = dir.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                loadDirectory(f);
                continue;
            }

            try {
                if (!ClassPathHack.hasFile(f)) {
                    ClassPathHack.addFile(f);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return true;
    }

    /**
     * Gets the list of loaded mods.
     * 
     * @return the list of loaded mods
     * @throws IllegalStateException
     *             If {@link #findAndLoad()} has not been called yet, and there
     *             are no mods loaded
     */
    public static List<Mod> getLoadedMods() {
        if (!hasLoadedMods()) {
            throw new IllegalStateException(
                    "Getting loaded mods list before loading mods!");
        }
        return ImmutableList.copyOf(providers.values());
    }

    /**
     * Returns {@code true} if mods have already been loaded.
     * 
     * @return {@code true} if mods have already been loaded
     */
    public static boolean hasLoadedMods() {
        return loadedMods;
    }
}
