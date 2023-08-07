package me.zane.grassware.features.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.zane.grassware.GrassWare;
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.Render3DEvent;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.features.setting.impl.FloatSetting;
import me.zane.grassware.features.setting.impl.ModeSetting;
import me.zane.grassware.shader.impl.GradientShader;
import me.zane.grassware.util.BlockUtil;
import me.zane.grassware.util.MathUtil;
import me.zane.grassware.util.RenderUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.*;

/**
 * @author zane4417/zaneATAT
 * @version 1.0
 * @since 8/8/23
 */
public class AutoCrystalRewrite extends Module {
    private final ModeSetting page = register("Page", "Calculations", Arrays.asList("Calculations", "Place", "Break", "Render", "Misc"));

    //Calculations
    private final BooleanSetting updated = register("1.13+", true);
    private final FloatSetting targetRange = register("TargetRange", 10.0f, 1.0f, 12.0f);
    private final FloatSetting maximumDamage = register("MaxSelf", 6.0f, 1.0f, 12.0f);
    private final FloatSetting minimumDamage = register("MinDamage", 4.0f, 1.0f, 16.0f);
    //Place
    private final FloatSetting placeRange = register("PlaceRange", 4.5f, 1.0f, 6.0f);
    private final FloatSetting placeWallRange = register("PlaceWall", 4.5f, 1.0f, 6.0f);
    //Break
    private final FloatSetting breakRange = register("BreakRange", 4.5f, 1.0f, 6.0f);
    private final FloatSetting breakWallRange = register("BreakTrace", 4.5f, 1.0f, 6.0f);
    private final ModeSetting setDead = register("SetDead", "Both", Arrays.asList("SetDead", "Remove", "Both"));
    private final BooleanSetting fastRemove = register("FastRemove", false);

    //Render
    private final FloatSetting opacity = register("Opacity", 0.5f, 0.0f, 1.0f);
    private final FloatSetting defualtOpacityVal = register("DOV", 0.5f, 0.0f, 1.0f);

    //The stuff
    private BlockPos placedPos;
    private BlockPos lastPos;


    //Break Code
    private float breakRange(Entity entity) {
        if (mc.player.canEntityBeSeen(entity))
            return breakRange.getValue();
        return breakWallRange.getValue();
    }
    private void handleSetDead(EntityEnderCrystal crystal) {
        (mc.getConnection()).sendPacket(new CPacketUseEntity(crystal));
        if (setDead.getValue().equals("Set Dead") || setDead.getValue().equals("Both"))
            crystal.setDead();
        if (setDead.getValue().equals("Remove") || setDead.getValue().equals("Both"))
            mc.world.removeEntity(crystal);
    }
   private void handleFastRemove(EntityEnderCrystal crystal) {
       mc.addScheduledTask(() -> {
           mc.world.removeEntity(crystal);
           mc.world.removeEntityDangerously(crystal);
       });
   }

    //Calc Code
    public EntityEnderCrystal getCrystal(BlockPos pos) {
        if (mc.player == null || mc.world == null) {
            return null;
        }
        java.util.List<Entity> entities = mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos));
        for (Entity entity : entities) {
            if (entity instanceof EntityEnderCrystal) {
                return (EntityEnderCrystal) entity;
            }
        }
        return null;
    }

    private EntityEnderCrystal crystal(final EntityPlayer entityPlayer) {
        final TreeMap<Float, EntityEnderCrystal> map = new TreeMap<>();

        mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityEnderCrystal && !(mc.player.getDistance(entity) > breakRange(entity))).map(entity -> (EntityEnderCrystal) entity).forEach(entityEnderCrystal -> {
            final float selfDamage = BlockUtil.calculateEntityDamage(entityEnderCrystal, mc.player);
            if (selfDamage > maximumDamage.getValue()) {
                return;
            }
            final float enemyDamage = BlockUtil.calculateEntityDamage(entityEnderCrystal, entityPlayer);
            if (enemyDamage < minimumDamage.getValue()) {
                return;
            }
            final float damage = enemyDamage - selfDamage;
            if (selfDamage > mc.player.getHealth() + mc.player.getAbsorptionAmount()) {
                return;
            }
            map.put(damage, entityEnderCrystal);
        });

        if (!map.isEmpty()) {
            return map.lastEntry().getValue();
        }

        return null;
    }

    private BlockPos pos(final EntityPlayer entityPlayer) {
        final TreeMap<Float, BlockPos> map = new TreeMap<>();

        BlockUtil.getBlocksInRadius(targetRange.getValue()).stream().filter(pos -> BlockUtil.valid(pos, updated.getValue())).forEach(pos -> {
            AxisAlignedBB bb = mc.player.getEntityBoundingBox();
            Vec3d center = new Vec3d(bb.minX + (bb.maxX - bb.minX) / 2, bb.minY + (bb.maxY - bb.minY) / 2, bb.minZ + (bb.maxZ - bb.minZ) / 2);

            if (mc.world.rayTraceBlocks(center, new Vec3d(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5), false, true, false) != null) {
                if (mc.player.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > placeWallRange.getValue())
                    return;
            }
            if (Math.sqrt(mc.player.getDistanceSq(pos)) > placeRange.getValue()) {
                return;
            }
            if (!mc.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(new BlockPos(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5))).isEmpty()) {
                return;
            }
            final float selfDamage = BlockUtil.calculatePosDamage(pos, mc.player);
            if (selfDamage > maximumDamage.getValue()) {
                return;
            }
            final float enemyDamage = BlockUtil.calculatePosDamage(pos, entityPlayer);
            if (enemyDamage < minimumDamage.getValue()) {
                return;
            }
            final float damage = enemyDamage - selfDamage;
            if (selfDamage > mc.player.getHealth() + mc.player.getAbsorptionAmount()) {
                return;
            }
            map.put(damage, pos);
        });

        if (!map.isEmpty()) {
            return map.lastEntry().getValue();
        }

        return null;
    }

    private EntityPlayer target(final float range) {
        final HashMap<Float, List<EntityPlayer>> map = new HashMap<>();
        mc.world.playerEntities.stream().filter(e -> !e.equals(mc.player) && !e.isDead).forEach(entityPlayer -> {
            if (entityPlayer.getHealth() <= 0)
                return;
            final float distance = entityPlayer.getDistance(mc.player);
            if (distance < range && !GrassWare.friendManager.isFriend(entityPlayer.getName())) {
                map.computeIfAbsent(distance, k -> new ArrayList<>()).add(entityPlayer);
            }
        });
        if (!map.isEmpty()) {
            final float minDistance = Collections.min(map.keySet());
            return map.get(minDistance).get(0);
        }
        return null;} //zane like smile

    //Render Code
    @EventListener
    public void onRender3D(final Render3DEvent event) {
        BlockPos renderPos = (placedPos != null) ? placedPos : lastPos;                                                  // this for some reason makes it not render at all
        if (renderPos != null && mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL)) { //&& mc.player.getHeldItemMainhand().getItem().equals(Items.TOTEM_OF_UNDYING)
            float newOpacity = (placedPos != null) ? defualtOpacityVal.getValue() : MathUtil.lerp(opacity.getValue(), 0f, 0.05f);
            opacity.setValue(Math.max(newOpacity, 0));

            GradientShader.setup(opacity.getValue());
            RenderUtil.boxShader(renderPos);
            RenderUtil.outlineShader(renderPos);
            RenderUtil.outlineShader(renderPos); //idk maybe doubling this makes the outline have 2x the opacity?
            GradientShader.finish();

            if (placedPos != null) {
                lastPos = placedPos;
            }
        }
    }
    //ModuleList Code
    @Override
    public String getInfo() {
        if (placedPos != null) {
            return " [" + ChatFormatting.WHITE + "Active" + ChatFormatting.RESET + "]";
        } else {
            return " [" + ChatFormatting.WHITE + "Idle" + ChatFormatting.RESET + "]";
        }
    }
}
