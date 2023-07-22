package me.zane.grassware.features.modules.combat;
//WARNING: ALL CONTENT BELONGS TO https://github.com/Zane2b2t , IF ANY OF THE CLASSES CONTAINING THIS WARNING ARENT IN https://github.com/Zane2b2t/Grassware.win-Rewrite INFORM GITHUB TO DMCA
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.event.events.TickEvent;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class AA extends Module {


    @EventListener
    public void onTick(final TickEvent event) {
        if (mc.player == null || mc.world == null || mc.player.ticksExisted % 2 == 0 || mc.currentScreen instanceof GuiContainer && !(mc.currentScreen instanceof InventoryEffectRenderer)) {
            return;
        }

        int[] bestArmorSlots = new int[4];
        int[] bestArmorValues = new int[4];

        for (int armorType = 0; armorType < 4; armorType++) {
            ItemStack old = mc.player.inventory.armorItemInSlot(armorType);
            if (old.getItem() instanceof ItemArmor) {
                bestArmorValues[armorType] = ((ItemArmor) old.getItem()).damageReduceAmount;
            }
            bestArmorSlots[armorType] = -1;
        }

        for (int slot = 0; slot < 36; slot++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(slot);
            if (stack.getCount() > 1 || !(stack.getItem() instanceof ItemArmor)) continue;
            ItemArmor armor = (ItemArmor) stack.getItem();
            int armorType = armor.armorType.ordinal() - 2;
            if (armorType == 2 && mc.player.inventory.armorItemInSlot(armorType).getItem().equals(Items.ELYTRA)) {
                continue;
            }
            int armorValue = armor.damageReduceAmount;
            if (armorValue > bestArmorValues[armorType]) {
                bestArmorSlots[armorType] = slot;
                bestArmorValues[armorType] = armorValue;
            }
        }

        for (int armorType = 0; armorType < 4; armorType++) {
            int slot = bestArmorSlots[armorType];
            if (slot == -1) continue;

            ItemStack oldArmor = mc.player.inventory.armorItemInSlot(armorType);
            if (oldArmor != ItemStack.EMPTY || mc.player.inventory.getFirstEmptyStack() != -1) {
                if (slot < 9) slot += 36;
                mc.playerController.windowClick(0, 8 - armorType, 0, ClickType.QUICK_MOVE, mc.player);
                mc.playerController.windowClick(0, slot, 0, ClickType.QUICK_MOVE, mc.player);
                break;
            }
        }
    }
}
