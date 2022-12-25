package me.zane.grassware.features.modules.player;

import me.zane.grassware.features.modules.Module;
import me.zane.grassware.event.events.TickEvent;
import me.zane.grassware.event.bus.EventListener;

import net.minecraft.network.play.client.CPacketPlayer;
import org.lwjgl.input.Keyboard;

public class CornerClip extends Module {
    public CornerClip() {
// made by WMS gaming -ZANE
        }
    @EventListener
    public void onTick(final TickEvent event) {
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX,mc.player.posY - 0.0042123,mc.player.posZ,mc.player.onGround));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX,mc.player.posY - 0.02141,mc.player.posZ,mc.player.onGround));
        mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX,mc.player.posY - 0.097421,mc.player.posZ,500,500,mc.player.onGround));
        toggle();
    }
}
