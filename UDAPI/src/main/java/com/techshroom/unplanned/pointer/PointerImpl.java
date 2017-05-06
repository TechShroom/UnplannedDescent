package com.techshroom.unplanned.pointer;

public class PointerImpl implements Pointer {

    public static PointerImpl wrap(long ptr) {
        return new PointerImpl(ptr);
    }

    protected final long pointer;

    protected PointerImpl(long pointer) {
        if (pointer == 0) {
            throw new NullPointerException();
        }

        this.pointer = pointer;
    }

    @Override
    public final long address() {
        return this.pointer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Pointer))
            return false;

        Pointer that = (Pointer) o;

        return this.pointer == that.address();

    }

    @Override
    public int hashCode() {
        return (int) (this.pointer ^ (this.pointer >>> 32));
    }

    @Override
    public String toString() {
        return String.format("%s pointer [0x%X]", getClass().getSimpleName(),
                this.pointer);
    }

}
