package com.techshroom.unplanned.value;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Point {

    public static final Point of(int x, int y) {
        return new AutoValue_Point(x, y);
    }

    public abstract int getX();

    public abstract int getY();

    public final Point withX(int x) {
        return of(x, getY());
    }

    public final Point withY(int y) {
        return of(getX(), y);
    }

}
