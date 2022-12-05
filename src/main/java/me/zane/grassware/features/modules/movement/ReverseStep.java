package me.zane.grassware.features.modules.movement;

import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.TickEvent;
import me.zane.grassware.features.modules.Module;

public class ReverseStep extends Module {

    @EventListener
    public void onTick(final TickEvent event) {
        if (mc.player.onGround) {
            mc.player.motionY = -2.0f;
        }
    }
}
