package me.zane.grassware.features.modules.combat;

import me.zane.grassware.features.modules.Module;
import me.zane.grassware.event.events.UpdateEvent;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.network.play.client.CPacketChatMessage;



public class CCSS extends Module {
  
    public boolean silentMode = false;

// you can silent switch on cc but your ca will be slower, with that you can stil offhand but also silent switch only when there's totem in ur offhand (youre low health you cant offhand rn)
  // so this gives u some extra slow crystals which is better than nothing!
  
    @SubscribeEvent
    public void onUpdate(final UpdateEvent event) {
        if (mc.player.getHeldItemOffhand().getItem() == TOTEM_OF_UNDYING) {    // makes autocrystal switch to silent when there's totem in offhand
            Util.mc.player.connection.sendPacket(new CPacketChatMessage(".AutoCrystal switch Silent "));
          Command.sendMessage(ChatFormatting.BOLD + "Silent switch is on!");
            silentMode = true; 
        }

        if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {  // makes autocrystal switch to none when there's crystal in offhand
            Util.mc.player.connection.sendPacket(new CPacketChatMessage("AutoCrystal switch None"));
          Command.sendMessage(ChatFormatting.BOLD + "Silent switch is off.");
            silentMode = false;
          return;
        }


}
  
