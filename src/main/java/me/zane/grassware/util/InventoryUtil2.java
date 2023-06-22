// other one is so bloaty istg
package me.zane.grassware.util;

import me.zane.grassware.util.MC;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;

public class InventoryUtil2 implements MC {

    public static void switchToSlot(int slot) {
        if (slot == -1) {
            return;
        }
        PacketUtil.invoke(new CPacketHeldItemChange(slot));
        mc.player.inventory.currentItem = slot;
        mc.playerController.updateController();
    }

    public static void switchBack(int slot) {
        if (slot == -1) {
            return;
        }
        mc.player.inventory.currentItem = slot;
        mc.playerController.updateController();
    }

    public static int getItemSlot(Item item, boolean hotbar) {
        int itemSlot = -1;
        for (int i = 1; i <= (hotbar ? 45 : 36); ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem().equals(item)) {
                itemSlot = i;
                break;
            }
        }
        return itemSlot;
    }

    public static int getItemSlot(Item item) {
        int itemSlot = -1;
        for (int i = 1; i <= 45; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem().equals(item)) {
                itemSlot = i;
                break;
            }
        }
        return itemSlot;
    }

    public static int getBlockSlot(Block block) {
        int itemSlot = -1;
        for (int i = 1; i <= 45; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem().equals(Item.getItemFromBlock(block))) {
                itemSlot = i;
                break;
            }
        }
        return itemSlot;
    }

    public static int getBlockSlotByName(String name) {
        int itemSlot = -1;
        for (int i = 1; i <= 45; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getDisplayName().toLowerCase().contains(name)) {
                itemSlot = i;
                break;
            }
        }
        return itemSlot;
    }


    public static int getBlockFromHotbar(Block block) {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getDisplayName().toLowerCase().contains("anchor")){
                continue;
            }
            if (stack.getItem().equals(Item.getItemFromBlock(block)))
                slot = i;
        }
        return slot;
    }

    public static int getItemFromHotbar(Item item) {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem().equals(item))
                slot = i;
        }
        return slot;
    }
}
