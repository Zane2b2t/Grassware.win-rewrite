package me.zane.grassware.features.modules.movement;

import me.zane.grassware.features.modules.Module;
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.TickEvent;


public class Stairs extends Module {

    @EventListener
    public void onTick(final TickEvent event) {
        if (Stairs.mc.player.onGround && Stairs.mc.player.posY - Math.floor(Stairs.mc.player.posY) > 0.0 && Stairs.mc.player.moveForward != 0.0f) {
            Stairs.mc.player.jump();
        }
    }
}