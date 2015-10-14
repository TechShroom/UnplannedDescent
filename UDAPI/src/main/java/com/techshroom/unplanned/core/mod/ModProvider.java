package com.techshroom.unplanned.core.mod;

import java.util.concurrent.Callable;

public interface ModProvider {

    /**
     * Returns the metadata for the mod.
     * 
     * @return the metadata for the mod
     */
            ModMetadata getMetadata();

    /**
     * Returns a callable that can make the mod. This allows for a variety of
     * implementations, all that is required is that a unique instance is
     * returned for each {@linkplain Callable#call() call} on the callable.
     * 
     * @return a callable that can make the mod
     */
            Callable<Mod> getModCreator();

}
