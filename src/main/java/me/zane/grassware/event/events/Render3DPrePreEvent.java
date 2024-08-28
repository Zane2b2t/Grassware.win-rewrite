package me.zane.grassware.event.events;

import me.zane.grassware.event.bus.Event;

public class Render3DPrePreEvent extends Event {
    public final float partialTicks;

    public Render3DPrePreEvent(final float partialTicks) {
        this.partialTicks = partialTicks;
    }

}
