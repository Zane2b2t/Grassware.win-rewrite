package me.zane.grassware.features.modules.movement;

import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.TickEvent;
import me.zane.grassware.features.modules.Module;

public class CounterStrafe extends Module {

    @EventListener
    public void onTick(final TickEvent event) {
        if (mc.player.movementInput.moveForward == 0 && mc.player.movementInput.moveStrafe == 0) {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }
    }
}
