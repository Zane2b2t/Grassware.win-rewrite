package me.alpha432.oyvey.event.events;

import me.alpha432.oyvey.event.bus.Event;

public class ChatEvent extends Event {
    private final String msg;

    public ChatEvent(final String msg) {
        this.msg = msg;
    }
}

