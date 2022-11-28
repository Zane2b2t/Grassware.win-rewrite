package me.alpha432.oyvey.event.events;

import me.alpha432.oyvey.event.bus.Event;

public class KeyEvent extends Event {
    public final int key;

    public KeyEvent(final int key) {
        this.key = key;
    }

    public int getKey() {
        return this.key;
    }
}

