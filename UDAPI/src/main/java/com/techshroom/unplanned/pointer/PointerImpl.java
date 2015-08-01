package com.techshroom.unplanned.pointer;

import org.lwjgl.system.PointerWrapper;

public final class PointerImpl extends PointerWrapper {

    public static final PointerImpl wrap(long ptr) {
        return new PointerImpl(ptr);
    }

    private PointerImpl(long ptr) {
        super(ptr);
    }

}
