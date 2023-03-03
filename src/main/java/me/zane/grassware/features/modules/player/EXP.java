package org.hockey.hockeyware.client.features.module.modules.Player;

import net.minecraft.init.Items;

import me.zane.grassware.features.modules.Module;
import me.zane.grassware.event.events.UpdatePlayerWalkingEvent;
import me.zane.grassware.event.bus.EventListener;

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
