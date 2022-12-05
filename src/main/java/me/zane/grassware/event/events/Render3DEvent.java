package me.zane.grassware.event.events;

import me.zane.grassware.event.bus.Event;

public class Render3DEvent extends Event {
    public final float partialTicks;

    public Render3DEvent( final float partialTicks) {
        this.partialTicks = partialTicks;
    }
}

