package com.techshroom.unplanned.event.mouse;

import com.flowpowered.math.vector.Vector2d;
import com.google.auto.value.AutoValue;
import com.techshroom.unplanned.input.Mouse;

@AutoValue
public abstract class MouseMoveEvent implements MouseEvent {

    public static MouseMoveEvent create(Mouse source, double x, double y) {
        return new AutoValue_MouseMoveEvent(source, new Vector2d(x, y));
    }

    MouseMoveEvent() {
    }

    public abstract Vector2d getPosition();

}
