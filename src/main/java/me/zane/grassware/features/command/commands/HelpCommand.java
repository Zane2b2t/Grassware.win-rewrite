package me.zane.grassware.features.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.zane.grassware.GrassWare;
import me.zane.grassware.features.command.Command;

public class HelpCommand
        extends Command {
    public HelpCommand() {
        super("help");
    }

    @Override
    public void execute(final String[] commands) {
        HelpCommand.sendMessage("Commands: ");
        for (Command command : GrassWare.commandManager.getCommands()) {
            HelpCommand.sendMessage(ChatFormatting.GRAY + GrassWare.commandManager.getPrefix() + command.getName());
        }
    }
}
