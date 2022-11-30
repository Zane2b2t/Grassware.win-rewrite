package me.alpha432.oyvey.util;

<<<<<<< HEAD
import me.alpha432.oyvey.features.modules.Module;
=======
import com.mojang.realmsclient.gui.ChatFormatting;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.modules.player.FakePlayer;
import me.alpha432.oyvey.mixin.mixins.IEntityLivingBase;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
>>>>>>> parent of f5e20b5 (Update EntityUtil.java)
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;

import java.util.Objects;
import java.util.TreeMap;

public class EntityUtil implements MC {

    public static EntityPlayer entityPlayer(final float range) {
        final TreeMap<Float, EntityPlayer> map = new TreeMap<>();
        mc.world.playerEntities.stream().filter(e -> !e.equals(mc.player) && !e.isDead).forEach(entityPlayer -> {
            final float distance = entityPlayer.getDistance(mc.player);
            if (distance < range) {
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
}
