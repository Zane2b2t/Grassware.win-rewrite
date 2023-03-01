package me.zane.grassware.features.module.modules.player;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemStack;

import me.zane.grassware.features.modules.Module;
import me.zane.grassware.mixin.mixins.IMinecraft;

public class XP extends Module {
  

    @Override
    public void init() {
        this.setModuleStack(new ItemStack(Items.EXPERIENCE_BOTTLE));
    }

    @Override
    public void onFastTick() {
        if (mc.player.getHeldItemMainhand().getItem() instanceof ItemExpBottle
        || mc.player.getHeldItemOffhand().getItem() instanceof ItemExpBottle) {
            if(((IMinecraft) mc).getRightClickDelayTimer() != 0) {
                ((IMinecraft) mc).setRightClickDelayTimer(0);
            }
        }
    }
}
