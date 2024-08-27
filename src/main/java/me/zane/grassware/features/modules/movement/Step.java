package me.zane.grassware.features.modules.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.TickEvent;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.FloatSetting;

public class Step extends Module {

    private final FloatSetting height = register("Heigt", 1.0f, 0.6f, 2.0F );

    @Override
    public void onDisable() {
        mc.player.stepHeight = 0.6f;
    }

    @EventListener
    public void onTick(final TickEvent event) {
        if (!mc.player.movementInput.jump && mc.player.collidedVertically && (double) mc.player.fallDistance < 0.1) {
            mc.player.stepHeight = height.getValue();
        }
    }
    @Override
    public String getInfo() {
        return " [" + ChatFormatting.WHITE + "Vanilla, " + height.getValue() + ChatFormatting.RESET  + "]";
    }
}