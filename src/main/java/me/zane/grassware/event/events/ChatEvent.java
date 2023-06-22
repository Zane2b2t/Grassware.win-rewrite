package me.zane.grassware.event.events;

import me.zane.grassware.event.bus.Event;

public class ChatEvent extends Event {
    private final String msg;

    public ChatEvent(final String msg) {
        this.msg = msg;
    }

}

