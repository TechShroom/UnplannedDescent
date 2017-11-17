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
package com.techshroom.unplanned.event.queue.guava;

import static com.google.common.base.Preconditions.checkState;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.Deque;

import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;

import com.google.common.eventbus.EventBus;
import com.techshroom.unplanned.event.queue.EventQueue;
import com.techshroom.unplanned.event.queue.Subscription;

public class GuavaEventQueue implements EventQueue {

    private static final class Sub implements Subscription {

        private final WeakReference<GuavaEventQueue> owner;

        public Sub(GuavaEventQueue owner) {
            this.owner = new WeakReference<>(owner);
        }

        private GuavaEventQueue getOwner() {
            GuavaEventQueue o = owner.get();
            checkState(o != null, "zombie sub");
            return o;
        }

        @Override
        public void drainEvents() {
            getOwner().drainEvents(this);
        }

        @Override
        public void unsubscribe() {
            getOwner().unsubscribe(this);
        }

    }

    private final MutableMap<Subscription, EventBus> busMap = Maps.mutable.empty();
    private final MutableMap<Subscription, Deque<Object>> eventsMap = Maps.mutable.empty();

    @Override
    public Subscription subscribe(Object object) {
        Subscription sub = new Sub(this);
        EventBus eventBus = new EventBus(object.toString());
        eventBus.register(object);
        busMap.put(sub, eventBus);
        eventsMap.put(sub, new ArrayDeque<>());
        return sub;
    }

    @Override
    public void post(Object event) {
        eventsMap.forEach(queue -> queue.addLast(event));
    }

    private void drainEvents(Subscription sub) {
        EventBus bus = busMap.get(sub);
        checkState(bus != null, "subscriber has been unsubscribed");
        Deque<Object> events = eventsMap.get(sub);
        while (!events.isEmpty()) {
            bus.post(events.removeFirst());
        }
    }

    private void unsubscribe(Subscription sub) {
        busMap.remove(sub);
        eventsMap.remove(sub);
    }

}
