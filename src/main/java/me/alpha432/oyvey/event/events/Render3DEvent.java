package me.alpha432.oyvey.event.events;

import me.alpha432.oyvey.event.bus.Event;

public class Render3DEvent extends Event {
    public final float partialTicks;

    public Render3DEvent( final float partialTicks) {
        this.partialTicks = partialTicks;
    }
}

