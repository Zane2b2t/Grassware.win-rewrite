package me.zane.grassware.util;
//WARNING: ALL CONTENT BELONGS TO https://github.com/Zane2b2t , IF ANY OF THE CLASSES CONTAINING THIS WARNING ARENT IN https://github.com/Zane2b2t/Grassware.win-Rewrite INFORM GITHUB TO DMCA
import com.mojang.realmsclient.gui.ChatFormatting;
import jdk.nashorn.internal.ir.Block;
import me.zane.grassware.GrassWare;
import me.zane.grassware.features.command.Command;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;

import java.util.Objects;
import java.util.TreeMap;

public class EntityUtil implements MC {
    public static EntityOtherPlayerMP setupEntity(EntityPlayer entityPlayer, Vec3d vec) { //maybe for PopESP?
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
    public static void breakCrystal(EntityEnderCrystal crystal, boolean rotate, boolean debug, boolean strictDir, float range) {
        if ((range * range) - 8 >= mc.player.getPositionEyes(1).squareDistanceTo(crystal.getPositionVector())) {
            if (rotate) {
                float[] rotations = BlockUtil.calculateRotations(crystal.getPosition().down().add(0, strictDir ? 0.5 : 1, 0), true, true, true); //if strictdir is on, look at the top face of the block below the crystal, if not look at the crystal itself
                mc.getConnection().sendPacket(new CPacketPlayer.Rotation(rotations[0], rotations[1], mc.player.onGround));
            }
            mc.getConnection().sendPacket(new CPacketUseEntity(crystal));
            if (debug) {
                Command.sendMessage("Attacked " + crystal.getEntityId() + "At range^2 " + ChatFormatting.WHITE + mc.player.getPositionEyes(1).squareDistanceTo(crystal.getPositionVector()));
            }
            if (rotate) {
                mc.getConnection().sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround));
            }
        }
    }
}
