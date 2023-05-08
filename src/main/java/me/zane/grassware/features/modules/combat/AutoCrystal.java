package me.zane.grassware.features.modules.combat;

import me.zane.grassware.GrassWare;
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.PacketEvent;
import me.zane.grassware.event.events.Render3DEvent;
import me.zane.grassware.event.events.Render3DPreEvent;
import me.zane.grassware.event.events.UpdatePlayerWalkingEvent;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.features.setting.impl.FloatSetting;
import me.zane.grassware.features.setting.impl.ModeSetting;
import me.zane.grassware.shader.impl.GradientShader;
import me.zane.grassware.util.BlockUtil;
import me.zane.grassware.util.EntityUtil;
import me.zane.grassware.util.MC;
import me.zane.grassware.util.RenderUtil;
import me.zane.grassware.features.modules.client.ClickGui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class AutoCrystal extends Module {
    private final ModeSetting breakMode = register("Break Mode", "Sequential", Arrays.asList("Sequential", "Adaptive"));
    private final FloatSetting placeRange = register("Place Range", 5.0f, 1.0f, 6.0f);
    private final FloatSetting placeWallRange = register("Place Wall Range", 3.0f, 1.0f, 6.0f);
    private final FloatSetting breakRange = register("BreakRange", 5.0f, 1.0f, 6.0f);
    private final FloatSetting breakWallRange = register("Break Wall Range", 3.0f, 1.0f, 6.0f);
    private final FloatSetting targetRange = register("Target Range", 5.0f, 0.1f, 15.0f);
    private final FloatSetting minimumDamage = register("Minimum Damage", 6.0f, 0.1f, 12.0f);
    private final FloatSetting maximumDamage = register("Maximum Damage", 8.0f, 0.1f, 12.0f);
    private final FloatSetting placeDelay = register("Place Delay", 0.0f, 0f, 500.0f);
    private final FloatSetting breakDelay = register("Break Delay", 50.0f, 0f, 500.0f);
    private final ModeSetting setDead = register("Set Dead", "Set Dead", Arrays.asList("None", "Set Dead", "Remove", "Both"));
    private final BooleanSetting fastRemove = register("Fast Remove", false);
    private final FloatSetting opacity = register("Opacity", 0.5f, 0.1f, 1.0f);
    private final FloatSetting defualtOpacityVal = register("DOV", 0.5f, 0.1f, 1.0f);
    private final BooleanSetting renderRing = register("Ring", true);
    private final Map<Integer, Long> breakMap = new ConcurrentHashMap<>();
    private BlockPos placedPos;
    private BlockPos lastPos;
    private long placeTime;
    private long breakTime;
    private float i = 0.0f;

    @EventListener

    public void onUpdate(final UpdatePlayerWalkingEvent event) {
        final EntityPlayer entityPlayer = target(targetRange.getValue());
        if (entityPlayer == null) {
            placedPos = null;
            return;
        }

        if (System.currentTimeMillis() - placeTime > placeDelay.getValue()) {
            final EnumHand enumHand = MC.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem().equals(Items.END_CRYSTAL) ? EnumHand.MAIN_HAND : MC.mc.player.getHeldItem(EnumHand.OFF_HAND).getItem().equals(Items.END_CRYSTAL) ? EnumHand.OFF_HAND : null;
            if (enumHand != null) {
                final BlockPos pos = pos(entityPlayer);
                if (pos != null) {
                    if (MC.mc.getConnection() != null) {
                        MC.mc.getConnection().getNetworkManager().channel().writeAndFlush(new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.UP, enumHand, 0.5f, 0.5f, 0.5f));
                    }
                    MC.mc.player.swingArm(enumHand);
                    placedPos = pos;
                    placeTime = System.currentTimeMillis();
                }
                if (pos == null)
                    placedPos = null;
            }
        }

        if (System.currentTimeMillis() - breakTime > breakDelay.getValue()) {
            final EntityEnderCrystal entityEnderCrystal = crystal(entityPlayer);
            if (entityEnderCrystal != null) {
                if (MC.mc.getConnection() != null) {
                    MC.mc.getConnection().getNetworkManager().channel().writeAndFlush(new CPacketUseEntity(entityEnderCrystal));
                }
                MC.mc.player.swingArm(EnumHand.OFF_HAND);
                handleSetDead(entityEnderCrystal);
                breakTime = System.currentTimeMillis();
                try {
                    breakMap.put(entityEnderCrystal.getEntityId(), System.currentTimeMillis());
                } catch (Exception ignored) {
                }
            }
        }
    }

    @Override
    public void onDisable() {
    }

    @EventListener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSpawnObject && breakMode.getValue().equals("Adaptive")) {
            SPacketSpawnObject packet = event.getPacket();
            if (packet.getType() != 51 || !(mc.world.getEntityByID(packet.getEntityID()) instanceof EntityEnderCrystal))
                return;
            EntityEnderCrystal crystal = (EntityEnderCrystal) mc.world.getEntityByID(packet.getEntityID());
            if (crystal == null)
                return;
            final EntityPlayer entityPlayer = target(targetRange.getValue());
            if (entityPlayer == null)
                return;
            final float selfDamage = BlockUtil.calculateEntityDamage(crystal, MC.mc.player);
            if (selfDamage > maximumDamage.getValue()) {
                return;
            }
            final float enemyDamage = BlockUtil.calculateEntityDamage(crystal, entityPlayer);
            if (enemyDamage < minimumDamage.getValue()) {
                return;
            }
            if (selfDamage > MC.mc.player.getHealth() + MC.mc.player.getAbsorptionAmount()) {
                return;
            }
            if (MC.mc.getConnection() != null) {
                MC.mc.getConnection().getNetworkManager().channel().writeAndFlush(new CPacketUseEntity(crystal));
            }
            MC.mc.player.swingArm(EnumHand.OFF_HAND);
            handleSetDead(crystal);
            breakTime = System.currentTimeMillis();
            try {
                breakMap.put(crystal.getEntityId(), System.currentTimeMillis());
            } catch (Exception ignored) {
            }
        }
        if (event.getPacket() instanceof SPacketDestroyEntities) {
            SPacketDestroyEntities packet = event.getPacket();
            for (int id : packet.getEntityIDs()) {
                try {
                    if (breakMap.containsKey(id) && breakMap.get(id) > 1500) {
                        breakMap.remove(id);
                        continue;
                    }
                    if (!fastRemove.getValue()) continue;
                    if (!breakMap.containsKey(id)) continue;
                    mc.world.removeEntityFromWorld(id);
                } catch (Exception ignored) {
                }
            }
        }
    }

    private void handleSetDead(EntityEnderCrystal crystal) {
        if (setDead.getValue().equals("None"))
            return;
        if (setDead.getValue().equals("Set Dead") || setDead.getValue().equals("Both"))
            crystal.setDead();
        if (setDead.getValue().equals("Remove") || setDead.getValue().equals("Both"))
            mc.world.removeEntity(crystal);
    }
    @EventListener
    public void onRender3DPre(final Render3DPreEvent event) {

        final EntityPlayer entityPlayer = EntityUtil.entityPlayer(5.0f);
        if (entityPlayer == null || !mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL) && !mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) && renderRing.getValue()) {
            return;
        }

        final Vec3d vec = RenderUtil.interpolateEntity(entityPlayer);
        final Color color = ClickGui.Instance.getGradient()[0];
        final Color color2 = ClickGui.Instance.getGradient()[1];
        final Color top = new Color(color2.getRed(), color2.getGreen(), color2.getBlue(), 0);
        final float sin = ((float) Math.sin(i / 25.0f) / 2.0f);
        i++;
        glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        glShadeModel(GL_SMOOTH);
        glDisable(GL_CULL_FACE);
        glBegin(GL_QUAD_STRIP);

        for (int i = 0; i <= 360; i++) {
            final double x = ((Math.cos(i * Math.PI / 180F) * entityPlayer.width) + vec.x);
            final double y = (vec.y + (entityPlayer.height / 2.0f));
            final double z = ((Math.sin(i * Math.PI / 180F) * entityPlayer.width) + vec.z);
            RenderUtil.glColor(color);
            glVertex3d(x, y + (sin * entityPlayer.height), z);
            RenderUtil.glColor(top);
            glVertex3d(x, y + (sin * entityPlayer.height / 2.0f), z);
        }

        glEnd();
        glEnable(GL_CULL_FACE);
        glShadeModel(GL_FLAT);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glPopMatrix();
    }


@EventListener
    public void onRender3D(final Render3DEvent event) {
        if (placedPos != null) {
            opacity.setValue(defualtOpacityVal.getValue()); //makes it so that the opacity doesnt stay at 0 after fading even after the box renders again
          //  glAlphaFunc(GL_GREATER, 0.001f);
            GradientShader.setup(opacity.getValue());
            RenderUtil.boxShader(placedPos);
            RenderUtil.outlineShader(placedPos);
            GradientShader.finish();
            lastPos = placedPos;
        } else {
            if (opacity.getValue() > 0) {
                opacity.setValue(opacity.getValue() - 0.01f);   // gradually decreases opacity by 0.01 every ms, only when there's no where to place anymore
                GradientShader.setup(opacity.getValue());
                RenderUtil.boxShader(lastPos);
                RenderUtil.outlineShader(lastPos);
                GradientShader.finish();
            }
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

    private float breakRange(Entity entity) {
        if (mc.player.canEntityBeSeen(entity))
            return breakRange.getValue();
        return breakWallRange.getValue();
    }

    private BlockPos pos(final EntityPlayer entityPlayer) {
        final TreeMap<Float, BlockPos> map = new TreeMap<>();

        BlockUtil.getBlocksInRadius(targetRange.getValue()).stream().filter(BlockUtil::valid).forEach(pos -> {
            if (mc.world.rayTraceBlocks(mc.player.getPositionVector().add(0, mc.player.eyeHeight, 0), new Vec3d(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5), false, true, false) != null) {
                if (mc.player.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > placeWallRange.getValue())
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

    private EntityPlayer target(final float range) {
        final TreeMap<Float, EntityPlayer> map = new TreeMap<>();
        mc.world.playerEntities.stream().filter(e -> !e.equals(mc.player) && !e.isDead).forEach(entityPlayer -> {
            if (entityPlayer.getHealth() <= 0)
                return;
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
}

