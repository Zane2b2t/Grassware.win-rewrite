package me.zane.grassware.features.modules.combat;

import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.Render3DEvent;
import me.zane.grassware.event.events.TickEvent;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.FloatSetting;
import me.zane.grassware.features.setting.impl.ModeSetting;
import me.zane.grassware.shader.impl.GradientShader;
import me.zane.grassware.util.BlockUtil;
import me.zane.grassware.util.EntityUtil;
import me.zane.grassware.util.RenderUtil;
import me.zane.grassware.util.MC;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.TreeMap;

public class AutoCrystal extends Module {
    private final FloatSetting placeRange = register("Place Range", 5.0f, 1.0f, 6.0f);
    private final FloatSetting placeWallRange = register("Place Wall Range", 3.0f, 1.0f, 6.0f);
    private final FloatSetting breakRange = register("BreakRange", 5.0f, 1.0f, 6.0f);
    private final FloatSetting breakWallRange = register("Break Wall Range", 3.0f, 1.0f, 6.0f);
    private final FloatSetting targetRange = register("Target Range", 5.0f, 0.1f, 15.0f);
    private final FloatSetting minimumDamage = register("Minimum Damage", 6.0f, 0.1f, 12.0f);
    private final FloatSetting maximumDamage = register("Maximum Damage", 8.0f, 0.1f, 12.0f);
    private final FloatSetting delay = register("Delay", 50.0f, 0f, 500.0f);
    private final ModeSetting setDead = register("Set Dead", "Set Dead", Arrays.asList("None", "Set Dead", "Remove", "Both"));
    private final FloatSetting opacity = register("Opacity", 0.5f, 0.1f, 1.0f);
    private BlockPos placedPos;
    private long sys;

    @EventListener
    public void onTick(final TickEvent event) {
        final EntityPlayer entityPlayer = EntityUtil.entityPlayer(targetRange.getValue());
        if(entityPlayer == null){
            placedPos = null;
            return;
        }
        if (System.currentTimeMillis() - sys <= delay.getValue()) {
            return;
        }
        final EnumHand enumHand = MC.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem().equals(Items.END_CRYSTAL) ? EnumHand.MAIN_HAND : MC.mc.player.getHeldItem(EnumHand.OFF_HAND).getItem().equals(Items.END_CRYSTAL) ? EnumHand.OFF_HAND : null;
        if (enumHand != null) {
            final BlockPos pos = pos(entityPlayer);
            if (pos != null) {
                if (MC.mc.getConnection() != null) {
                    MC.mc.getConnection().getNetworkManager().channel().writeAndFlush(new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.UP, enumHand, 0.5f, 0.5f, 0.5f));
                }
                MC.mc.player.swingArm(enumHand);
                placedPos = pos;
                sys = System.currentTimeMillis();
            }
            if(pos == null)
                placedPos = null;
        }
        final EntityEnderCrystal entityEnderCrystal = crystal(entityPlayer);
        if (entityEnderCrystal != null) {
            if (MC.mc.getConnection() != null) {
                MC.mc.getConnection().getNetworkManager().channel().writeAndFlush(new CPacketUseEntity(entityEnderCrystal));
            }
            MC.mc.player.swingArm(EnumHand.OFF_HAND);
            handleSetDead(entityEnderCrystal);
            sys = System.currentTimeMillis();
        }
    }

    private void handleSetDead(EntityEnderCrystal crystal){
        if(setDead.getValue().equals("None"))
            return;
        if(setDead.getValue().equals("Set Dead") || setDead.getValue().equals("Both"))
            crystal.setDead();
        if(setDead.getValue().equals("Remove") || setDead.getValue().equals("Both"))
            mc.world.removeEntity(crystal);
    }

    @EventListener
    public void onRender3D(final Render3DEvent event) {
        if (placedPos != null) {
            GradientShader.setup(opacity.getValue());
            RenderUtil.boxShader(placedPos);
            RenderUtil.outlineShader(placedPos);
            GradientShader.finish();
        }
    }

    private EntityEnderCrystal crystal(final EntityPlayer entityPlayer) {
        final TreeMap<Float, EntityEnderCrystal> map = new TreeMap<>();

        MC.mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityEnderCrystal && !(MC.mc.player.getDistance(entity) > breakRange(entity))).map(entity -> (EntityEnderCrystal) entity).forEach(entityEnderCrystal -> {
            final float selfDamage = BlockUtil.calculateEntityDamage(entityEnderCrystal, MC.mc.player);
            if (selfDamage > maximumDamage.getValue()) {
                return;
            }
            final float enemyDamage = BlockUtil.calculateEntityDamage(entityEnderCrystal, entityPlayer);
            if (enemyDamage < minimumDamage.getValue()) {
                return;
            }
            final float damage = enemyDamage - selfDamage;
            if (selfDamage > MC.mc.player.getHealth() + MC.mc.player.getAbsorptionAmount()) {
                return;
            }
            map.put(damage, entityEnderCrystal);
        });

        if (!map.isEmpty()) {
            return map.lastEntry().getValue();
        }

        return null;
    }

    private float breakRange(Entity entity){
        if(mc.player.canEntityBeSeen(entity))
            return breakRange.getValue();
        return breakWallRange.getValue();
    }

    private BlockPos pos(final EntityPlayer entityPlayer) {
        final TreeMap<Float, BlockPos> map = new TreeMap<>();

        BlockUtil.getBlocksInRadius(targetRange.getValue()).stream().filter(BlockUtil::valid).forEach(pos -> {
            if(mc.world.rayTraceBlocks(mc.player.getPositionVector().add(0, mc.player.eyeHeight, 0), new Vec3d(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5), false, true, false) != null){
                if(mc.player.getDistance(pos.getX() + 0.5, pos.getY()+ 0.5, pos.getZ() + 0.5) > placeWallRange.getValue())
                    return;
            }
            if (Math.sqrt(MC.mc.player.getDistanceSq(pos)) > placeRange.getValue()) {
                return;
            }
            if (!MC.mc.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(new BlockPos(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5))).isEmpty()) {
                return;
            }
            final float selfDamage = BlockUtil.calculatePosDamage(pos, MC.mc.player);
            if (selfDamage > maximumDamage.getValue()) {
                return;
            }
            final float enemyDamage = BlockUtil.calculatePosDamage(pos, entityPlayer);
            if (enemyDamage < minimumDamage.getValue()) {
                return;
            }
            final float damage = enemyDamage - selfDamage;
            if (selfDamage > MC.mc.player.getHealth() + MC.mc.player.getAbsorptionAmount()) {
                return;
            }
            map.put(damage, pos);
        });

        if (!map.isEmpty()) {
            return map.lastEntry().getValue();
        }

        return null;
    }

}
