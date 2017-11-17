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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.util.Iterator;
import java.util.Random;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.api.set.primitive.IntSet;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.impl.factory.Lists;
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

    private final ImmutableList<CSystem> systems;
    private final ImmutableMap<Component, MutableIntSet> componentLists;
    private final MutableIntObjectMap<Entity> entities = IntObjectMaps.mutable.empty();

    ObjectCEA(ImmutableList<CSystem> csys, ImmutableList<Component> comps) {
        this.systems = Lists.immutable.withAll(csys);
        MutableMap<Component, MutableIntSet> cl = Maps.mutable.empty();
        this.systems.forEach(cs -> {
            for (Component c : cs.getComponents()) {
                cl.put(c, IntSets.mutable.empty());
            }
        });
        for (Component c : comps) {
            cl.put(c, IntSets.mutable.empty());
        }
        componentLists = cl.toImmutable();
    }

    private MutableIntSet cl(Component key) {
        MutableIntSet val = componentLists.get(key);
        checkState(val != null, "Component %s is not part of this CEA", key.getId());
        return val;
    }

    private static final Random RANDOM = new Random();

    private static int randomId() {
        return Math.abs(Long.hashCode(java.lang.System.nanoTime()) ^ RANDOM.nextInt());
    }

    private Entity add() {
        Entity e = new Entity(randomId());
        entities.put(e.id, e);
        return e;
    }

    private void associate(Entity e, Component c) {
        cl(c).add(e.id);
        c.getFields().forEach((name, f) -> {
            e.fields.put(f, f.getType().defaultValue);
        });
    }

    @Override
    public int newEntity(Component component) {
        Entity e = add();
        associate(e, component);
        return e.id;
    }

    @Override
    public int newEntity(Component... component) {
        Entity e = add();
        for (Component c : component) {
            associate(e, c);
        }
        return e.id;
    }

    @Override
    public int newEntity(Iterable<Component> component) {
        Entity e = add();
        for (Component c : component) {
            associate(e, c);
        }
        return e.id;
    }

    @Override
    public <T> void set(int entityId, ComponentField<T> field, T value) {
        checkArgument(value != null, "values may not be null");
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
    public void remove(int entityId) {
        entities.removeKey(entityId);
        componentLists.forEach(s -> s.remove(entityId));
    }

    @Override
    public IntSet getEntities(Component component) {
        return getEntitiesNoFreeze(component).freeze();
    }

    private MutableIntSet getEntitiesNoFreeze(Component component) {
        return componentLists.getIfAbsent(component, IntSets.mutable::empty);
    }

    @Override
    public IntSet getEntities(Iterable<Component> components) {
        Iterator<Component> iter = components.iterator();
        if (!iter.hasNext()) {
            return IntSets.immutable.empty();
        }
        MutableIntSet intsersection = IntSets.mutable.ofAll(getEntitiesNoFreeze(iter.next()));
        while (iter.hasNext()) {
            IntSet next = getEntitiesNoFreeze(iter.next());
            intsersection.retainAll(next);
        }
        return intsersection.freeze();
    }

    @Override
    public boolean hasComponent(int entityId, Component component) {
        return componentLists.get(component).contains(entityId);
    }

    @Override
    public boolean hasEntity(int entityId) {
        return entities.containsKey(entityId);
    }

    @Override
    public void tick(long nano) {
        systems.forEach(sys -> sys.processList(this, nano));
    }

}
