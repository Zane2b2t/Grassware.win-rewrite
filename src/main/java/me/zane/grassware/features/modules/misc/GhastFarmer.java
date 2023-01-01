package me.zane.grassware.features.modules.misc;

import me.zane.grassware.util.MathUtil;
import me.zane.grassware.features.command.Command;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.Setting;
import me.zane.grassware.features.setting.impl.IntSetting;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.init.SoundEvents;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.init.Items;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Set;

public class GhastFarmer extends Module {
    public Setting<String> baritonePrefix = this.register(new Setting<String>("BaritonePrefix", "#"));
    public IntSetting radius = this.register(new IntSetting("Radius", 45, 1, 100));
    private Set<Entity> entities = new HashSet<Entity>();
    BlockPos startPos = null;
    double itemX;
    double itemY;
    double itemZ;
    double ghastX;
    double ghastY;
    double ghastZ;


    @SubscribeEvent
    public void onEnable() {
        if (!GhastFarmer.fullNullCheck()) {
            this.startPos = GhastFarmer.mc.player.getPosition();
        }
        this.entities.clear();
    }

    @SubscribeEvent
    public void onDisable() {
        GhastFarmer.mc.player.sendChatMessage(this.baritonePrefix.getValue() + "cancel");
    }
// this is an optimized version of the BigBullet'z outdated ghast farmer -ZANE
    @SubscribeEvent
    public void onUpdate() {
        if (GhastFarmer.mc.player == null || GhastFarmer.mc.world == null) {
            return;
        }
        Entity ghastEnt = null;
        for (Entity entity2 : GhastFarmer.mc.world.loadedEntityList) {
            if (!(entity2 instanceof EntityGhast)) continue;
            ghastEnt = entity2;
            this.ghastX = (int)entity2.posX;
            this.ghastY = (int)entity2.posY;
            this.ghastZ = (int)entity2.posZ;
        }
        ArrayList entityItems = new ArrayList();
        entityItems.addAll(GhastFarmer.mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityItem).map(entity -> (EntityItem)entity).filter(entityItem -> entityItem.getItem().getItem() == Items.GHAST_TEAR).collect(Collectors.toList()));
        Entity itemEnt = null;
        Iterator iterator = entityItems.iterator();
        while (iterator.hasNext()) {
            Entity item;
            itemEnt = item = (Entity)iterator.next();
            this.itemX = MathUtil.round(item.posX, 2);
            this.itemY = MathUtil.round(item.posY, 2);
            this.itemZ = MathUtil.round(item.posZ, 2);
        }
        if (ghastEnt != null) {
            GhastFarmer.mc.player.sendChatMessage(this.baritonePrefix.getValue() + "goto " + this.ghastX + " " + this.ghastY + " " + this.ghastZ);
        } else if (itemEnt != null) {
            GhastFarmer.mc.player.sendChatMessage(this.baritonePrefix.getValue() + "goto " + this.itemX + " " + this.itemY + " " + this.itemZ);
        } else if (this.startPos != null) {
            GhastFarmer.mc.player.sendChatMessage(this.baritonePrefix.getValue() + "goto " + this.startPos.getX() + " " + this.startPos.getY() + " " + this.startPos.getZ());
        }
    }
}
