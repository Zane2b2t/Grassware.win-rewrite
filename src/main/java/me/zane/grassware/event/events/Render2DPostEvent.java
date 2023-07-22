package me.zane.grassware.event.events;

import me.zane.grassware.event.bus.Event;
import net.minecraft.client.gui.ScaledResolution;

public class Render2DPostEvent extends Event {
    private final ScaledResolution scaledResolution;
    private final float partialTicks;

    public Render2DPostEvent(float partialTicks, ScaledResolution scaledResolution) {
        this.partialTicks = partialTicks;
        this.scaledResolution = scaledResolution;
    }


    public ScaledResolution getScaledResolution() {
        return scaledResolution;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
