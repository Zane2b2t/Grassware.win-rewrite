package me.zane.grassware.features.modules.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.TickEvent;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.ModeSetting;

import java.util.Arrays;

public class Sprint extends Module {

    private final ModeSetting mode = register("Mode", "Rage", Arrays.asList("Legit", "Rage"));

    @EventListener
    public void onTick(final TickEvent event) {
        switch (mode.getValue()) {
            case "Rage":
                mc.player.setSprinting(mc.player.movementInput.moveForward != 0.0f || mc.player.movementInput.moveStrafe != 0.0f);
                break;
                case "Legit":
                    mc.player.setSprinting(mc.player.movementInput.moveForward != 0.0f);
        }
    }
    @Override
    public String getInfo() {
        return " [" + ChatFormatting.WHITE + mode.getValue() + ChatFormatting.RESET + "]";
    }
}