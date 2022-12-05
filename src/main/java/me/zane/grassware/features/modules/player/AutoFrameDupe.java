package me.zane.grassware.features.modules.player;

import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.IntSetting;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.util.EnumHand;

public class AutoFrameDupe extends Module {
    private final BooleanSetting shulkersonly = register("ShulkerOnly", false);
    private final IntSetting range = register("Range", 5, 1, 10);
    private final IntSetting turns = register("Turns", 5, 1, 10);
    private final IntSetting ticks = register("Ticks", 10, 1, 20);
    private int timeoutTicks = 0;


    // @Override
    public void onUpdate() {
        if (Util.mc.player != null && Util.mc.world != null) {
            if (shulkersonly.getValue()) {
                int shulkerSlot = getShulkerSlot();
                if (shulkerSlot != -1) {
                    Util.mc.player.inventory.currentItem = shulkerSlot;
                }
            }
            for (Entity frame : Util.mc.world.loadedEntityList) {
                if (frame instanceof EntityItemFrame) {
                    if (Util.mc.player.getDistance(frame) <= range.getValue()) {
                        if (timeoutTicks >= ticks.getValue()) {
                            if (((EntityItemFrame) frame).getDisplayedItem().getItem() == Items.AIR && !Util.mc.player.getHeldItemMainhand().isEmpty) {
                                Util.mc.playerController.interactWithEntity(Util.mc.player, frame, EnumHand.MAIN_HAND);
                            }
                            if (((EntityItemFrame) frame).getDisplayedItem().getItem() != Items.AIR) {
                                for (int i = 0; i < turns.getValue(); i++) {
                                    Util.mc.playerController.interactWithEntity(Util.mc.player, frame, EnumHand.MAIN_HAND);
                                }
                                Util.mc.playerController.attackEntity(Util.mc.player, frame);
                                timeoutTicks = 0;
                            }
                        }
                        ++timeoutTicks;
                    }
                }
            }
        }
    }

    private int getShulkerSlot() {
        int shulkerSlot = -1;
        for (int i = 0; i < 9; i++) {
            Item item = Util.mc.player.inventory.getStackInSlot(i).getItem();
            if (item instanceof ItemShulkerBox) shulkerSlot = i;
        }
        return shulkerSlot;
    }
}
