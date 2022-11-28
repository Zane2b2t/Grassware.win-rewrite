package me.alpha432.oyvey.event.events;

import me.alpha432.oyvey.event.bus.Event;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;

public class OverlayEvent extends Event {
    public final RenderBlockOverlayEvent.OverlayType overlayType;

    public OverlayEvent(final RenderBlockOverlayEvent.OverlayType overlayType){
        this.overlayType = overlayType;
    }
}
