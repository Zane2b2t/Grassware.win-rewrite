package me.zane.grassware.event.events;

import me.zane.grassware.event.bus.Event;

public class KeyEvent extends Event {
    public final int key;

    public KeyEvent(final int key) {
        this.key = key;
    }

    public int getKey() {
        return this.key;
    }
}

