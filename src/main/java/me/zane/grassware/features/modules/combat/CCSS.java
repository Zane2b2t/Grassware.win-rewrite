package me.zane.grassware.features.modules.combat;

import me.zane.grassware.features.modules.Module;

import net.minecraft.init.Items;
import net.minecraft.item.ItemPickaxe;

import static net.minecraft.init.Items.DIAMOND_PICKAXE;

public class CCSS extends Module {
  
    public boolean silentMode = false;

// you can silent switch on cc but your ca will be slower, with that you can stil offhand but also silent switch only when there's totem in ur offhand (youre low health you cant offhand rn)
  // so this gives u some extra slow crystals which is better than nothing!
  
    @Override
    public void onUpdate() {
        if (mc.player.getHeldItemOffhand().getItem() == TOTEM_OF_UNDYING) {    // makes autocrystal switch to silent when there's totem in offhand
            mc.player.sendChatMessage(".AutoCrystal switch Silent");
            silentMode = true;
        }

        if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {  // makes autocrystal switch to none when there's crystal in offhand
            mc.player.sendChatMessage(".AutoCrystal switch None");
            silentMode = false;
        }


}
  
