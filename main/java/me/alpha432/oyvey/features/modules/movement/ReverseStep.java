package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.event.bus.EventListener;
import me.alpha432.oyvey.event.events.TickEvent;
import me.alpha432.oyvey.features.modules.Module;

public class ReverseStep extends Module {

    @EventListener
    public void onTick(final TickEvent event){
        if (mc.player.onGround){
            mc.player.motionY = -2.0f;
        }
    }
}
