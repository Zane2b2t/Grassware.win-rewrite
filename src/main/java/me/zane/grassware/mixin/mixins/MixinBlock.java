package me.zane.grassware.mixin.mixins;

import me.zane.grassware.event.events.BoundingBoxEvent;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Block.class)
public class MixinBlock {

    @Inject(method = "addCollisionBoxToList(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/entity/Entity;Z)V", at = @At("HEAD"))
    private void in(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entity, boolean isActualState, CallbackInfo ci) {
        if (Minecraft.getMinecraft().player == null || entity == null || world == null || entityBox == null) {
            return;
        }

        Block block = (Block) (Object) this;
        BoundingBoxEvent event = new BoundingBoxEvent(block, pos, block.getCollisionBoundingBox(state, world, pos), collidingBoxes, entity);
        MinecraftForge.EVENT_BUS.post(event);
    }

}