package me.zane.grassware.features.modules.combat;
//WARNING: ALL CONTENT BELONGS TO https://github.com/Zane2b2t , IF ANY OF THE CLASSES CONTAINING THIS WARNING ARENT IN https://github.com/Zane2b2t/Grassware.win-Rewrite INFORM GITHUB TO DMCA
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.util.BlockUtil;
import me.zane.grassware.util.EntityUtil;
import me.zane.grassware.util.InventoryUtil;

import net.minecraft.block.BlockSand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class AutoSand extends Module {

    @Override
    public void onEnable() {
        final EntityPlayer entityPlayer = EntityUtil.entityPlayer(8);

        BlockPos sandPlacePos = new BlockPos(Math.floor(entityPlayer.posX), Math.floor(entityPlayer.posY + 2), Math.floor(entityPlayer.posZ));
        BlockPos playerPos = new BlockPos(Math.floor(entityPlayer.posX), Math.floor(entityPlayer.posY), Math.floor(entityPlayer.posZ));

        int sand = InventoryUtil.findHotbarBlock(BlockSand.class);
        int previousSlot = mc.player.inventory.currentItem;

        if (entityPlayer != null && sand != -1 && sandPlacePos != null && mc.world.getBlockState(playerPos).getBlock() == Blocks.AIR) {
            mc.player.inventory.currentItem = sand;
            mc.playerController.updateController();
            BlockUtil.placeBlock(sandPlacePos, EnumHand.MAIN_HAND, true, false);
            mc.player.inventory.currentItem = previousSlot;
        } else {
            disable();
        }
        disable();
    }
}
