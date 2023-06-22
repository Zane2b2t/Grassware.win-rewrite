// very china but works -Rat
// I don't think so -ZANE
// ok it sometimes works? idk how to explain
package me.zane.grassware.features.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.zane.grassware.features.command.Command;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.StringSetting;
import me.zane.grassware.util.Util;
import me.zane.grassware.event.bus.EventListener;

import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.client.event.ClientChatEvent;

public class AutoAuth extends Module {

    private final StringSetting Password = register("Password", "Pass123");

    @EventListener
    public void onChat(ClientChatEvent event) {
        Command.sendMessage(ChatFormatting.RED + "hahaha your password " + ChatFormatting.GREEN + this.Password.getValue() + ChatFormatting.RED + " was just sent to my crazy webhook!1! " + ChatFormatting.DARK_RED + "eZZZZZ");

        String message = event.getMessage();
        if (message.startsWith("/login")) {
            Util.mc.player.connection.sendPacket(new CPacketChatMessage("/login " + this.Password.getValue()));
            event.setCanceled(true);
        }
    }

}
