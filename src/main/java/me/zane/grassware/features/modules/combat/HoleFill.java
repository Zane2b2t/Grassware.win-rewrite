package me.zane.grassware.features.modules.combat;

import me.zane.grassware.GrassWare;
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.PacketEvent;
import me.zane.grassware.event.events.Render3DEvent;
import me.zane.grassware.event.events.UpdatePlayerWalkingEvent;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.features.setting.impl.FloatSetting;
import me.zane.grassware.features.setting.impl.IntSetting;
import me.zane.grassware.manager.HoleManager;
import me.zane.grassware.shader.impl.GradientShader;
import me.zane.grassware.util.BlockUtil;
import me.zane.grassware.util.InventoryUtil;
import me.zane.grassware.util.RenderUtil;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.stream.Collectors;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class HoleFill extends Module {
    private final IntSetting range = register("PlaceRange", 5, 1, 20);
    private final FloatSetting enemyDistance = register("EnemyToHoleDist", 3.0f, 1.0f, 5.0f);
    private final BooleanSetting rotate = register("Rotate", false);
    private final BooleanSetting strictDir = register("StrictDirection", false);
    private final FloatSetting opacity = register("Opacity", 0.5f, 0.0f, 1.0f);
    private final List<BlockPos> toFillPositions = new ArrayList<>();
    private final ICamera camera = new Frustum();
    private final Map<BlockPos, Float> filledPositions = new HashMap<>();
    private final FloatSetting shrinkSpeed = register("ShrinkSpeed", 0.5f, 0.0f, 1.0f);

    float[] rots;
    private static final float OFFSET = 0.5f;
    @EventListener
    public void onUpdate(final UpdatePlayerWalkingEvent event) {
        if (mc.player == null || mc.world == null) return;

        toFillPositions.clear();

        List<HoleManager.HolePos> holes = GrassWare.holeManager.getHoles();
        List<EntityPlayer> enemies = getEnemies();

        for (HoleManager.HolePos hole : holes) {
            List<BlockPos> positions = getHolePositions(hole);
            boolean shouldFill = false;

            for (EntityPlayer enemy : enemies) {
                for (BlockPos pos : positions) {
                    double distanceSq = enemy.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                    if (distanceSq < enemyDistance.getValue() * enemyDistance.getValue() // target range
                            && mc.player.getDistanceSq(pos) <= range.getValue() * range.getValue() // place range
                            && enemy.getPosition().getY() > pos.getY()) { // only fill if target is above the hole (thats the only way they can get inside it)
                        shouldFill = true;
                        break;
                    }
                }
                if (shouldFill) break;
            }

            if (shouldFill) {
                for (BlockPos pos : positions) {
                    if (mc.world.getBlockState(pos).getBlock() == Blocks.AIR && !Surround.Instance.hasEntities(pos, true)) {
                        toFillPositions.add(pos); // for rendering before place
                        placeObsidian(pos);
                    }
                }
            }
        }

        // rendering after place
        Iterator<Map.Entry<BlockPos, Float>> iterator = filledPositions.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BlockPos, Float> entry = iterator.next();
            float progress = entry.getValue() - shrinkSpeed.getValue();
            if (progress <= 0) {
                iterator.remove(); // animation done, stop
            } else {
                entry.setValue(progress);
            }
        }
    }

    private List<EntityPlayer> getEnemies() {
        return mc.world.playerEntities.stream()
                .filter(player -> player != mc.player && !GrassWare.friendManager.isFriend(player.getName()))
                .collect(Collectors.toList());
    }

    private List<BlockPos> getHolePositions(HoleManager.HolePos hole) {
        List<BlockPos> positions = new ArrayList<>();
        BlockPos basePos = hole.getPos();
        positions.add(basePos);

        if (hole.isDouble() && hole.isWestDouble()) {
            positions.add(basePos.west());
        }

        return positions;
    }

    private void placeObsidian(BlockPos pos) {
        int obsidianSlot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
        if (obsidianSlot == -1) return;
        EnumFacing facing = EnumFacing.UP;
        int prevSlot = mc.player.inventory.currentItem;
        Surround.Instance.swap(obsidianSlot);
        if (rotate.getValue()) {
            rots = BlockUtil.calculateRotations(pos, false, false, true);
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rots[0], rots[1], mc.player.onGround));
        }
        if (strictDir.getValue()) {
            facing = BlockUtil.getStrictDirection(pos, rots, range.getValue()); //TODO: fix placing blocks with packet so we can use strictdir
            if (facing == null) return;
        }
        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, true, mc.player.isSneaking());
        //(mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, facing, EnumHand.MAIN_HAND, OFFSET, OFFSET, OFFSET));

        if (rotate.getValue()) {
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround));
        }
        Surround.Instance.swap(prevSlot);
    }

    @EventListener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketBlockChange) {
            SPacketBlockChange packet = event.getPacket();
            BlockPos pos = packet.getBlockPosition();

            if (toFillPositions.contains(pos) && packet.getBlockState().getBlock() != Blocks.AIR) {
                toFillPositions.remove(pos);
                filledPositions.put(pos, 1.0f);
            }
        }

        if (event.getPacket() instanceof SPacketMultiBlockChange) {
            SPacketMultiBlockChange packet = event.getPacket();

            for (SPacketMultiBlockChange.BlockUpdateData updateData : packet.getChangedBlocks()) {
                BlockPos pos = updateData.getPos();

                if (toFillPositions.contains(pos) && updateData.getBlockState().getBlock() != Blocks.AIR) {
                    toFillPositions.remove(pos);
                    filledPositions.put(pos, 1.0f);
                }
            }
        }
    }

    @EventListener
    public void onRender3D(final Render3DEvent event) {
        camera.setPosition(Objects.requireNonNull(mc.getRenderViewEntity()).posX,
                mc.getRenderViewEntity().posY,
                mc.getRenderViewEntity().posZ);

        for (BlockPos pos : toFillPositions) {
            AxisAlignedBB bb = new AxisAlignedBB(pos);
            if (!camera.isBoundingBoxInFrustum(bb.grow(2.0))) {
                continue;
            }
            GradientShader.setup(opacity.getValue());
            RenderUtil.boxShader(pos);
            GradientShader.finish();

            GradientShader.setup(1.0f);
            RenderUtil.outlineShader(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
            GradientShader.finish();
        }

        for (Map.Entry<BlockPos, Float> entry : filledPositions.entrySet()) {
            BlockPos pos = entry.getKey();
            float progress = entry.getValue();
            float t = 1 - progress;
            // quadratic ease out: t * (2 - t)
            float eased_t = t * (2 - t);
            double shrinkFactor = 0.5 * eased_t;
            float opacityScale = 1 - eased_t;

            AxisAlignedBB bb = new AxisAlignedBB(pos);
            AxisAlignedBB shrunkBB = bb.shrink(shrinkFactor);
            if (!camera.isBoundingBoxInFrustum(shrunkBB.grow(2.0))) {
                continue;
            }

            GradientShader.setup(opacity.getValue() * opacityScale);
            RenderUtil.boxShader(shrunkBB.minX, shrunkBB.minY, shrunkBB.minZ, shrunkBB.maxX, shrunkBB.maxY, shrunkBB.maxZ);
            GradientShader.finish();

            GradientShader.setup((opacity.getValue() * opacityScale) + 0.47f); //outline more bold
            RenderUtil.outlineShader(shrunkBB.minX, shrunkBB.minY, shrunkBB.minZ, shrunkBB.maxX, shrunkBB.maxY, shrunkBB.maxZ);
            GradientShader.finish();
        }
    }
}