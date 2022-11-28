package me.alpha432.oyvey.event.events;

import me.alpha432.oyvey.event.bus.Event;
import net.minecraft.client.gui.ScaledResolution;

public class RenderHotbarEvent extends Event {
    public final ScaledResolution scaledResolution;

    public RenderHotbarEvent(final ScaledResolution scaledResolution){
        this.scaledResolution = scaledResolution;
    }
}
