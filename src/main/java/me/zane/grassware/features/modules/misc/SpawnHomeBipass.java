package me.zane.grassware.features.modules.misc;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.zane.grassware.features.command.Command;

import me.zane.grassware.features.command.Command;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.StringSetting;
import net.minecraft.network.play.client.CPacketChatMessage;
import me.zane.grassware.util.Util;



public class SpawnHomeBipass extends Module {

    private final StringSetting HomeName = register("Home Name", "Home1");

    @SubscribeEvent
    public void onEnable() {
        Command.sendRemovableMessage(ChatFormatting.WHITE + name + " Attempting to teleport to " + ChatFormatting.RED + this.HomeName.getValue() + "(don't tell leee!!!!)", 1);
        Util.mc.player.connection.sendPacket(new CPacketChatMessage("/HOmE" + this.HomeName.getValue()));
    }
}
