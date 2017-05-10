package com.techshroom.unplanned.event.mouse;

import com.flowpowered.math.vector.Vector2d;
import com.google.auto.value.AutoValue;
import com.techshroom.unplanned.input.Mouse;

@AutoValue
public abstract class MouseScrollEvent implements MouseEvent {

    public static MouseScrollEvent create(Mouse source, double dx, double dy) {
        return new AutoValue_MouseScrollEvent(source, new Vector2d(dx, dy));
    }

    MouseScrollEvent() {
    }

    public abstract Vector2d getMovement();

}
