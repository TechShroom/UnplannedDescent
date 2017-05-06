package com.techshroom.unplanned.pointer;

public class DualPointer extends PointerImpl implements org.lwjgl.system.Pointer {

    public static DualPointer wrap(long pointer) {
        return new DualPointer(pointer);
    }

    private DualPointer(long pointer) {
        super(pointer);
    }

}
