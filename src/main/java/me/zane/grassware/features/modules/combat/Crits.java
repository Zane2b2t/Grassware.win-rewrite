package me.zane.grassware.features.modules.combat;
//WARNING: ALL CONTENT BELONGS TO https://github.com/Zane2b2t , IF ANY OF THE CLASSES CONTAINING THIS WARNING ARENT IN https://github.com/Zane2b2t/Grassware.win-Rewrite INFORM GITHUB TO DMCA
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.features.setting.impl.ModeSetting;
import me.zane.grassware.event.events.PacketEvent;
import me.zane.grassware.features.modules.Module;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;

import com.mojang.realmsclient.gui.ChatFormatting;

import java.util.Arrays;

public class Crits extends Module {

    private final ModeSetting mode = register("Mode", "PACKET", Arrays.asList("JUMP", "PACKET", "NCP"));

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (!nullSafe()) return;
        if (event.getPacket() instanceof CPacketUseEntity) {
            CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
            if (packet.getAction() == CPacketUseEntity.Action.ATTACK) {
                if (mc.player.onGround && !mc.gameSettings.keyBindJump.isKeyDown() && packet.getEntityFromWorld(mc.world) instanceof EntityLivingBase) {
                    if (mode.getValue().equals("JUMP")) {
                        mc.player.jump();
                    } else if (mode.getValue().equals("NCP")) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.11, mc.player.posZ, false));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1100013579, mc.player.posZ, false));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1100013579, mc.player.posZ, false));
                    }
                    else {
                        mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1F, mc.player.posZ, false));
                    }
                }

                }
            }
        }

    public String getInfo()  {
        return "[" + ChatFormatting.WHITE + mode.getValue() + ChatFormatting.GRAY + "]";
    }

}