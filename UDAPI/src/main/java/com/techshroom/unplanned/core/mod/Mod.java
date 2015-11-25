package com.techshroom.unplanned.core.mod;

/**
 * The mod interface.
 * 
 * @author Kenzie Togami
 */
public interface Mod {
    
    ModMetadata getMetadata();

    default void load() {
        // do nothing
    }

    default void gameInit(Game game) {
        // do nothing
    }

    default void unload() {
        // do nothing
    }

}
