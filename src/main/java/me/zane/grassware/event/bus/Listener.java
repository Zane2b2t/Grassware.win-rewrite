package me.zane.grassware.event.bus;

import java.lang.reflect.Method;


public final class Listener {
    public final Method method;
    public final Object object;
    public final Class<?> event;

    public Listener(final Method method, final Object object, final Class<?> event) {
        this.method = method;
        this.object = object;
        this.event = event;
    }
}