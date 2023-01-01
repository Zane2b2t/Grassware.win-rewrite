package me.zane.grassware.features.modules.misc;

import java.util.HashSet;
import java.util.Set;

import me.zane.grassware.features.command.Command;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.Setting;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.event.bus.EventListener;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.init.SoundEvents;

public class GhastNotifier extends Module {

    private Set<Entity> ghasts = new HashSet<Entity>();
    public BooleanSetting chat = register("Chat", true);
    public BooleanSetting sound = register("Sound", true);


    public void onEnable() {
        this.ghasts.clear();
    }

    @Override
    public void onUpdate() {
        for (Entity entity : GhastNotifier.mc.world.getLoadedEntityList()) {
            if (!(entity instanceof EntityGhast) || this.ghasts.contains(entity)) continue;
            if (this.Chat.getValue().BooleanValue()) {
                Command.sendMessage("Ghast Detected at: " + entity.getPosition().getX() + "x, " + entity.getPosition().getY() + "y, " + entity.getPosition().getZ() + "z.");
            }
            this.ghasts.add(entity);
            if (this.Sound.getValue().BooleanValue()) continue; 
            GhastNotifier.mc.player.playSound(SoundEvents.BLOCK_ANVIL_DESTROY, 1.0f, 1.0f);
        }
    }
}
