package me.zane.grassware.features.modules.misc;

import me.zane.grassware.features.command.Command;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.BooleanSetting;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.init.SoundEvents;

import java.util.HashSet;
import java.util.Set;

public class EntityNotifier extends Module {
    private final Set<Entity> ghasts = new HashSet<>();
    private final Set<Entity> donkeys = new HashSet<>();
    private final Set<Entity> mules = new HashSet<>();
    private final Set<Entity> llamas = new HashSet<>();
    public final BooleanSetting Chat = register("Chat", true);
    public final BooleanSetting Sound = register("Sound", true);
    public final BooleanSetting Ghasts = register("Ghasts", true);
    public final BooleanSetting Donkeys = register("Donkeys", true);
    public final BooleanSetting Mules = register("Mules", true);
    public final BooleanSetting Llamas = register("Llamas", true);

    @SubscribeEvent
    public void onEnable() {
        ghasts.clear();
        donkeys.clear();
        mules.clear();
        llamas.clear();
    }

    public void onUpdate() {
        if (this.Ghasts.getValue()) {
            for (Entity entity : EntityNotifier.mc.world.getLoadedEntityList()) {
                if (!(entity instanceof EntityGhast) || this.ghasts.contains(entity)) continue;
                if (this.Chat.getValue()) {
                    Command.sendMessage("Ghast Detected at: " + Math.round(entity.getPosition().getX()) + "X, " + Math.round(entity.getPosition().getY()) + "Y, " + Math.round(entity.getPosition().getZ()) + "Z.");
                }
                this.ghasts.add(entity);
                if (!this.Sound.getValue()) continue;
                EntityNotifier.mc.player.playSound(SoundEvents.BLOCK_ANVIL_DESTROY, 1.0f, 1.0f);
            }
        }
        if (this.Donkeys.getValue()) {
            for (Entity entity : EntityNotifier.mc.world.getLoadedEntityList()) {
                if (!(entity instanceof EntityDonkey) || this.donkeys.contains(entity)) continue;
                if (this.Chat.getValue()) {
                    Command.sendMessage("Donkey Detected at: " + Math.round(entity.getPosition().getX()) + "X, " + Math.round(entity.getPosition().getY()) + "Y, " + Math.round(entity.getPosition().getZ()) + "Z.");
                }
                this.donkeys.add(entity);
                if (!this.Sound.getValue()) continue;
                EntityNotifier.mc.player.playSound(SoundEvents.BLOCK_ANVIL_DESTROY, 1.0f, 1.0f);
            }
        }
        if (this.Mules.getValue()) {
            for (Entity entity : EntityNotifier.mc.world.getLoadedEntityList()) {
                if (!(entity instanceof EntityMule) || this.mules.contains(entity)) continue;
                if (this.Chat.getValue()) {
                    Command.sendMessage("Mule Detected at: " + Math.round(entity.getPosition().getX()) + "X, " + Math.round(entity.getPosition().getY()) + "Y, " + Math.round(entity.getPosition().getZ()) + "Z.");
                }
                this.mules.add(entity);
                if (!this.Sound.getValue()) continue;
                EntityNotifier.mc.player.playSound(SoundEvents.BLOCK_ANVIL_DESTROY, 1.0f, 1.0f);
            }
        }
        if (this.Llamas.getValue()) {
            for (Entity entity : EntityNotifier.mc.world.getLoadedEntityList()) {
                if (!(entity instanceof EntityLlama) || this.llamas.contains(entity)) continue;
                if (this.Chat.getValue()) {
                    Command.sendMessage("Llama Detected at: " + Math.round(entity.getPosition().getX()) + "X, " + Math.round(entity.getPosition().getY()) + "Y, " + Math.round(entity.getPosition().getZ()) + "Z.");
                }
                this.llamas.add(entity);
                if (!this.Sound.getValue()) continue;
                EntityNotifier.mc.player.playSound(SoundEvents.BLOCK_ANVIL_DESTROY, 1.0f, 1.0f);
            }
        }
    }
}