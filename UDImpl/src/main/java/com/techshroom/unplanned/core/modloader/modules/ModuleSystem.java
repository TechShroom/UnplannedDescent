package com.techshroom.unplanned.core.modloader.modules;

import java.util.ArrayList;
import java.util.List;

import com.techshroom.unplanned.core.modloader.IMod;
import com.techshroom.unplanned.core.modloader.Mods;
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
		List<IMod> loaded = Mods.getLoadedMods();
		for (IMod m : loaded) {
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
	public static <T extends Module> T[] getRegisteredModules(Class<T> moduleClass) {
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
