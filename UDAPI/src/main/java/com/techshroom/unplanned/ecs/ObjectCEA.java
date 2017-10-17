/*
 * This file is part of UnplannedDescent, licensed under the MIT License (MIT).
 *
 * Copyright (c) TechShroom Studios <https://techshroom.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.techshroom.unplanned.ecs;

import static com.google.common.base.Preconditions.checkState;

import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.api.set.primitive.IntSet;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;
import org.eclipse.collections.impl.factory.primitive.IntSets;

public class ObjectCEA implements CompEntAssoc {

    private static final class Entity {

        private final int id;
        private final MutableMap<ComponentField<?>, Object> fields = Maps.mutable.empty();

        public Entity(int id) {
            this.id = id;
        }

    }

    private final MutableMap<Component, MutableIntSet> componentLists = Maps.mutable.empty();
    private final MutableIntObjectMap<Entity> entities = IntObjectMaps.mutable.empty();

    private MutableIntSet cl(Component key, boolean add) {
        MutableIntSet val = componentLists.get(key);
        if (val == null) {
            val = IntSets.mutable.empty();
            if (add) {
                componentLists.put(key, val);
            }
        }
        return val;
    }

    private Entity addIfNeeded(int entityId) {
        return entities.getIfAbsentPut(entityId, () -> new Entity(entityId));
    }

    private void associate(Entity e, Component c) {
        cl(c, true).add(e.id);
        c.getFields().forEach(f -> {
            e.fields.put(f, f.getType().defaultValue);
        });
    }

    @Override
    public void associate(int entityId, Component component) {
        Entity e = addIfNeeded(entityId);
        associate(e, component);
    }

    @Override
    public void associate(int entityId, Component... component) {
        Entity e = addIfNeeded(entityId);
        for (Component c : component) {
            associate(e, c);
        }
    }

    @Override
    public void associate(int entityId, Iterable<Component> component) {
        Entity e = addIfNeeded(entityId);
        for (Component c : component) {
            associate(e, c);
        }
    }

    @Override
    public <T> void associate(int entityId, ComponentField<T> field, T value) {
        Entity e = entities.get(entityId);
        checkState(e != null, "entity %s is not associated yet!", entityId);
        checkState(e.fields.containsKey(field), "field %s is not associated with entity %s", field, entityId);
        e.fields.put(field, value);
    }

    @Override
    public <T> T get(int entityId, ComponentField<T> field) {
        Entity e = entities.get(entityId);
        checkState(e != null, "entity %s is not associated yet!", entityId);
        @SuppressWarnings("unchecked")
        T val = (T) e.fields.get(field);
        checkState(val != null, "field %s is not associated with entity %s", field, entityId);
        return val;
    }

    @Override
    public void disassociate(int entityId) {
        entities.removeKey(entityId);
    }

    @Override
    public IntSet getEntities(Component component) {
        return componentLists.get(component).freeze();
    }

}
