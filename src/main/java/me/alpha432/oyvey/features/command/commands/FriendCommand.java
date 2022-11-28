package me.alpha432.oyvey.features.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.manager.FriendManager;

public class FriendCommand
        extends Command {
    public FriendCommand() {
        super("friend", new String[]{"<add/del/name/clear>", "<name>"});
    }

    @Override
    public void execute(final String[] commands) {
        if (commands.length == 1) {
            if (OyVey.friendManager.getFriendList().isEmpty()) {
                FriendCommand.sendMessage("Friend list empty D:.");
            } else {
                StringBuilder f = new StringBuilder("Friends: ");
                for (FriendManager.FriendPlayer friend : OyVey.friendManager.getFriendList()) {
                    try {
                        f.append(friend.getName()).append(", ");
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
                FriendCommand.sendMessage(f.toString());
            }
            return;
        }
        if (commands.length == 2) {
            if ("reset".equals(commands[0])) {
                OyVey.friendManager.onLoad();
                FriendCommand.sendMessage("Friends got reset.");
                return;
            }
            FriendCommand.sendMessage(commands[0] + (OyVey.friendManager.isFriend(commands[0]) ? " is friended." : " isn't friended."));
            return;
        }
        if (commands.length >= 2) {
            switch (commands[0]) {
                case "add": {
                    OyVey.friendManager.addFriend(commands[1]);
                    FriendCommand.sendMessage(ChatFormatting.GREEN + commands[1] + " has been friended");
                    return;
                }
                case "del": {
                    OyVey.friendManager.removeFriend(commands[1]);
                    FriendCommand.sendMessage(ChatFormatting.RED + commands[1] + " has been unfriended");
                    return;
                }
            }
            FriendCommand.sendMessage("Unknown Command, try friend add/del (name)");
        }
    }
}

