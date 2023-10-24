package me.zane.grassware.manager;
//WARNING: ALL CONTENT BELONGS TO https://github.com/Zane2b2t , IF ANY OF THE CLASSES CONTAINING THIS WARNING ARENT IN https://github.com/Zane2b2t/Grassware.win-Rewrite INFORM GITHUB TO DMCA
import me.zane.grassware.util.MC;
import me.zane.grassware.util.InventoryUtil;
import me.zane.grassware.util.PacketUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class InteractionManager implements MC {
    public List<Block> sneakBlocks = Arrays.asList(
            Blocks.ENDER_CHEST,
            Blocks.ANVIL,
            Blocks.CHEST,
            Blocks.TRAPPED_CHEST,
            Blocks.CRAFTING_TABLE,
            Blocks.BREWING_STAND,
            Blocks.HOPPER,
            Blocks.DROPPER,
            Blocks.DISPENSER,
            Blocks.TRAPDOOR,
            Blocks.ENCHANTING_TABLE
    );
    public List<Block> shulkers = Arrays.asList(
            Blocks.WHITE_SHULKER_BOX,
            Blocks.ORANGE_SHULKER_BOX,
            Blocks.MAGENTA_SHULKER_BOX,
            Blocks.LIGHT_BLUE_SHULKER_BOX,
            Blocks.YELLOW_SHULKER_BOX,
            Blocks.LIME_SHULKER_BOX,
            Blocks.PINK_SHULKER_BOX,
            Blocks.GRAY_SHULKER_BOX,
            Blocks.SILVER_SHULKER_BOX,
            Blocks.CYAN_SHULKER_BOX,
            Blocks.PURPLE_SHULKER_BOX,
            Blocks.BLUE_SHULKER_BOX,
            Blocks.BROWN_SHULKER_BOX,
            Blocks.GREEN_SHULKER_BOX,
            Blocks.RED_SHULKER_BOX,
            Blocks.BLACK_SHULKER_BOX
    );

    public void placeBlock(BlockPos pos, boolean rotate, boolean packet, boolean strict, boolean ignoreEntities) {
        for (EnumFacing enumFacing : EnumFacing.values()) {
            BlockPos directionOffset = pos.offset(enumFacing);
            if (!ignoreEntities) {
                for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
                    if (!(entity instanceof EntityItem || entity instanceof EntityEnderCrystal)) {
                        return;
                    }
                }
            }
            if (strict && !getVisibleSides(directionOffset).contains(enumFacing.getOpposite()) || mc.world.getBlockState(directionOffset).getMaterial().isReplaceable()) {
                continue;
            }
            boolean sprint = mc.player.isSprinting();
            if (sprint) {
                PacketUtil.invoke(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
                mc.player.setSprinting(false);
            }
            Block block = mc.world.getBlockState(directionOffset).getBlock();
            boolean sneak = sneakBlocks.contains(block) || shulkers.contains(block) && !mc.player.isSneaking();
            if (sneak) {
                PacketUtil.invoke(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                mc.player.setSneaking(true);
            }
            Vec3d vec = new Vec3d(directionOffset).add(0.5f, 0.5f, 0.5f).add(new Vec3d(enumFacing.getOpposite().getDirectionVec()).scale(0.5));
            EnumActionResult clickBlock = null;
            if (packet) {
                EnumFacing facing = getFirstEnumFacing(pos);
                EnumFacing opposite = facing.getOpposite();
                BlockPos closest = pos.offset(facing);
                PacketUtil.invoke(new CPacketPlayerTryUseItemOnBlock(closest, opposite, EnumHand.MAIN_HAND, (float) (vec.x - closest.getX()), (float) (vec.y - closest.getY()), (float) (vec.z - closest.getZ())));
            } else {
                clickBlock = mc.playerController.processRightClickBlock(mc.player, mc.world, directionOffset, enumFacing.getOpposite(), vec, EnumHand.MAIN_HAND);
            }
            if (packet || clickBlock != EnumActionResult.FAIL) {
                mc.player.swingArm(EnumHand.MAIN_HAND);
                //  ((IMinecraft) mc).setRightClickDelayTimer(4);
                return;
            }
            if (sneak) {
                PacketUtil.invoke(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                mc.player.setSneaking(false);
            }
            if (sprint) {
                PacketUtil.invoke(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
                mc.player.setSprinting(true);
            }
        }
    }

    public void placeBlock(BlockPos pos, boolean rotate, boolean packet, boolean strict, boolean ignoreEntities, int slot) {
        if (slot == -1) {
            return;
        }
        int currentItem = mc.player.inventory.currentItem;
        InventoryUtil.switchToSlot(slot);
        placeBlock(pos, rotate, packet, strict, ignoreEntities);
        InventoryUtil.switchBack(currentItem);
    }

    public EnumFacing getFirstEnumFacing(BlockPos pos) {
        return getEnumFacingSides(pos).stream().findFirst().orElse(null);
    }

    public ArrayList<EnumFacing> getEnumFacingSides(BlockPos pos) {
        ArrayList<EnumFacing> sides = new ArrayList<>();
        Arrays.stream(EnumFacing.values()).forEach(side -> {
            BlockPos pos1 = pos.offset(side);
            if (mc.world.getBlockState(pos1).getBlock().canCollideCheck(mc.world.getBlockState(pos1), false)) {
                if (!mc.world.getBlockState(pos1).getMaterial().isReplaceable()) {
                    sides.add(side);
                }
            }
        });
        return sides;
    }

    public ArrayList<EnumFacing> getVisibleSides(BlockPos pos) {
        ArrayList<EnumFacing> sides = new ArrayList<>();
        Vec3d vec = new Vec3d(pos).add(0.5, 0.5, 0.5);
        Vec3d eye = mc.player.getPositionEyes(1);
        double facingX = eye.x - vec.x;
        double facingY = eye.y - vec.y;
        double facingZ = eye.z - vec.z;
        if (facingX < -0.5) {
            sides.add(EnumFacing.WEST);
        } else if (facingX > 0.5) {
            sides.add(EnumFacing.EAST);
        } else if (isVisible(pos)) {
            sides.add(EnumFacing.WEST);
            sides.add(EnumFacing.EAST);
        }
        if (facingY < -0.5) {
            sides.add(EnumFacing.DOWN);
        } else if (facingY > 0.5) {
            sides.add(EnumFacing.UP);
        } else {
            sides.add(EnumFacing.DOWN);
            sides.add(EnumFacing.UP);
        }
        if (facingZ < -0.5) {
            sides.add(EnumFacing.NORTH);
        } else if (facingZ > 0.5) {
            sides.add(EnumFacing.SOUTH);
        } else if (isVisible(pos)) {
            sides.add(EnumFacing.NORTH);
            sides.add(EnumFacing.SOUTH);
        }
        return sides;
    }

    private boolean isVisible(BlockPos pos) {
        return !mc.world.getBlockState(pos).isFullBlock() || !mc.world.isAirBlock(pos);
    }

    public EnumFacing getClosestEnumFacing(BlockPos pos) {
        TreeMap<Double, EnumFacing> enumFacingTreeMap = getVisibleSides(pos).stream().collect(Collectors.toMap(enumFacing -> {
            Vec3i distanceToFace = getDistanceToFace(enumFacing);
            return mc.player.getDistanceSq(pos.add(distanceToFace));
        }, enumFacing -> enumFacing, (a, b) -> b, TreeMap::new));
        return enumFacingTreeMap.firstEntry().getValue();
    }


    private Vec3i getDistanceToFace(EnumFacing enumFacing) {
        Vec3i vec3i = new Vec3i(0.5, 0.5, 0.5);
        switch (enumFacing) {
            case NORTH:
                vec3i = new Vec3i(0.5, 0.5, -1.0);
                break;
            case EAST:
                vec3i = new Vec3i(1.0, 0.5, 0.5);
                break;
            case SOUTH:
                vec3i = new Vec3i(0.5, 0.5, 1.0);
                break;
            case WEST:
                vec3i = new Vec3i(-1.0, 0.5, 0.5);
                break;
            case UP:
                vec3i = new Vec3i(0.5, 1.0, 0.5);
                break;
            case DOWN:
                vec3i = new Vec3i(0.5, -1.0, 0.5);
                break;
        }
        return vec3i;
    }
}