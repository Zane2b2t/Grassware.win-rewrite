package me.alpha432.oyvey.util;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;

import java.util.*;

public class BlockUtil implements MC {

    public static boolean valid(final BlockPos pos) {
        return mc.world.getBlockState(pos.up()).getBlock().equals(Blocks.AIR)
                && mc.world.getBlockState(pos.up().up()).getBlock().equals(Blocks.AIR)
                && (mc.world.getBlockState(pos).getBlock().equals(Blocks.OBSIDIAN)
                || mc.world.getBlockState(pos).getBlock().equals(Blocks.BEDROCK));
    }

    public static double[] calculateAngle(Vec3d to) {
        final Vec3d eye = mc.player.getPositionEyes(mc.getRenderPartialTicks());
        double yaw = Math.toDegrees(Math.atan2(to.subtract(eye).z, to.subtract(eye).x)) - 90;
        double pitch = Math.toDegrees(-Math.atan2(to.subtract(eye).y, Math.hypot(to.subtract(eye).x, to.subtract(eye).z)));
        return new double[]{MathHelper.wrapDegrees(yaw), MathHelper.wrapDegrees(pitch)};
    }

    public static BlockPos center(){
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    public static float calculateEntityDamage(final EntityEnderCrystal crystal, final EntityPlayer entityPlayer) {
        return calculatePosDamage(crystal.posX, crystal.posY, crystal.posZ, entityPlayer);
    }

    public static float calculatePosDamage(final BlockPos position, final EntityPlayer entityPlayer) {
        return calculatePosDamage(position.getX() + 0.5, position.getY() + 1.0, position.getZ() + 0.5, entityPlayer);
    }

    @SuppressWarnings("ConstantConditions")
    public static float calculatePosDamage(final double posX, final double posY, final double posZ, final Entity entity) {
        final float doubleSize = 12.0F;
        final double size = entity.getDistance(posX, posY, posZ) /  doubleSize;
        final Vec3d vec3d = new Vec3d(posX, posY, posZ);
        final double blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        final double value = (1.0D - size) * blockDensity;
        final float damage = (float) ((int) ((value * value + value) / 2.0D * 7.0D *  doubleSize + 1.0D));
        double finalDamage = 1.0D;

        if (entity instanceof EntityLivingBase) {
            finalDamage = getBlastReduction((EntityLivingBase) entity, getMultipliedDamage(damage), new Explosion(mc.world, null, posX, posY, posZ, 6.0F, false, true));
        }

        return (float) finalDamage;
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
