package me.alpha432.oyvey.event.events;

import me.alpha432.oyvey.event.bus.Event;

public class ChunkLoadEvent extends Event {
    public long delay;

    public ChunkLoadEvent(final long delay){
        this.delay = delay;
    }
}
