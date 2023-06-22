package me.zane.grassware.util;

import me.zane.grassware.GrassWare;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.util.math.Vec3d;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;

import java.util.Objects;
import java.util.TreeMap;

public class EntityUtil implements MC {

    public static EntityOtherPlayerMP setupEntity(EntityPlayer entityPlayer, Vec3d vec) {
        EntityOtherPlayerMP entityOtherPlayerMP1 = new EntityOtherPlayerMP(mc.world, entityPlayer.getGameProfile());
        entityOtherPlayerMP1.copyLocationAndAnglesFrom(entityPlayer);
        entityOtherPlayerMP1.rotationYawHead = entityPlayer.rotationYawHead;
        entityOtherPlayerMP1.prevRotationYawHead = entityPlayer.rotationYawHead;
        entityOtherPlayerMP1.rotationYaw = entityPlayer.rotationYaw;
        entityOtherPlayerMP1.prevRotationYaw = entityPlayer.rotationYaw;
        entityOtherPlayerMP1.rotationPitch = entityPlayer.rotationPitch;
        entityOtherPlayerMP1.prevRotationPitch = entityPlayer.rotationPitch;
        entityOtherPlayerMP1.cameraYaw = entityPlayer.rotationYaw;
        entityOtherPlayerMP1.cameraPitch = entityPlayer.rotationPitch;
        entityOtherPlayerMP1.limbSwing = entityPlayer.limbSwing;
        entityOtherPlayerMP1.setPosition(vec.x, vec.y, vec.z);
        return entityOtherPlayerMP1;
    }

    public static EntityPlayer entityPlayer(final float range) {
        final TreeMap<Float, EntityPlayer> map = new TreeMap<>();
        mc.world.playerEntities.stream().filter(e -> !e.equals(mc.player) && !e.isDead).forEach(entityPlayer -> {
            final float distance = entityPlayer.getDistance(mc.player);
            if (distance < range && !GrassWare.friendManager.isFriend(entityPlayer.getName())) {
                map.put(distance, entityPlayer);
            }
        });
        if (!map.isEmpty()) {
            return map.firstEntry().getValue();
        }
        return null;
    }

    public static double getMaxSpeed() {
        double maxModifier = 0.2873;
        if (mc.player.isPotionActive(Objects.requireNonNull(Potion.getPotionById(1)))) {
            maxModifier *= 1.0 + 0.2 * (Objects.requireNonNull(mc.player.getActivePotionEffect(Objects.requireNonNull(Potion.getPotionById(1)))).getAmplifier() + 1);
        }
        return maxModifier;
    }


    public static double getBaseMotionSpeed() {
        double event = 0.272;
        if (mc.player.isPotionActive(MobEffects.SPEED)) {
            int var3 = Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            event *= 1.0 + 0.2 * var3;
        }
        return event;
    }
    public static EntityPlayer getEntityPlayer(float range) {
        EntityPlayer lowest = null;
        for (EntityPlayer entityPlayer : mc.world.playerEntities) {
            if (entityPlayer.equals(mc.player)) {
                continue;
            }
            if (entityPlayer.isDead || entityPlayer.getHealth() <= 0.0f) {
                continue;
            }
            if (mc.player.getDistance(entityPlayer) > range) {
                continue;
            }
            if (!GrassWare.friendManager.isFriend(entityPlayer.getName())) {
                continue;
            }
            if (lowest == null || mc.player.getDistance(entityPlayer) < mc.player.getDistance(lowest)) {
                lowest = entityPlayer;
            }
        }
        return lowest;
    }

    public static float getHealth(EntityPlayer entityPlayer) {
        return entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount();
    }
}
