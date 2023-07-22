package me.zane.grassware.event.events;

import me.zane.grassware.event.bus.Event;

public class Render3DPostEvent extends Event {
    private final float partialTicks;

    public Render3DPostEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
