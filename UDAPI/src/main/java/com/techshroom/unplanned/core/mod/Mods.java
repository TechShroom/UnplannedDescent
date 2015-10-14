package com.techshroom.unplanned.core.mod;

import static com.techshroom.unplanned.core.util.Functional.printErrors;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
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

    private static final Mod NULL_MOD = new Mod() {
        // does nothing!
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
        Logging.log("Creating mods via providers...", LoggingGroup.DEBUG);
        Multimap<String, ModMetadata> ids = ArrayListMultimap.create();
        providerList.forEach(
                p -> ids.put(p.getMetadata().getId(), p.getMetadata()));
        if (ids.keySet().stream().map(ids::get).anyMatch(x -> x.size() > 1)) {
            ids.asMap().entrySet().stream().filter(e -> e.getValue().size() < 2)
                    .map(Entry::getKey).collect(Collectors.toSet())
                    .forEach(ids::removeAll);
            Logging.log("ID Conflicts detected, load failed",
                    LoggingGroup.ERROR);
            throw new ModIDConflictException(ids);
        }
        Logging.log("Loaded provider list, dumping", LoggingGroup.JUNK);
        providerList
                .forEach(x -> Logging.log("PROVIDER: " + x, LoggingGroup.JUNK));
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
