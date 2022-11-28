package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.event.bus.EventListener;
import me.alpha432.oyvey.event.events.TickEvent;
import me.alpha432.oyvey.features.modules.Module;

public class Sprint extends Module {

    @EventListener
    public void onTick(final TickEvent event){
        if (mc.player.movementInput.moveForward != 0.0f || mc.player.movementInput.moveStrafe != 0.0f){
            mc.player.setSprinting(true);
        }
    }
}
