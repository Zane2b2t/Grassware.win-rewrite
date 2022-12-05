package me.zane.grassware.event.events;

import me.zane.grassware.event.bus.Event;
import net.minecraft.client.gui.ScaledResolution;

public class RenderHotbarEvent extends Event {
    public final ScaledResolution scaledResolution;

    public RenderHotbarEvent(final ScaledResolution scaledResolution) {
        this.scaledResolution = scaledResolution;
    }
}
