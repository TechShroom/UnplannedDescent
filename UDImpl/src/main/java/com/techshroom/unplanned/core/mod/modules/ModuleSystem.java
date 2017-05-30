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
package com.techshroom.unplanned.core.mod.modules;

import java.util.ArrayList;
import java.util.List;

import com.techshroom.unplanned.core.mod.Mod;
import com.techshroom.unplanned.core.mod.Mods;
import com.techshroom.unplanned.core.util.Arrays2;

/**
 * The module system handles all of the additions to UD that are optional.
 * Modules are loaded via the mods system, but have a different subclass.
 * 
 * @author Kenzie Togami
 */
public final class ModuleSystem {

    private static ArrayList<Module> modules = new ArrayList<Module>();
    private static boolean loadedModules = false;

    private ModuleSystem() {
        throw new AssertionError("Please don't create the module system.");
    }

    /**
     * Load modules from {@link Mods#getLoadedMods()}, loading mods if they have
     * not been loaded yet.
     */
    public static void loadModulesFromMods() {
        if (!Mods.hasLoadedMods()) {
            Mods.findAndLoad();
        }
        List<Mod> loaded = Mods.getLoadedMods();
        for (Mod m : loaded) {
            if (m instanceof Module) {
                Module im = (Module) m;
                modules.add(im);
            }
        }
    }

    /**
     * Gets the registered modules for the given module class.
     * 
     * @param moduleClass
     *            - the module class
     * @return an array of {@link Module}s that extend <tt>moduleClass</tt>
     */
    public static <T extends Module> T[]
            getRegisteredModules(Class<T> moduleClass) {
        T[] array = Arrays2.newArray(moduleClass, 0);
        if (moduleClass == null) {
            throw new IllegalArgumentException("Module class cannot be null");
        }
        ArrayList<Module> matched = new ArrayList<Module>();
        for (Module m : modules) {
            if (moduleClass.isInstance(m)) {
                matched.add(m);
            }
        }
        return matched.toArray(array);
    }

    /**
     * Return {@code true} if modules have been loaded.
     * 
     * @return {@code true} if modules have been loaded
     */
    public static boolean hasLoadedModules() {
        return loadedModules;
    }
}
