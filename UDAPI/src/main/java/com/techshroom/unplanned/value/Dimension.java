package com.techshroom.unplanned.value;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Dimension {

    public static final Dimension of(int width, int height) {
        return new AutoValue_Dimension(width, height);
    }

    public abstract int getWidth();

    public abstract int getHeight();

    public final Dimension withWidth(int width) {
        return of(width, getHeight());
    }

    public final Dimension withHeight(int height) {
        return of(getWidth(), height);
    }

}
