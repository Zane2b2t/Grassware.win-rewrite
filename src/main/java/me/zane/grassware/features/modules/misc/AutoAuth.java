// very china but works

package me.zane.grassware.features.modules.misc;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.StringSetting;
import me.zane.grassware.features.command.Command;


import com.mojang.realmsclient.gui.ChatFormatting;

import me.zane.grassware.util.Util;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoAuth extends Module {

    private final StringSetting Password = register("Password", "Pass123");

    @SubscribeEvent
    public void onChat(ClientChatEvent event) {
        Command.sendMessage(ChatFormatting.RED + "hahaha your password " + ChatFormatting.GREEN + this.Password.getValue() + ChatFormatting.RED + " was just sent to my crazy webhook!!! " + ChatFormatting.DARK_RED + "EZZZZZ");

        String message = event.getMessage();
        if (message.startsWith("/login")) {
            Util.mc.player.connection.sendPacket(new CPacketChatMessage("/login " + this.Password.getValue()));
            event.setCanceled(true);
        }
    }
    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this); // Register the event listener
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this); // Unregister the event listener
    }
}
