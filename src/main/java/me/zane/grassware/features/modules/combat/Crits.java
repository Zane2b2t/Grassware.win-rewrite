package me.zane.grassware.features.modules.combat;
//WARNING: ALL CONTENT BELONGS TO https://github.com/Zane2b2t , IF ANY OF THE CLASSES CONTAINING THIS WARNING ARENT IN https://github.com/Zane2b2t/Grassware.win-Rewrite INFORM GITHUB TO DMCA
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.features.setting.impl.ModeSetting;
import me.zane.grassware.event.events.PacketEvent;
import me.zane.grassware.features.modules.Module;

import me.zane.grassware.util.Timer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;

import com.mojang.realmsclient.gui.ChatFormatting;

import java.util.Arrays;
//this no work bruv
public class Crits extends Module {

    private final ModeSetting mode = register("Mode", "Packet", Arrays.asList("Jump", "MiniJump", "Packet", "NCP"));

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (!nullSafe()) return;
        if (event.getPacket() instanceof CPacketUseEntity) {
            CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
            if (packet.getAction() == CPacketUseEntity.Action.ATTACK) {
                if (mc.player.onGround  && packet.getEntityFromWorld(mc.world) instanceof EntityLivingBase) {
                    switch (mode.getValue()) {
                        case "Jump":
                            mc.player.jump(); //this is useless lol
                            break;
                        case "Packet":
                            mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0625101, mc.player.posZ, false));
                            mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                            mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                            mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.1E-5, mc.player.posZ, false));
                            mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                            break;
                        case "NCP":
                            mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.062600301692775, mc.player.posZ, false));
                            mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.07260029960661, mc.player.posZ, false));
                            mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                            mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.07260029960661, mc.player.posZ, false));
                            break;
                        case "MiniJump":
                            mc.player.jump();
                            mc.player.motionY /= 2.0;
                            break;
                    }

                }

            }
        }
    }


    public String getInfo()  {
        return " [" + ChatFormatting.WHITE + mode.getValue() + ChatFormatting.RESET + "]";
    }

}