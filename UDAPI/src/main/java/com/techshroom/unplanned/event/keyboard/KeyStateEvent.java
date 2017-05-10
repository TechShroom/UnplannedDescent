package com.techshroom.unplanned.event.keyboard;

import java.util.Collection;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.techshroom.unplanned.input.Key;
import com.techshroom.unplanned.input.KeyModifier;
import com.techshroom.unplanned.input.Keyboard;

@AutoValue
public abstract class KeyStateEvent implements KeyEvent {

    public static KeyStateEvent create(Keyboard source, Key key, KeyState state, Collection<KeyModifier> modifiers) {
        return new AutoValue_KeyStateEvent(source, key, state, Sets.immutableEnumSet(modifiers));
    }

    KeyStateEvent() {
    }

    public abstract KeyState getState();

    public abstract ImmutableSet<KeyModifier> getModifiers();

    public final boolean is(Key key, KeyState state) {
        return key == getKey() && state == getState();
    }

}
