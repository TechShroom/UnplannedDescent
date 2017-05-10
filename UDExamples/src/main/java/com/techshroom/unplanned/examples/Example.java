package com.techshroom.unplanned.examples;

public abstract class Example {

    public String getName() {
        return getClass().getSimpleName();
    }

    public abstract void run();

}
