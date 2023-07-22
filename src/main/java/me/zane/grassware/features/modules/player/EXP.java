package me.zane.grassware.features.modules.player;

import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.UpdatePlayerWalkingEvent;
import me.zane.grassware.features.modules.Module;

import net.minecraft.init.Items;

public class EXP extends Module {

    @EventListener
    public void onUpdate(final UpdatePlayerWalkingEvent event) {
        if (mc.world == null || mc.player == null)
            return;

        if (mc.player.inventory.getStackInSlot(mc.player.inventory.currentItem).getItem() == Items.EXPERIENCE_BOTTLE) {
            mc.rightClickDelayTimer = 0;
        }

    }
}