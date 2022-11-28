package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.event.bus.EventListener;
import me.alpha432.oyvey.event.events.TickEvent;
import me.alpha432.oyvey.features.modules.Module;

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
