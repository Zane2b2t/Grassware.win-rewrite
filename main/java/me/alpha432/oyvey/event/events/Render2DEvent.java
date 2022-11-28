package me.alpha432.oyvey.event.events;

import me.alpha432.oyvey.event.bus.Event;
import net.minecraft.client.gui.ScaledResolution;

public class Render2DEvent extends Event {
    public final ScaledResolution scaledResolution;
    public final float partialTicks;

    public Render2DEvent(final float partialTicks, final ScaledResolution scaledResolution) {
        this.partialTicks = partialTicks;
        this.scaledResolution = scaledResolution;
    }
}

