package me.zane.grassware.features.modules.combat;
// this is turning into a skidhole AAA
import me.zane.grassware.GrassWare;
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.MotionUpdateEvent;
import me.zane.grassware.event.events.Render3DEvent;
import me.zane.grassware.event.events.PacketEvent;
import me.zane.grassware.event.events.WebExplosionEvent;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.*;
import me.zane.grassware.mixin.mixins.ICPacketUseEntity;
import me.zane.grassware.shader.impl.GradientShader;
import me.zane.grassware.util.*;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class asd extends Module {
    private final IntSetting placeInterval = register("Place Interval", 50, 0, 500);
    private final IntSetting breakInterval = register("Break Interval", 50, 0, 500);
    private final FloatSetting defualtOpacityVal = register("DOV", 0.5f, 0.1f, 1.0f);
    private final BooleanSetting simultaneously = register("Simultaneously", false);

    private final ModeSetting packet = register("Packet", "Both", Arrays.asList("Place", "Break", "Both", "None"));
    private final ModeSetting placements = register("Placements", "1.12.2", Arrays.asList("1.12.2", "1.13+"));
    private final BooleanSetting limit = register("Limit", false);
    private final IntSetting limitTimeout = register("Limit Timeout", 100, 0, 500);
    private final BooleanSetting silentSwap = register("Silent Swap", false);
    private final BooleanSetting boost = register("Boost", false);
    private final BooleanSetting damageTick = register("Damage Tick", false);
    private final BooleanSetting autoSwitch = register("Auto Switch", false);
    private final BooleanSetting constBypass = register("Const Bypass", false);
    private final BooleanSetting superTrace = register("Super Trace", false);
    private final BooleanSetting raytraceBypass = register("Raytrace Bypass", false);
    private final IntSetting wait = register("Wait", 1, 1, 20).invokeVisibility(z -> raytraceBypass.getValue());
    private final IntSetting timeout = register("Timeout", 1, 1, 20).invokeVisibility(z -> raytraceBypass.getValue());

    private final ModeSetting calculations = register("Calculations", "Damage", Arrays.asList("Damage", "Net"));
    private final BooleanSetting smartCalculations = register("Smart Calculations", true);
    private final BooleanSetting removeDesync = register("Remove Desync", true);
    private final FloatSetting minimumDamage = register("Minimum Damage", 6.0f, 0.1f, 20.0f);
    private final FloatSetting maximumSelfDamage = register("Maximum Self Damage", 8.0f, 0.1f, 20.0f);
    private final BooleanSetting antiSuicide = register("Anti Suicide", true);
    private final FloatSetting antiSuicideSafety = register("Anti Suicide Safety", 2.0f, 0.0f, 15.0f).invokeVisibility(z -> antiSuicide.getValue());
    private final IntSetting ticksExisted = register("Ticks Existed", 0, 0, 20);

    private final FloatSetting targetRange = register("Target Range", 10.0f, 0.1f, 15.0f);
    private final FloatSetting scanRange = register("Scan Range", 5.0f, 0.1f, 6.0f);
    private final FloatSetting placeRange = register("Place Range", 5.0f, 0.1f, 6.0f);
    private final FloatSetting breakRange = register("Break Range", 5.0f, 0.1f, 6.0f);
    private final BooleanSetting strictTrace = register("Strict Trace", false);
    private final FloatSetting placeWallRange = register("Place Wall Range", 5.0f, 0.1f, 6.0f).invokeVisibility(z -> !strictTrace.getValue());
    private final FloatSetting breakWallRange = register("Break Wall Range", 5.0f, 0.1f, 6.0f).invokeVisibility(z -> !strictTrace.getValue());

    private final BooleanSetting predictMotion = register("Predict Motion", false);
    private final IntSetting predictMotionFactor = register("Predict Motion", 2, 1, 20).invokeVisibility(z -> predictMotion.getValue());
    private final BooleanSetting predictMotionVisualize = register("Visualize", false).invokeVisibility(z -> predictMotion.getValue());

    private final FloatSetting opacity = register("Opacity", 150.0f, 0.0f, 255.0f);

    private boolean calculating;
    private long placeTime, breakTime;
    private BlockPos pos;
    private int ticks, shiftTicks;
    public static long time;
    private EntityOtherPlayerMP entityOtherPlayerMP;
    private final HashMap<EntityEnderCrystal, Long> limitCrystals = new HashMap<>();
    private float[] spoofed = new float[]{0.0f, 0.0f};


    @Override
    public void onEnable() {
        if (autoSwitch.getValue()) {
            EntityPlayer entityPlayer = EntityUtil.getEntityPlayer(targetRange.getValue());
            if (entityPlayer != null) {
                int slot = InventoryUtil2.getItemFromHotbar(Items.END_CRYSTAL);
                if (slot != -1) {
                    mc.player.inventory.currentItem = slot;
                }
            }
        }
    }

    @EventListener
    public void onMotion(MotionUpdateEvent event) {
        if (limit.getValue()) {
            for (Map.Entry<EntityEnderCrystal, Long> c : new HashMap<>(limitCrystals).entrySet()) {
                if (System.currentTimeMillis() - c.getValue() > limitTimeout.getValue()) {
                    limitCrystals.remove(c.getKey());
                }
            }
        }


        boolean active = false;
        EntityPlayer entityPlayer = EntityUtil.getEntityPlayer(targetRange.getValue());
        if (entityPlayer != null) {

            invokeInfo(entityPlayer.getName());

            if (attemptRaytraceBypass()) {
                event.setPitch(-90.0f);
                return;
            }

            if (constBypass.getValue() && mc.currentScreen == null) {
                PacketUtil.invoke(new CPacketCloseWindow());
            }

            double[] position = new double[]{entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ};
            Vec3d vec = GrassWare.motionPredictManager.getPredictedPosByPlayer(entityPlayer, predictMotionFactor.getValue());
            if (predictMotion.getValue()) {
                entityPlayer.setPosition(vec.x, vec.y, vec.z);
                if (predictMotionVisualize.getValue()) {
                    entityOtherPlayerMP = EntityUtil.setupEntity(entityPlayer, vec);
                } else {
                    entityOtherPlayerMP = null;
                }
            } else {
                entityOtherPlayerMP = null;
            }

            calculating = true;
            EntityEnderCrystal crystal = crystal(entityPlayer);
            BlockPos pos = findPos(entityPlayer);
            calculating = false;


            long sys = System.currentTimeMillis();
            if (sys - placeTime > placeInterval.getValue() && pos != null) {
                active = true;
                placeCrystal(pos, event);
                placeTime = sys;
            }

            if (!active || simultaneously.getValue()) {
                if (sys - breakTime > breakInterval.getValue() && crystal != null) {
                    active = true;
                    breakCrystal(crystal, event);
                    breakTime = sys;
                }
            }

            this.pos = crystal != null ? crystal.getPosition().down() : pos;

            entityPlayer.setPosition(position[0], position[1], position[2]);

        } else {
            entityOtherPlayerMP = null;
            pos = null;
            invokeInfo("");
        }
        if (active) {
            time = System.currentTimeMillis();
        }
    }

                @EventListener
                public void onRender3D(final Render3DEvent event) {
                    if (pos != null) {
                        opacity.setValue(defualtOpacityVal.getValue());
                        GradientShader.setup(opacity.getValue());
                        RenderUtil.boxShader(pos);
                        RenderUtil.outlineShader(pos);
                        GradientShader.finish();
                    }
                    else {
                        GradientShader.setup(opacity.getValue());
                        RenderUtil.boxShader(pos);
                        RenderUtil.outlineShader(pos);
                    }
                }



    @EventListener
    public void onWebExplosion(WebExplosionEvent event) {
        if (calculating) {
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (removeDesync.getValue() && event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                for (Entity entity : new ArrayList<>(mc.world.loadedEntityList)) {
                    if (entity instanceof EntityEnderCrystal) {
                        if (Math.sqrt(entity.getDistanceSq(packet.getX(), packet.getY(), packet.getZ())) <= 5.0f) {
                            entity.setDead();
                        }
                    }
                }
            }
        }
        if (boost.getValue() && event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock) event.getPacket();

            if (!BlockUtil.valid(packet.getPos(), placements.getValue().equals("1.13+"))) {
                return;
            }

            Entity highestEntity = null;
            int entityId = 0;
            for (Entity entity : mc.world.loadedEntityList) {
                if (entity instanceof EntityEnderCrystal) {
                    if (entity.getEntityId() > entityId) {
                        entityId = entity.getEntityId();
                    }
                    highestEntity = entity;
                }
            }

            if (highestEntity != null) {
                int latency = Objects.requireNonNull(mc.getConnection()).getPlayerInfo(mc.getConnection().getGameProfile().getId()).getResponseTime() / 50;
                for (int i = latency; i < latency + 10; i++) {
                    try {
                        CPacketUseEntity cPacketUseEntity = new CPacketUseEntity();

                        ((ICPacketUseEntity) cPacketUseEntity).setEntityId(highestEntity.getEntityId() + i);
                        ((ICPacketUseEntity) cPacketUseEntity).setAction(CPacketUseEntity.Action.ATTACK);
                        PacketUtil.invoke(cPacketUseEntity);
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    private void breakCrystal(EntityEnderCrystal entity, MotionUpdateEvent event) {
        EnumHand enumHand =
                mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem().equals(Items.END_CRYSTAL) ? EnumHand.MAIN_HAND
                        : mc.player.getHeldItem(EnumHand.OFF_HAND).getItem().equals(Items.END_CRYSTAL) ? EnumHand.OFF_HAND
                        : EnumHand.MAIN_HAND;

        int handleWeakness = handleWeakness();
        if (packet.getValue().equals("Both") || packet.getValue().equals("Break")) {
            PacketUtil.invoke(new CPacketUseEntity(entity));
        } else {
            mc.playerController.attackEntity(mc.player, entity);
        }
        if (handleWeakness != -1) {
            InventoryUtil2.switchBack(handleWeakness);
        }

        if (limit.getValue()) {
            limitCrystals.put(entity, System.currentTimeMillis());
        }
        mc.player.swingArm(enumHand);
    }

    private void placeCrystal(BlockPos pos, MotionUpdateEvent event) {
        EnumHand enumHand =
                mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem().equals(Items.END_CRYSTAL) ? EnumHand.MAIN_HAND
                        : mc.player.getHeldItem(EnumHand.OFF_HAND).getItem().equals(Items.END_CRYSTAL) ? EnumHand.OFF_HAND
                        : EnumHand.MAIN_HAND;


        int slot = !silentSwap.getValue() ? -1 : InventoryUtil2.getItemFromHotbar(Items.END_CRYSTAL);

        EnumFacing enumFacing = EnumFacing.UP;
        if (packet.getValue().equals("Both") || packet.getValue().equals("Place")) {
            if (superTrace.getValue()) {
                Vec3d vec = RaytraceUtil.getRaytraceSides(pos);
                AxisAlignedBB bb = new AxisAlignedBB(pos);
                PacketUtil.invoke(new CPacketPlayerTryUseItemOnBlock(pos, enumFacing, enumHand, (float) (vec == null ? 0.5f : (vec.x - bb.minX)), (float) (vec == null ? 0.5f : (vec.y - bb.minY)), (float) (vec == null ? 0.5f : (vec.z - bb.minZ))));
                mc.player.swingArm(enumHand);

            } else {
                PacketUtil.invoke(new CPacketPlayerTryUseItemOnBlock(pos, enumFacing, enumHand, enumFacing.getDirectionVec().getX(), enumFacing.getDirectionVec().getY(), enumFacing.getDirectionVec().getZ()), slot);
                mc.player.swingArm(enumHand);
            }
        } else {
            int currentItem = mc.player.inventory.currentItem;
            if (slot != -1) {
                InventoryUtil2.switchToSlot(slot);
            }
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, enumFacing, new Vec3d(mc.player.posX, -mc.player.posY, -mc.player.posZ), enumHand);
            if (slot != -1) {
                InventoryUtil2.switchBack(currentItem);
            }
            mc.player.swingArm(enumHand);
        }


    }

    private EntityEnderCrystal crystal(EntityPlayer entityPlayer) {
        final TreeMap<Float, EntityEnderCrystal> posses = new TreeMap<>();
        boolean resistant = damageTick.getValue() && entityPlayer.hurtResistantTime > entityPlayer.maxHurtResistantTime / 2.0f;
        for (Entity entity : mc.world.loadedEntityList) {
            if (!(entity instanceof EntityEnderCrystal)) {
                continue;
            }
            if (entity.ticksExisted < ticksExisted.getValue()) {
                continue;
            }
            boolean raytrace = RaytraceUtil.raytrace(entity);
            float range = raytrace ? breakRange.getValue() : (strictTrace.getValue() ? 0.0f : breakWallRange.getValue());
            if (mc.player.getDistance(entity) > range) {
                continue;
            }
            float selfDamage = BlockUtil.calculateEntityDamage((EntityEnderCrystal) entity, mc.player);
            float damage = BlockUtil.calculateEntityDamage((EntityEnderCrystal) entity, entityPlayer);

            if (smartCalculations.getValue() && damage - selfDamage < 0) {
                continue;
            }
            if (selfDamage > maximumSelfDamage.getValue()) {
                continue;
            }
            if (antiSuicide.getValue() && selfDamage > mc.player.getHealth() + mc.player.getAbsorptionAmount() - antiSuicideSafety.getValue()) {
                continue;
            }
            if (damage < Math.min(EntityUtil.getHealth(entityPlayer), minimumDamage.getValue())) {
                continue;
            }
            if (limit.getValue() && limitCrystals.containsKey(entity)) {
                continue;
            }

            posses.put(calculations.getValue().equals("Damage") ? damage : damage - selfDamage, (EntityEnderCrystal) entity);
        }
        if (!posses.isEmpty()) {
            return posses.lastEntry().getValue();
        }
        return null;
    }

    private BlockPos findPos(EntityPlayer entityPlayer) {
        final TreeMap<Float, BlockPos> posses = new TreeMap<>();
        boolean resistant = damageTick.getValue() && entityPlayer.hurtResistantTime > (float) entityPlayer.maxHurtResistantTime / 2.0f;
        float lastDamage = GrassWare.lastDamageManager.getLastDamage(entityPlayer);
        for (BlockPos pos : BlockUtil.getBlocksInRadius(scanRange.getValue())) {
            if (!BlockUtil.valid(pos, placements.getValue().equals("1.13+"))) {
                continue;
            }
            if (!BlockUtil.empty(pos)) {
                continue;
            }
            float selfDamage = BlockUtil.calculatePosDamage(pos, mc.player);
            float damage = BlockUtil.calculatePosDamage(pos, entityPlayer);
            if (resistant && damage <= lastDamage) {
                continue;
            }
            if (smartCalculations.getValue() && damage - selfDamage < 0) {
                continue;
            }
            if (selfDamage > maximumSelfDamage.getValue()) {
                continue;
            }
            if (antiSuicide.getValue() && selfDamage > mc.player.getHealth() + mc.player.getAbsorptionAmount() - antiSuicideSafety.getValue()) {
                continue;
            }
            if (damage < Math.min(EntityUtil.getHealth(entityPlayer), minimumDamage.getValue())) {
                continue;
            }

            boolean raytrace = RaytraceUtil.hasVisibleVec(pos);
            float range = raytrace ? placeRange.getValue() : (strictTrace.getValue() ? 0.0f : placeWallRange.getValue());
            if (Math.sqrt(mc.player.getDistanceSq(BlockUtil.center(pos))) > range) {
                continue;
            }

            posses.put(calculations.getValue().equals("Damage") ? damage : damage - selfDamage, pos);
        }
        if (!posses.isEmpty()) {
            return posses.lastEntry().getValue();
        }
        return null;
    }

    private int handleWeakness() {
        int currentItem = -1;
        PotionEffect weakness = mc.player.getActivePotionEffect(MobEffects.WEAKNESS);
        if (weakness != null && !mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_SWORD)) {
            int swordSlot = InventoryUtil2.getItemFromHotbar(Items.DIAMOND_SWORD);
            if (swordSlot != -1) {
                currentItem = mc.player.inventory.currentItem;
                InventoryUtil2.switchToSlot(swordSlot);
            }
        }
        return currentItem;
    }

    private boolean attemptRaytraceBypass() {
        if (raytraceBypass.getValue()) {
            if (ticks > 0) {
                shiftTicks = timeout.getValue();
                ticks--;
            } else {
                if (shiftTicks > 0) {
                    shiftTicks--;
                    return true;
                } else {
                    ticks = wait.getValue();
                }
            }
        }
        return false;
    }
}
