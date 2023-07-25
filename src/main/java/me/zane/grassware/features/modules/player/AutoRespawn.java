package me.zane.grassware.features.modules.player;
//WARNING: ALL CONTENT BELONGS TO https://github.com/Zane2b2t , IF ANY OF THE CLASSES CONTAINING THIS WARNING ARENT IN https://github.com/Zane2b2t/Grassware.win-Rewrite INFORM GITHUB TO DMCA
import me.zane.grassware.event.events.UpdatePlayerWalkingEvent;
import me.zane.grassware.features.command.Command;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.util.Timer;

import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.network.play.client.CPacketClientStatus;

import com.mojang.realmsclient.gui.ChatFormatting;

public class AutoRespawn extends Module {
    private final BooleanSetting chat = register("Chat", false);
    private final Timer stopWatch = new Timer();

    @EventListener
    public void onUpdate(final UpdatePlayerWalkingEvent event) {
        if (mc.currentScreen instanceof GuiGameOver && stopWatch.passed(4999)) {
            mc.getConnection().sendPacket(new CPacketClientStatus(CPacketClientStatus.State.PERFORM_RESPAWN));
            if (chat.getValue()) {
                Command.sendMessage(ChatFormatting.RED + "Respawning.");
            }
            stopWatch.reset();
        }
    }

}
