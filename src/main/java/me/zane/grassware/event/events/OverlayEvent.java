package me.zane.grassware.event.events;

import me.zane.grassware.event.bus.Event;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;

public class OverlayEvent extends Event {
    public final RenderBlockOverlayEvent.OverlayType overlayType;

    public OverlayEvent(final RenderBlockOverlayEvent.OverlayType overlayType){
        this.overlayType = overlayType;
    }
}
