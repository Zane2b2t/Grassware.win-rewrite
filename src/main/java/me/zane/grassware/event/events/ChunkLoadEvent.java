package me.zane.grassware.event.events;

import me.zane.grassware.event.bus.Event;

public class ChunkLoadEvent extends Event {
    public long delay;

    public ChunkLoadEvent(final long delay) {
        this.delay = delay;
    }
}
