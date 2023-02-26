package me.zane.grassware.features.modules.misc;

import com.google.common.eventbus.Subscribe;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.command.Command;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.zane.grassware.features.setting.impl.StringSetting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class CustomJD extends Module {

    private final StringSetting CustomText = register("Custom Text", "Tuesday, 11 September 2001 - 8:14:00");

    @Override
    public void onEnable() {
        Command.sendSilentMessage(ChatFormatting.GOLD + "[" + ChatFormatting.DARK_BLUE + "8b" + ChatFormatting.BLUE + "8t" + ChatFormatting.GOLD + "]" + " You joined the server on " + ChatFormatting.RED + this.CustomText.getValue());
        disable();
    }
}
