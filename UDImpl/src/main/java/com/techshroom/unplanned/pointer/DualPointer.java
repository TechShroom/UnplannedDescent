package com.techshroom.unplanned.pointer;

import org.lwjgl.Pointer;

public class DualPointer extends PointerImpl implements Pointer {

    public static DualPointer wrap(long pointer) {
        return new DualPointer(pointer);
    }
    
    private DualPointer(long pointer) {
        super(pointer);
    }

}
