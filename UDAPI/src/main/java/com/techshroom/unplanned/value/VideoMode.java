package com.techshroom.unplanned.value;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class VideoMode {

    public static final VideoMode create(int width, int height, int redBits,
            int greenBits, int blueBits, int refreshRate) {
        return new AutoValue_VideoMode(width, height, redBits, greenBits,
                blueBits, refreshRate);
    }

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract int getRedBits();

    public abstract int getGreenBits();

    public abstract int getBlueBits();

    public abstract int getRefreshRate();

}
