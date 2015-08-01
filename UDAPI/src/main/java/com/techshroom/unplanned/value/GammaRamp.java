package com.techshroom.unplanned.value;

import static com.google.common.base.Preconditions.checkState;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class GammaRamp {

    public static final GammaRamp of(int[] red, int[] green, int[] blue,
            int size) {
        checkState(red.length == size, "red array was not of size %s", size);
        checkState(green.length == size, "green array was not of size %s", size);
        checkState(blue.length == size, "blue array was not of size %s", size);
        return new AutoValue_GammaRamp(red.clone(), green.clone(),
                blue.clone(), size);
    }

    public static final GammaRamp of(float exponent) {
        // adapted from GLFW source
        int[] values = new int[256];
        for (int i = 0; i < values.length; i++) {
            double value;

            // Calculate intensity
            value = i / 255.0;
            // Apply gamma curve
            value = Math.pow(value, 1.0 / exponent) * 65535.0 + 0.5;

            // Clamp to value range
            if (value > 65535.0) {
                value = 65535.0;
            }

            // unsigned short cast
            values[i] = ((int) value) & 0xFFFF;
        }
        return of(values, values, values, values.length);
    }

    public abstract int[] getRedChannel();

    public abstract int[] getGreenChannel();

    public abstract int[] getBlueChannel();

    public abstract int getSize();

}
