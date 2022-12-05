package me.zane.grassware.features.modules.movement;

import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.TickEvent;
import me.zane.grassware.features.modules.Module;

public class Step extends Module {

    @Override
    public void onDisable(){
        mc.player.stepHeight = 0.6f;
    }

    @EventListener
    public void onTick(final TickEvent event) {
        mc.player.stepHeight = 2.0f;
    }
}
