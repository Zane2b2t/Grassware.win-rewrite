package me.zane.grassware.features.modules.combat;
//WARNING: ALL CONTENT BELONGS TO https://github.com/Zane2b2t , IF ANY OF THE CLASSES CONTAINING THIS WARNING ARENT IN https://github.com/Zane2b2t/Grassware.win-Rewrite INFORM GITHUB TO DMCA
import com.mojang.realmsclient.gui.ChatFormatting;
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.TickEvent;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.FloatSetting;

import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayer;

import java.util.Arrays;

public class Offhand extends Module {
    private final FloatSetting health = register("Health", 14.0f, 0.0f, 36.0f);

    @EventListener
    public void onTick(final TickEvent event) {
        final int slot = slot();
        if (slot != -1) {
            swapItem(slot);
        }
    }

    private int slot() {
        if (mc.currentScreen != null) {
            return -1;
        }
        final int totem = inventorySlot(Items.TOTEM_OF_UNDYING);
        if (mc.player.getHealth() + mc.player.getAbsorptionAmount() <= health.getValue()) {
            return totem;
        }
        if (mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_SWORD)) {
            if (mc.gameSettings.keyBindUseItem.isKeyDown()) {
                return inventorySlot(Items.GOLDEN_APPLE);
            }

        }
        final int crystal = inventorySlot(Items.END_CRYSTAL);
        if (crystal != -1) {
            return inventorySlot(Items.END_CRYSTAL);
        }
        return totem;
    }

    private void swapItem(final int i) {
        final Item item = mc.player.inventory.getStackInSlot(i).getItem();
        if (!mc.player.getHeldItemOffhand().getItem().equals(item)) {
            int slot = i < 9 ? i + 36 : i;
            swap(new int[]{slot, 45, slot});
            mc.playerController.updateController();
        }
    }

    private void swap(final int[] slots) {
        if (mc.getConnection() != null) {
            Arrays.stream(slots).forEach(i -> mc.playerController.windowClick(0, i, 0, ClickType.PICKUP, mc.player));
            mc.getConnection().sendPacket(new CPacketPlayer());
        }
    }

    public int inventorySlot(final Item item) {
        int itemSlot = -1;
        for (int i = 45; i > 0; --i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem().equals(item)) {
                itemSlot = i;
                break;
            }
        }
        return itemSlot;
    }
    @Override
    public String getInfo() {
        if (mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL)) {
            return "[" + ChatFormatting.AQUA + "Crystal" + ChatFormatting.GRAY + "]";
        }
        if (mc.player.getHeldItemOffhand().getItem().equals((Items.TOTEM_OF_UNDYING))) {
            return "[" + ChatFormatting.RED + "Totem" + ChatFormatting.GRAY + "]";
        }
        if (mc.player.getHeldItemOffhand().getItem().equals((Items.GOLDEN_APPLE))) {
            if (mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_SWORD)) {
                if (mc.gameSettings.keyBindUseItem.isKeyDown()) {
                        return "[" + ChatFormatting.YELLOW + "Crystal" + ChatFormatting.GRAY + "]";
                }
            }
        }
        return null;
    }}