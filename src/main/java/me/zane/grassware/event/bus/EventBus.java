package me.zane.grassware.event.bus;

import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

public final class EventBus {
    private final CopyOnWriteArrayList<Listener> listeners;

    public EventBus() {
        listeners = new CopyOnWriteArrayList<>();
    }

    public void registerListener(final Object object) {
        listeners(object);
    }

    public void unregisterListener(final Object object) {
        listeners.removeIf(listener -> listener.object == object);
    }

    private void listeners(final Object object) {
        final Class<?> c = object.getClass();
        Arrays.stream(c.getDeclaredMethods()).forEach(method -> {
            if (method.isAnnotationPresent(EventListener.class)) {
                final Class<?>[] parameterTypes = method.getParameterTypes();
                listeners.add(new Listener(method, object, parameterTypes[0]));
            }
        });
    }

    public void invoke(final Event event) {
        listeners.stream().filter(listener -> listener.event.equals(event.getClass())).forEach(listener -> {
            try {
                listener.method.invoke(listener.object, event);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }
}