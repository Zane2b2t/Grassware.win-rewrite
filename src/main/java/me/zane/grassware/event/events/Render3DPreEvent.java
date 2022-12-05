package me.zane.grassware.event.events;

import me.zane.grassware.event.bus.Event;

public class Render3DPreEvent extends Event {
    public final float partialTicks;

    public Render3DPreEvent( final float partialTicks) {
        this.partialTicks = partialTicks;
    }
}
