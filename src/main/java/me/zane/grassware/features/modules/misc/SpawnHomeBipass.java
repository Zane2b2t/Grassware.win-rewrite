package me.zane.grassware.features.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.zane.grassware.features.command.Command;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.StringSetting;
import me.zane.grassware.util.Util;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SpawnHomeBipass extends Module {

    private final StringSetting HomeName = register("Home Name", "Home1");

    @SubscribeEvent
    public void onEnable() {
        Command.sendMessage(ChatFormatting.WHITE + name + " Attempting to teleport to " + ChatFormatting.RED + this.HomeName.getValue() + "(don't tell leee!!!!)");
        Util.mc.player.connection.sendPacket(new CPacketChatMessage("/HOmE " + this.HomeName.getValue()));
    }
}
