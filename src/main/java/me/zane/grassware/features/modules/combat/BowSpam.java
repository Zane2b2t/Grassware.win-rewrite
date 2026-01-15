package me.zane.grassware.features.modules.combat;

import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.UpdatePlayerWalkingEvent;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.IntSetting;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.math.BlockPos;

public class BowSpam extends Module {
    private final IntSetting ticks = register("Ticks", 3, 1, 20);
    private int ticksHeld = 0;

    @EventListener
    public void onUpdate(final UpdatePlayerWalkingEvent event) {
        if (mc.player == null || mc.world == null) return;

        if (mc.player.getHeldItemMainhand().getItem() instanceof ItemBow) {
            if (mc.player.isHandActive()) {
                ticksHeld++;

                if (ticksHeld >= ticks.getValue()) {
                    releaseBow();
                    ticksHeld = 0;
                }
            } else {
                ticksHeld = 0;
            }
        } else {
            ticksHeld = 0;
        }
    }

    private void releaseBow() {
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM,
                BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
        mc.player.stopActiveHand();
    }
}
