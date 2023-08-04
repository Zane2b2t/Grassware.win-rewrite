package me.zane.grassware.features.modules.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.TickEvent;
import me.zane.grassware.features.modules.Module;

public class Sprint extends Module {

    @EventListener
    public void onTick(final TickEvent event) {
        if (mc.player.movementInput.moveForward != 0.0f || mc.player.movementInput.moveStrafe != 0.0f) {
            mc.player.setSprinting(true);
        }
    }
    @Override
    public String getInfo() {
        return " [" + ChatFormatting.WHITE + "Legit" + ChatFormatting.RESET + "]";
    }
}