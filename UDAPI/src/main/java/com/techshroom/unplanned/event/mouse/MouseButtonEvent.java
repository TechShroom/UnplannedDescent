package com.techshroom.unplanned.event.mouse;

import java.util.Collection;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.techshroom.unplanned.input.KeyModifier;
import com.techshroom.unplanned.input.Mouse;

@AutoValue
public abstract class MouseButtonEvent implements MouseEvent {

    public static MouseButtonEvent create(Mouse source, int button, boolean down, Collection<KeyModifier> modifiers) {
        return new AutoValue_MouseButtonEvent(source, button, down, Sets.immutableEnumSet(modifiers));
    }

    public abstract int getButton();

    public abstract boolean isDown();

    public abstract ImmutableSet<KeyModifier> getModifiers();

}
