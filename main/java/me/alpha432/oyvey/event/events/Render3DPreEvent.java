package me.alpha432.oyvey.event.events;

import me.alpha432.oyvey.event.bus.Event;

public class Render3DPreEvent extends Event {
    public final float partialTicks;

    public Render3DPreEvent( final float partialTicks) {
        this.partialTicks = partialTicks;
    }
}
