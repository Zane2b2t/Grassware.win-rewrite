package me.zane.grassware.util;
//WARNING: ALL CONTENT BELONGS TO https://github.com/Zane2b2t , IF ANY OF THE CLASSES CONTAINING THIS WARNING ARENT IN https://github.com/Zane2b2t/Grassware.win-Rewrite INFORM GITHUB TO DMCA
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class BlockUtil implements MC {
   // EntityOtherPlayerMP futurePlayer = new EntityOtherPlayerMP(mc.world, entityPlayer.getGameProfile());
    private static final Vec3i[] hole = new Vec3i[]{
            new Vec3i(-1, 0, 0),
            new Vec3i(1, 0, 0),
            new Vec3i(0, 0, 1),
            new Vec3i(0, 0, -1),
            new Vec3i(0, -1, 0),
    };


    public static boolean valid(BlockPos pos, boolean updated) {
        return mc.world.getBlockState(pos.up()).getBlock().equals(Blocks.AIR)
                && (mc.world.getBlockState(pos.up().up()).getBlock().equals(Blocks.AIR) || updated)
                && (mc.world.getBlockState(pos).getBlock().equals(Blocks.OBSIDIAN)
                || mc.world.getBlockState(pos).getBlock().equals(Blocks.BEDROCK));
    }



    public static BlockPos center() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }
    public static BlockPos getPosition() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }
    public static boolean empty(BlockPos pos) {
        return mc.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(new BlockPos(pos.getX() + 0.5f, pos.getY() + 1.0f, pos.getZ() + 0.5f))).isEmpty();
    }
    public static boolean isReplaceable(BlockPos pos){
        return mc.world.getBlockState(pos).getMaterial().isReplaceable();
    }

    public static BlockPos getPosition(EntityPlayer entityPlayer) {
        return new BlockPos(Math.floor(entityPlayer.posX), Math.floor(entityPlayer.posY), Math.floor(entityPlayer.posZ));
    }

    public static boolean hasCrystal(BlockPos pos) {
        return !mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(new BlockPos(pos.getX() + 0.0f, pos.getY() + 1.5f, pos.getZ() + 0.0f))).isEmpty();
    }

    public static float distance(BlockPos pos) {
        return (float) Math.sqrt(mc.player.getDistanceSq(pos));
    }
    public static BlockPos center(BlockPos pos) {
        return pos.add(0.5f, 0.5f, 0.5f);
    }
    public static boolean is(BlockPos pos, Block block) {
        return mc.world.getBlockState(pos).getBlock().equals(block);
    }
    public static boolean isPlayerSafe(EntityPlayer entityPlayer) {
        final BlockPos pos = entityPlayer.getPosition();
        if (isNotIntersecting(entityPlayer)) {
            return isBedrockOrObsidianOrEchest(pos.north()) && isBedrockOrObsidianOrEchest(pos.east()) && isBedrockOrObsidianOrEchest(pos.south()) && isBedrockOrObsidianOrEchest(pos.west()) && isBedrockOrObsidianOrEchest(pos.down());
        } else {
            return isIntersectingSafe(entityPlayer);
        }
    }
    public static boolean isNotIntersecting(EntityPlayer entityPlayer) {
        final BlockPos pos = entityPlayer.getPosition();
        final AxisAlignedBB bb = entityPlayer.getEntityBoundingBox();
        return (!air(pos.north()) || !bb.intersects(new AxisAlignedBB(pos.north()))) && (!air(pos.east()) || !bb.intersects(new AxisAlignedBB(pos.east()))) && (!air(pos.south()) || !bb.intersects(new AxisAlignedBB(pos.south()))) && (!air(pos.west()) || !bb.intersects(new AxisAlignedBB(pos.west())));
    }

    public static boolean isIntersectingSafe(EntityPlayer entityPlayer) {
        final BlockPos pos = entityPlayer.getPosition();
        final AxisAlignedBB bb = entityPlayer.getEntityBoundingBox();
        if (air(pos.north()) && bb.intersects(new AxisAlignedBB(pos.north()))) {
            final BlockPos pos1 = pos.north();
            if (!isBedrockOrObsidianOrEchest(pos1.north()) || !isBedrockOrObsidianOrEchest(pos1.east()) || !isBedrockOrObsidianOrEchest(pos1.west()) || !isBedrockOrObsidianOrEchest(pos1.down()))
                return false;
        }
        if (air(pos.east()) && bb.intersects(new AxisAlignedBB(pos.east()))) {
            final BlockPos pos1 = pos.east();
            if (!isBedrockOrObsidianOrEchest(pos1.north()) || !isBedrockOrObsidianOrEchest(pos1.east()) || !isBedrockOrObsidianOrEchest(pos1.south()) || !isBedrockOrObsidianOrEchest(pos1.down()))
                return false;
        }
        if (air(pos.south()) && bb.intersects(new AxisAlignedBB(pos.south()))) {
            final BlockPos pos1 = pos.south();
            if (!isBedrockOrObsidianOrEchest(pos1.east()) || !isBedrockOrObsidianOrEchest(pos1.south()) || !isBedrockOrObsidianOrEchest(pos1.west()) || !isBedrockOrObsidianOrEchest(pos1.down()))
                return false;
        }
        if (air(pos.west()) && bb.intersects(new AxisAlignedBB(pos.west()))) {
            final BlockPos pos1 = pos.west();
            return isBedrockOrObsidianOrEchest(pos1.north()) && isBedrockOrObsidianOrEchest(pos1.south()) && isBedrockOrObsidianOrEchest(pos1.west()) && isBedrockOrObsidianOrEchest(pos1.down());
        }
        return true;
    }
    public static boolean isBedrockOrObsidianOrEchest(final BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().equals(Blocks.BEDROCK) || mc.world.getBlockState(pos).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos).getBlock().equals(Blocks.ENDER_CHEST);
    }

    public static boolean air(final BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR);
    }



    public static float calculateEntityDamage(final EntityEnderCrystal crystal, final EntityPlayer entityPlayer) {
        return calculatePosDamage(crystal.posX, crystal.posY, crystal.posZ, entityPlayer);
    }

    public static float calculatePosDamage(final BlockPos position, final EntityPlayer entityPlayer) {
        return calculatePosDamage(position.getX() + 0.5, position.getY() + 1.0, position.getZ() + 0.5, entityPlayer);
    }
    public static float calculatePosDamageEx(final BlockPos position, final Vec3d futurePos) {
        // Calculate the damage based on the position
        // This is just a placeholder, replace with your actual damage calculation
        float damage = (float) (position.distanceSqToCenter(futurePos.x, futurePos.y, futurePos.z) / 100.0);

        return damage;
    }


    public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction, boolean packet) {
        if (packet) {
            float f = (float) (vec.x - (double) pos.getX());
            float f1 = (float) (vec.y - (double) pos.getY());
            float f2 = (float) (vec.z - (double) pos.getZ());
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
        } else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, direction, vec, hand);
        }
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.rightClickDelayTimer = 4;
    }

    @SuppressWarnings("ConstantConditions")
    public static float calculatePosDamage(final double posX, final double posY, final double posZ, final Entity entity) {
        final float doubleSize = 12.0F;
        final double size = entity.getDistance(posX, posY, posZ) / doubleSize;
        final Vec3d vec3d = new Vec3d(posX, posY, posZ);
        final double blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        final double value = (1.0D - size) * blockDensity;
        final float damage = (float) ((int) ((value * value + value) / 2.0D * 7.0D * doubleSize + 1.0D));
        double finalDamage = 1.0D;

        if (entity instanceof EntityLivingBase) {
            finalDamage = getBlastReduction((EntityLivingBase) entity, getMultipliedDamage(damage), new Explosion(mc.world, null, posX, posY, posZ, 6.0F, false, true));
        }

        return (float) finalDamage;
    }

    public static boolean placeBlock(BlockPos pos, EnumHand hand, boolean packet, boolean isSneaking) {
        boolean sneaking = false;
        EnumFacing side = BlockUtil.getFirstFacing(pos);
        if (side == null) {
            return isSneaking;
        }
        List<Block> blackList = Arrays.asList(Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, Blocks.TRAPDOOR, Blocks.ENCHANTING_TABLE);
        List<Block> shulkerList = Arrays.asList(Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX);
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = BlockUtil.mc.world.getBlockState(neighbour).getBlock();
        if (!BlockUtil.mc.player.isSneaking() && (blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock))) {
            BlockUtil.mc.player.connection.sendPacket(new CPacketEntityAction(BlockUtil.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            BlockUtil.mc.player.setSneaking(true);
            sneaking = true;
        }
        BlockUtil.rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        BlockUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
        BlockUtil.mc.rightClickDelayTimer = 4;
        return sneaking || isSneaking;
    }

    public static List<EnumFacing> getPossibleSides(BlockPos pos) {
        ArrayList<EnumFacing> facings = new ArrayList<>();
        if (mc.world == null || pos == null) {
            return facings;
        }
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = pos.offset(side);
            IBlockState blockState = mc.world.getBlockState(neighbour);
            if (!blockState.getBlock().canCollideCheck(blockState, false) || blockState.getMaterial().isReplaceable())
                continue;
            facings.add(side);
        }
        return facings;
    }

    public static EnumFacing getFirstFacing(BlockPos pos) {
        Iterator<EnumFacing> iterator = BlockUtil.getPossibleSides(pos).iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    private static float getMultipliedDamage(final float damage) {
        return damage * (mc.world.getDifficulty().getId() == 0 ? 0.0F : (mc.world.getDifficulty().getId() == 2 ? 1.0F : (mc.world.getDifficulty().getId() == 1 ? 0.5F : 1.5F)));
    }

    public static float getBlastReduction(final EntityLivingBase entity, final float damageI, final Explosion explosion) {
        float damage = damageI;
        final DamageSource ds = DamageSource.causeExplosionDamage(explosion);
        damage = CombatRules.getDamageAfterAbsorb(damage, entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        int k = 0;
        try {
            k = EnchantmentHelper.getEnchantmentModifierDamage(entity.getArmorInventoryList(), ds);
        } catch (Exception ignored) {
        }
        damage = damage * (1.0F - MathHelper.clamp(k, 0.0F, 20.0F) / 25.0F);

        if (entity.isPotionActive(MobEffects.RESISTANCE)) {
            damage = damage - (damage / 4);
        }

        return damage;
    }


    public static List<BlockPos> getBlocksInRadius(final float range) {
        final List<BlockPos> posses = new ArrayList<>();
        if (mc.player == null) {
            return posses;
        }
        for (int x = (int) -range; x < range; x++) {
            for (int y = (int) -range; y < range; y++) {
                for (int z = (int) -range; z < range; z++) {
                    final BlockPos position = mc.player.getPosition().add(x, y, z);
                    if (mc.player.getDistance(position.getX() + 0.5, position.getY() + 1, position.getZ() + 0.5) <= range) {
                        posses.add(position);
                    }
                }
            }
        }
        return posses;
    }
}
