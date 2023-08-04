package me.zane.grassware.features.modules.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.PacketEvent;
import me.zane.grassware.event.events.PushBlockEvent;
import me.zane.grassware.features.modules.Module;

import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;

public class Velocity extends Module {

    @EventListener
    public void onPacketReceive(final PacketEvent.Receive event) {
        if ((event.getPacket() instanceof SPacketEntityVelocity && ((SPacketEntityVelocity) event.getPacket()).getEntityID() == mc.player.entityId) || event.getPacket() instanceof SPacketExplosion) {
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onPushBlock(final PushBlockEvent event) {
        event.setCancelled(true);
    }

    @Override
    public String getInfo() {
        return " [" + ChatFormatting.WHITE + "%0, %0" +ChatFormatting.RESET + ChatFormatting.GRAY + "]";
    }
}