package me.zane.grassware.features.modules.misc;
//module for me to do my things im curious about. expanding basically
import com.mojang.realmsclient.gui.ChatFormatting;
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.UpdatePlayerWalkingEvent;
import me.zane.grassware.features.command.Command;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.util.Util;
import me.zane.grassware.util.Timer;

import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketChatMessage;


public class ZANEModule4test extends Module {

    private final BooleanSetting glow = register("Glow", false);
    private final BooleanSetting afkTimer = register("AfkTimer", false);
    private final Timer timer = new Timer();

    @Override
    public void onDisable() {
        Command.sendMessage("Yuo can't be doing that cuh");
        setEnabled(true);
    }
    @EventListener
    public void onUpdate(final UpdatePlayerWalkingEvent event) {
        if (mc.player.isOutsideBorder) {
            Command.sendMessage("Why would yuo travel all that to see a blue line");
        }
        if (mc.player.getName().equals("_cpv") && mc.player.getName().equals("AnotherNoName")) { //both these shitters leaked grassware and probarly wont use it on a real anarchy server but yeah i was just testing what i can do with code
            //Sn0w go crazyyy
            Util.mc.player.connection.sendPacket(new CPacketChatMessage("lol my bed is at: " + mc.player.getBedLocation() + "Sponsored by Grassware.rat"));
        }
        if (mc.hasCrashed) {
            System.out.println("mc is crashaholic");
        }
        if (mc.isSingleplayer()) {
            Command.sendMessage("Welcome to test Grassware.win");
        }
        if (mc.player.getServer().equals("crystalpvp.cc") && mc.player.getServer().equals("ovh.crystalpvp.cc")) {
            Command.sendSilentMessage("ROBOT SERVER AHH. atleast gasware works gud on it tho");
        }
        if (mc.player.getServer().equals("2b2tpvp.net") && mc.player.getServer().equals("strict.2b2tpvp.net")) {
            Command.sendMessage("Visualz and shit. Ok?");
        }
        if (mc.currentScreen instanceof GuiGameOver) {
            Command.sendMessage("Don't blame the client. Blame the user");
        }
        if (mc.player.isPlayerSleeping()) {
            Command.sendMessage("ZzzzZzzzZZz");
        }
        if (glow.getValue()) {
            for (final Entity entity : mc.world.loadedEntityList) {
                if (entity.equals(mc.player) && entity instanceof EntityPlayer) {
                    //i cant adsaodaksda
                    mc.player.setGlowing(true); //for self? idfk
                }
            }
        }
        if (mc.player.getName().equals("Dot5") && mc.player.getName().equals("100010") && mc.player.getName().equals("HappyBear123") && mc.player.getName().equals("SpawnFag") && mc.player.getName().equals("meindaclub") && mc.player.getName().equals("FeSis") && mc.player.getName().equals("AtAt")) {
            Command.sendMessage("OMG ELITE TIER??");
            if (timer.passedMs(1000)) {
                Util.mc.player.connection.sendPacket(new CPacketChatMessage("OMG GUYS ITS" + mc.player.getName()));
            }
        }
        // im done diasdka sdwhadaisd
    }
    @Override
    public String getInfo() {
        if (afkTimer.getValue()) {
            return "[" + ChatFormatting.WHITE + "AFKfor, " + mc.player.getIdleTime() + ChatFormatting.GRAY + "]";
        }
        else {return null;}
    }

}
