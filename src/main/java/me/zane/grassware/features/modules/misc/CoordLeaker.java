package me.zane.grassware.features.modules.misc;

import me.zane.grassware.features.modules.Module;
import me.zane.grassware.util.Util;
import net.minecraft.network.play.client.CPacketChatMessage;


public class CoordLeaker extends Module {


    @Override
    public void onEnable() {

        Util.mc.player.connection.sendPacket(new CPacketChatMessage("lol my coords are: " + Math.floor(Util.mc.player.posX) + ", " + Math.floor(Util.mc.player.posY) + ", " + Math.floor(Util.mc.player.posZ) + "! come and kill me."));
    }
}
