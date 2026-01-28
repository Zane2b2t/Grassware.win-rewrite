package me.zane.grassware.features.modules.combat;

import me.zane.grassware.GrassWare;
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.Render3DEvent;
import me.zane.grassware.event.events.UpdatePlayerWalkingEvent;
import me.zane.grassware.features.command.Command;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.shader.impl.GradientShader;
import me.zane.grassware.util.BlockUtil;
import me.zane.grassware.util.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class AutoMine extends Module {

    private final BooleanSetting fill = register("Fill", true);
    private final BooleanSetting outline = register("Outline", true);
    private final BooleanSetting animateOnBreak = register("AnimateBreak", true);

    private static final double MAX_DISTANCE = 6.0;

    private final Map<BlockPos, Float> animatedBlocks = new HashMap<>();
    private final Set<BlockPos> targetBlocks = new HashSet<>();
    private final Frustum camera = new Frustum();
    private static final float ANIM_SPEED = 0.08f;

    @EventListener
    public void onUpdate(UpdatePlayerWalkingEvent event) {
        if (mc.player == null || mc.world == null) return;

        targetBlocks.clear();

        List<EntityPlayer> targets = mc.world.playerEntities.stream()
                .filter(p -> p != mc.player && !GrassWare.friendManager.isFriend(p.getName())
                        && mc.player.getDistanceSq(p) <= MAX_DISTANCE * MAX_DISTANCE)
                .collect(Collectors.toList());

        for (EntityPlayer target : targets) {
            List<BlockPos> surround = BlockUtil.getSurroundPoses(target);


            for (BlockPos block : surround) {
                Block b = mc.world.getBlockState(block).getBlock();
                if (b == Blocks.OBSIDIAN || b == Blocks.ENDER_CHEST) {
                    Command.sendMessage("found block at " + block.getX() + ", " +block.getY() + ", " + block.getZ());
                    targetBlocks.add(block);
                }
            }
        }

        Iterator<BlockPos> it = new HashSet<>(targetBlocks).iterator();
        while (it.hasNext()) {
            BlockPos pos = it.next();
            Block b = mc.world.getBlockState(pos).getBlock();
            if (b == Blocks.AIR && animateOnBreak.getValue()) {
                animatedBlocks.put(pos, 1.0f);
                it.remove();
                targetBlocks.remove(pos);
            }
        }

        Iterator<Map.Entry<BlockPos, Float>> animIt = animatedBlocks.entrySet().iterator();
        while (animIt.hasNext()) {
            Map.Entry<BlockPos, Float> entry = animIt.next();
            float progress = entry.getValue() - ANIM_SPEED;
            if (progress <= 0) animIt.remove();
            else entry.setValue(progress);
        }
    }

    @EventListener
    public void onRender3D(Render3DEvent event) {
        if (mc.getRenderViewEntity() == null) return;

        camera.setPosition(mc.getRenderViewEntity().posX,
                mc.getRenderViewEntity().posY,
                mc.getRenderViewEntity().posZ);

        for (BlockPos pos : targetBlocks) {
            AxisAlignedBB bb = new AxisAlignedBB(pos);
            if (!camera.isBoundingBoxInFrustum(bb.grow(2.0))) continue;

            if (fill.getValue()) {
                GradientShader.setup(0.35f);
                RenderUtil.boxShader(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
                GradientShader.finish();
            }

            if (outline.getValue()) {
                GradientShader.setup(0.8f);
                RenderUtil.outlineShader(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
                GradientShader.finish();
            }
        }

        for (Map.Entry<BlockPos, Float> entry : animatedBlocks.entrySet()) {
            BlockPos pos = entry.getKey();
            float progress = entry.getValue();
            float eased = progress * (2 - progress);
            double shrink = 0.5 * (1 - eased);
            float opacityScale = eased;

            AxisAlignedBB bb = new AxisAlignedBB(pos).shrink(shrink);
            if (!camera.isBoundingBoxInFrustum(bb.grow(2.0))) continue;

            if (fill.getValue()) {
                GradientShader.setup(0.35f * opacityScale);
                RenderUtil.boxShader(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
                GradientShader.finish();
            }

            if (outline.getValue()) {
                GradientShader.setup((0.35f * opacityScale) + 0.45f);
                RenderUtil.outlineShader(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
                GradientShader.finish();
            }
        }
    }
}
