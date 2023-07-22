package me.zane.grassware.features.modules.misc;

import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.PlaceBlockEvent;
import me.zane.grassware.features.modules.Module;

public class PlaceConfirm extends Module {

    @EventListener
    public void onPlaceBlock(PlaceBlockEvent event) {
        event.setCancelled(true);
    }
}
