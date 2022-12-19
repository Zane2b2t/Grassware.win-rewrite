package me.zane.grassware.features.modules.movement;

import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.PacketEvent;
import me.zane.grassware.event.events.ItemInputUpdateEvent;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoSlow extends Module {
       private final BooleanSetting strict = register("Strict", false);
       private final BooleanSetting toobee = register("2b2t", false);

    @EventListener
    public void onItemInputUpdate(final ItemInputUpdateEvent event) {
        if (slowed()) {
            final MovementInput movementInput = event.movementInput;
            movementInput.moveForward /= 0.2f;
            movementInput.moveStrafe /= 0.2f;
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (slowed()) {
        if (event.getPacket() instanceof CPacketPlayer && toobee.getValue()) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
        }
        if (slowed()) {
        if (event.getPacket() instanceof CPacketPlayer && strict.getValue()) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ)), EnumFacing.DOWN));
        }
    }

    private boolean slowed() {
        return mc.player.isHandActive() && !mc.player.isRiding() && !mc.player.isElytraFlying();
    }
}
