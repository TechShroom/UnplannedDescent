package com.techshroom.unplanned.core.modloader.modules;

import java.util.ArrayList;
import java.util.List;

import com.techshroom.unplanned.core.modloader.IMod;
import com.techshroom.unplanned.core.modloader.Mods;
import com.techshroom.unplanned.core.util.BetterArrays;

public final class ModuleSystem {

	private static ArrayList<IModule> modules = new ArrayList<IModule>();
	private static boolean loaded_modules = false;

	private ModuleSystem() {
		throw new AssertionError("Please don't create the module system.");
	}

	public static void loadModulesFromMods() {
		if (!Mods.hasLoadedMods()) {
			Mods.findAndLoad();
		}
		List<IMod> loaded = Mods.getLoadedMods();
		for (IMod m : loaded) {
			if (m instanceof IModule) {
				IModule im = (IModule) m;
				modules.add(im);
			}
		}
	}

	/**
	 * Gets the registered modules for the given module class.
	 * 
	 * @param moduleClass
	 *            - the module class
	 * @return an array of {@link IModule}s that extend <tt>moduleClass</tt>
	 */
	public static <T extends IModule> T[] getRegisteredModules(
			Class<T> moduleClass) {
		T[] array = BetterArrays.newArray(moduleClass, 0);
		if (moduleClass == null) {
			throw new IllegalArgumentException("Module class cannot be null");
		}
		ArrayList<IModule> matched = new ArrayList<IModule>();
		for (IModule m : modules) {
			if (moduleClass.isInstance(m)) {
				matched.add(m);
			}
		}
		return matched.toArray(array);
	}

	public static boolean hasLoadedModules() {
		return loaded_modules;
	}
}
