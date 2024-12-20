package me.zane.grassware.features.modules.combat;
//WARNING: ALL CONTENT BELONGS TO https://github.com/Zane2b2t , IF ANY OF THE CLASSES CONTAINING THIS WARNING ARENT IN https://github.com/Zane2b2t/Grassware.win-Rewrite INFORM GITHUB TO DMCA
import com.mojang.realmsclient.gui.ChatFormatting;
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.features.command.Command;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.features.setting.impl.FloatSetting;
import me.zane.grassware.event.events.UpdatePlayerWalkingEvent;

import me.zane.grassware.util.BlockUtil;
import me.zane.grassware.util.EntityUtil;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayer;

import java.util.Arrays;

public class Offhand extends Module {
    private final FloatSetting health = register("Health", 14.0f, 0.0f, 36.0f);
    private final FloatSetting defualtHealthVal = register("DHV", 14.0f, 0.0f, 36.0f);
    private final BooleanSetting halfInHole = register("HalfInHole", true);
    private final BooleanSetting lethaCheck = register("Lethal", true);
    private final BooleanSetting debug = register("Debug", true);
    float selfDamage;

    @EventListener
    public void onUpdate(final UpdatePlayerWalkingEvent event) {
        final int slot = slot();
        if (slot != -1) {
            swapItem(slot);
        }
    }
    private void idk() {
        if (mc.player.getHeldItemOffhand().isEmpty) { //idfk i want it so when we have no totems it doesnt remove crystals from offhand
            inventorySlot(Items.END_CRYSTAL);
        }
    }

    private int slot() {
        if (mc.currentScreen != null) {
            return -1;
        }
        final int totem = inventorySlot(Items.TOTEM_OF_UNDYING);
        if (totem == -1) {
            health.setValue(0.1f);
        } else {
            health.setValue(defualtHealthVal.getValue());
        }
        if (mc.player.getHealth() + mc.player.getAbsorptionAmount() <= (BlockUtil.isPlayerSafe(mc.player) && halfInHole.getValue() ? health.getValue() / 2 : health.getValue())) {
            return totem;
        }
        if (isLetheal() && lethaCheck.getValue() && totem != -1) { //if a crystal is lethal, and we have totem inventory and the lethal setting is enabled. (this is nbot in order i cba)
            if (debug.getValue()) {
                Command.sendMessage(ChatFormatting.WHITE + "Lethal, selfDamage:" + selfDamage + " currentHealth:" + EntityUtil.getHealth(mc.player) + " healthCfg:" + health.getValue());
            }
            return totem; //i love untested code :heart:
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
    private boolean isLetheal() {
        mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityEnderCrystal
                && !(mc.player.getDistanceSq(entity) > 36)).map(entity -> (EntityEnderCrystal) entity).forEach(entityEnderCrystal -> { //get all crystals within 6 block radius
             selfDamage = BlockUtil.calculateEntityDamage(entityEnderCrystal, mc.player);
    });
        return selfDamage > EntityUtil.getHealth(mc.player); //true if self damage is more than player's health
    }

    @Override
    public String getInfo() {
        if (health.getValue() >= 36.0) {
            return " [" + ChatFormatting.WHITE + "Totem" + ChatFormatting.RESET + "]"; //when somebody is mainhanding (white text)
        }
        if (mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL)) {
            return " [" + ChatFormatting.WHITE + "Crystal" + ChatFormatting.RESET + "]";
        }
        if (mc.player.getHeldItemOffhand().getItem().equals((Items.TOTEM_OF_UNDYING)) && health.getValue() < 36.0) { //when somebody is offhanding but he's low health (red text)
            return " [" + ChatFormatting.RED + "Totem" + ChatFormatting.RESET + "]";
        }
        if (mc.player.getHeldItemOffhand().getItem().equals((Items.GOLDEN_APPLE))) { //if we're gappling it shows crystal but in gold
                        return " [" + ChatFormatting.GOLD + "Crystal" + ChatFormatting.RESET + "]";
                }
        return null;}} //i like smile