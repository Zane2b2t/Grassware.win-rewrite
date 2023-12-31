package me.zane.grassware.features.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.zane.grassware.GrassWare;
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.PacketEvent;
import me.zane.grassware.event.events.Render3DEvent;
import me.zane.grassware.event.events.UpdatePlayerWalkingEvent;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.features.setting.impl.FloatSetting;
import me.zane.grassware.features.setting.impl.ModeSetting;
import me.zane.grassware.shader.impl.GradientShader;
import me.zane.grassware.util.BlockUtil;
import me.zane.grassware.util.EntityUtil;
import me.zane.grassware.util.MathUtil;
import me.zane.grassware.util.RenderUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static net.minecraft.network.play.client.CPacketUseEntity.Action.ATTACK;

/**
 * @author zane4417/zaneATAT
 * @since 8/8/23
 */
public class AutoCrystalRewrite extends Module {
    private final ModeSetting page = register("Page", "Calculations", Arrays.asList("Calculations", "Place", "Break", "Render", "Misc")); //China way of adding pages but idk how to make pages using Enums

    //Calculations Page
    private final BooleanSetting updated = register("1.13+", true).invokeVisibility(z -> page.getValue().equals("Calculations"));
    private final FloatSetting targetRange = register("TargetRange", 10.0f, 1.0f, 12.0f).invokeVisibility(z -> page.getValue().equals("Calculations"));
    private final FloatSetting maximumDamage = register("MaxSelf", 6.0f, 1.0f, 12.0f).invokeVisibility(z -> page.getValue().equals("Calculations"));
    private final FloatSetting minimumDamage = register("MinDamage", 4.0f, 1.0f, 16.0f).invokeVisibility(z -> page.getValue().equals("Calculations"));

    //Place Page
    private final FloatSetting placeDelay = register("PlaceDelay", 50.0f, 0.0f, 200.0f).invokeVisibility(z -> page.getValue().equals("Place"));
    private final FloatSetting placeRange = register("PlaceRange", 4.5f, 1.0f, 6.0f).invokeVisibility(z -> page.getValue().equals("Place"));
    private final FloatSetting placeWallRange = register("PlaceWall", 4.5f, 1.0f, 6.0f).invokeVisibility(z -> page.getValue().equals("Place"));
    private final BooleanSetting placeEfficient = register("PlaceEfficient", true).invokeVisibility(z -> page.getValue().equals("Place"));

    //Break Page
    private final FloatSetting breakDelay = register("BreakDelay", 50f, 0f, 200f).invokeVisibility(z -> page.getValue().equals("Break"));
    private final ModeSetting setDead = register("SetDead", "Both", Arrays.asList("SetDead", "Remove", "Both")).invokeVisibility(z -> page.getValue().equals("Break"));

    //Break Page
    private final FloatSetting breakRange = register("BreakRange", 4.5f, 1.0f, 6.0f).invokeVisibility(z -> page.getValue().equals("Break"));
    private final FloatSetting breakWallRange = register("BreakTrace", 4.5f, 1.0f, 6.0f).invokeVisibility(z -> page.getValue().equals("Break"));
    private final BooleanSetting inhibit = register("Inhibit", false).invokeVisibility(z -> page.getValue().equals("Break"));
    private final BooleanSetting predict = register("Predict", false).invokeVisibility(z -> page.getValue().equals("Break"));
    private final BooleanSetting await = register("Await", false).invokeVisibility(z -> page.getValue().equals("Break"));
    private final BooleanSetting soundRemove = register("SoundRemove", false).invokeVisibility(z -> page.getValue().equals("Break"));
    private final BooleanSetting fastRemove = register("FastRemove", false).invokeVisibility(z -> page.getValue().equals("Break"));
    private final BooleanSetting breakEfficient = register("BreakEfficient", true).invokeVisibility(z -> page.getValue().equals("Break"));

    //Render Page
    private final FloatSetting opacity = register("Opacity", 0.5f, 0.0f, 1.0f).invokeVisibility(z -> page.getValue().equals("Render"));
    private final FloatSetting defualtOpacityVal = register("DOV", 0.5f, 0.0f, 1.0f).invokeVisibility(z -> page.getValue().equals("Render"));

    //Misc Page
    private final ModeSetting mode = register("Mode", "Sequential", Arrays.asList("Sequential", "Adaptive")).invokeVisibility(z -> page.getValue().equals("Misc"));
    private final ModeSetting syncMode = register("SynMode", "instant", Arrays.asList("Instant", "Sound")).invokeVisibility(z -> page.getValue().equals("Misc"));
    private final ModeSetting logic = register("Logic", "BreakPlace", Arrays.asList("BreakPlace", "PlaceBreak")).invokeVisibility(z -> page.getValue().equals("Misc"));
    private final BooleanSetting bongo = register("Bongo", false).invokeVisibility(z -> page.getValue().equals("Misc"));
    private final BooleanSetting blyat = register("Blayt", false);

    //The stuff
    private BlockPos placedPos;
    private BlockPos lastPos;
    private BlockPos currentPos;
    private EntityPlayer targetPlayer;
    ArrayList<EntityEnderCrystal> crystals = new ArrayList<>();
    private final Map<Integer, Long> breakMap = new ConcurrentHashMap<>();
    private EnumHand enumHand;
    private boolean hasPlaced = false;
    private long placeTime;
    private long breakTime = 0;

    @EventListener
    public void onUpdate(final UpdatePlayerWalkingEvent event) {
        final EntityPlayer entityPlayer = target(targetRange.getValue());
        if (entityPlayer == null) {
            placedPos = null;
        } else {
            final BlockPos pos = pos(entityPlayer);
            currentPos = pos;
            targetPlayer = entityPlayer;
            switch (logic.getValue()) {
                case "PlaceBreak":
                    placeCrystal(pos);
                    breakCrystal(entityPlayer);
                    break;
                case "BreakPlace":
                    breakCrystal(entityPlayer);
                    placeCrystal(pos);
                    break;
            }
        }
    }
    @Override
    public void onEnable() {
        swingHand();
    }


    //Place Code
    public void placeCrystal(BlockPos pos) {
        if (pos == null) {
            placedPos = null;
            return;
        }
        if (System.currentTimeMillis() - placeTime > placeDelay.getValue()) {
            if (enumHand != null) {
                (mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.UP, enumHand, 0.5f, 0.5f, 0.5f));
                swingHand();
            }
            placedPos = pos;
            placeTime = System.currentTimeMillis();
            if (placedPos != null && bongo.getValue()) {
                (mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(placedPos, EnumFacing.UP, enumHand, 0.5f, 0.5f, 0.5f));
            }
        }
    }


    //Break Code
    private void breakCrystal(EntityPlayer entityPlayer) {
        final EntityEnderCrystal entityEnderCrystal = crystal(entityPlayer);
        if (entityEnderCrystal == null) {
            return;
        }
        if (await.getValue()) {
            if (!hasPlaced) {// If Await is enabled, the method only executes if we placed a crysta
                return;
            }
        }
        final boolean isCrystalNotListed = !inhibit.getValue() || !crystals.contains(entityEnderCrystal);
        if (System.currentTimeMillis() - breakTime > breakDelay.getValue() && isCrystalNotListed) {
            CPacketUseEntity crystal = new CPacketUseEntity();
            crystal.entityId = entityEnderCrystal.entityId;
            crystal.action = ATTACK;
            mc.player.connection.sendPacket(crystal);
            crystals.add(entityEnderCrystal);
            handleSetDead(entityEnderCrystal);
            handleFastRemove(entityEnderCrystal);

            breakTime = System.currentTimeMillis();
            try {
                breakMap.put(entityEnderCrystal.getEntityId(), System.currentTimeMillis());
            } catch (Exception ignored) {
            }
        }

    }
//Basically gets the entityId of the SpawnedObject from the Packet and attacks that ID so we attack instanly
    @EventListener
    public void onPredict(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSpawnObject && this.predict.getValue()) {
            SPacketSpawnObject packet = event.getPacket();
            if (packet.getType() != 51) {
                return;
            }
            EntityEnderCrystal crystal = new EntityEnderCrystal(AutoCrystal.mc.world, packet.getX(), packet.getY(), packet.getZ());
            CPacketUseEntity crystalPacket = new CPacketUseEntity();
            crystalPacket.entityId = packet.getEntityID();
            crystalPacket.action = ATTACK;
            handleSetDead(crystal); //should those be moved to 131?
            handleFastRemove(crystal);
          //  if (breakMop.getValue()) {
                breakMap.put(packet.getEntityID(), breakMap.containsKey(packet.getEntityID()) ? breakMap.get(packet.getEntityID()) + 1 : 1);
          //  }
            AutoCrystal.mc.player.connection.sendPacket(crystalPacket);
             crystals.add(crystal);
        }


    }
    //no explaination needed
    private float breakRange(Entity entity) {
        if (mc.player.canEntityBeSeen(entity))
            return breakRange.getValue();
        return breakWallRange.getValue();
    }

//Handlers Code
    private void handleSetDead(EntityEnderCrystal crystal) {
        (mc.getConnection()).sendPacket(new CPacketUseEntity(crystal));
        if (setDead.getValue().equals("Set Dead") || setDead.getValue().equals("Both"))
            crystal.setDead();
        if (setDead.getValue().equals("Remove") || setDead.getValue().equals("Both"))
            mc.world.removeEntity(crystal);
    }
    private void handleFastRemove(EntityEnderCrystal crystal) {
        if (fastRemove.getValue()) {
            mc.addScheduledTask(() -> {
                mc.world.removeEntity(crystal);
                mc.world.removeEntityDangerously(crystal);
            }); // sad );
        }
    }
    private void handleCPacketPlayerTryUseItemOnBlock(PacketEvent.Send event) {
        if (mode.getValue().equals("Adaptive")) {
            CPacketPlayerTryUseItemOnBlock placePacket = event.getPacket();
            if (placePacket.position.equals(currentPos) && getCrystal(currentPos) != null) {
                breakCrystal(targetPlayer);
            }
        }
    }

    private void handleCPacketUseEntity(PacketEvent.Send event) {
        if (mode.getValue().equals("Adaptive")) {
            CPacketUseEntity breakPacket = event.getPacket();
            if (breakPacket.getEntityFromWorld(mc.world) instanceof EntityEnderCrystal && breakPacket.action.equals(ATTACK)) {
                placeCrystal(currentPos);
            }

        }
    }

    //Misc Code
    public void swingHand() {
        if (mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL)) {
            mc.player.swingArm(EnumHand.MAIN_HAND);
            enumHand = EnumHand.MAIN_HAND;
        } else if (mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL)) {
            mc.player.swingArm(EnumHand.OFF_HAND);
            enumHand = EnumHand.OFF_HAND;
        }
    }
//this too much
@EventListener
public void onPacketReceive(PacketEvent.Receive event) {
    if (event.getPacket() instanceof SPacketSpawnObject && mode.getValue().equals("Adaptive")) {
        SPacketSpawnObject packet = event.getPacket();
        if (packet.getType() != 51 || !(mc.world.getEntityByID(packet.getEntityID()) instanceof EntityEnderCrystal))
            return;
        EntityEnderCrystal crystal = (EntityEnderCrystal) mc.world.getEntityByID(packet.getEntityID());
        if (crystal == null)
            return;
        final EntityPlayer entityPlayer = target(targetRange.getValue());
        if (entityPlayer == null)
            return;
        final float selfDamage = BlockUtil.calculateEntityDamage(crystal, mc.player);
        if (selfDamage > maximumDamage.getValue()) {
            return;
        }
        final float enemyDamage = BlockUtil.calculateEntityDamage(crystal, entityPlayer);
        if (enemyDamage < minimumDamage.getValue()) {
            return;
        }

        if (selfDamage > mc.player.getHealth() + mc.player.getAbsorptionAmount()) {
            return;
        }
        (mc.getConnection()).sendPacket(new CPacketUseEntity(crystal));
        if (predict.getValue()) {
            CPacketUseEntity packetUseEntity = new CPacketUseEntity();
            packetUseEntity.entityId = packet.getEntityID();
            packetUseEntity.action = ATTACK;
        }
        swingHand();
        handleSetDead(crystal);
        handleFastRemove(crystal);
        breakTime = System.currentTimeMillis();
        try {
            breakMap.put(crystal.getEntityId(), System.currentTimeMillis());
        } catch (Exception ignored) {
        }
        if (mode.getValue().equals("Sequential") && lastPos != null && lastPos == placedPos || (mode.getValue().equals("Sequential")) && lastPos != null && lastPos == currentPos) {
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(lastPos, EnumFacing.UP, enumHand, 0.5f, 0.5f, 0.5f));
        }
    }
    if (event.getPacket() instanceof SPacketDestroyEntities) {
        SPacketDestroyEntities packet = event.getPacket();
        for (int id : packet.getEntityIDs()) {
            try {
                if (breakMap.containsKey(id) || breakMap.containsKey(packet.getEntityIDs()) && breakMap.get(id) > 1500) {
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
            if (event.getPacket() instanceof SPacketSoundEffect && soundRemove.getValue()) {
                final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
                if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                    mc.addScheduledTask(() -> {
                        for (Entity entity : mc.world.loadedEntityList) {
                            if (entity instanceof EntityEnderCrystal && entity.getDistanceSq(packet.getX(), packet.getY(), packet.getZ()) < 36) {
                                entity.setDead();
                                if (setDead.getValue().equals("Both")) {
                                    mc.world.removeEntity(entity);
                                    mc.world.removeEntityDangerously(entity);
                                }
                            }
                        }
                    });
                }
            }
        }
    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        CPacketUseEntity packet;
        if (event.getPacket() instanceof CPacketUseEntity && this.syncMode.getValue().equals("Instant") && (packet = event.getPacket()).getEntityFromWorld(AutoCrystal.mc.world) instanceof EntityEnderCrystal) {
            (packet.getEntityFromWorld(AutoCrystal.mc.world)).setDead();
            AutoCrystal.mc.world.removeEntityFromWorld(packet.entityId);
        }
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            handleCPacketPlayerTryUseItemOnBlock(event);
        } else if (event.getPacket() instanceof CPacketUseEntity) {
            handleCPacketUseEntity(event);
        }
    }
     //TODO: Expand this when we make breakCrystal

    //Calc Code
    public EntityEnderCrystal getCrystal(BlockPos pos) {
        if (mc.player == null || mc.world == null) {
            return null;
        }
        java.util.List<Entity> entities = mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos)); // Get all entities within the specified block position
        for (Entity entity : entities) { // Iterate through the list of entities
            if (entity instanceof EntityEnderCrystal) { // Check if the entity is an instance of EntityEnderCrysta
                return (EntityEnderCrystal) entity;
            }
        }
        return null;
    }
// istg im not spending all that time commenting again. this time its much more simple
    private EntityEnderCrystal crystal(final EntityPlayer entityPlayer) {
        final TreeMap<Float, EntityEnderCrystal> map = new TreeMap<>(); // Create a TreeMap to store the damage and crystal entity
// Filter all loaded entities to get only Ender Crystals within the BreakRange
        mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityEnderCrystal && !(mc.player.getDistance(entity) > breakRange(entity))).map(entity -> (EntityEnderCrystal) entity).forEach(entityEnderCrystal -> {
            final float selfDamage = BlockUtil.calculateEntityDamage(entityEnderCrystal, mc.player);
            if (selfDamage > maximumDamage.getValue()) {
                return;
            }
            final float enemyDamage = BlockUtil.calculateEntityDamage(entityEnderCrystal, entityPlayer);
            if (enemyDamage < minimumDamage.getValue()) {
                return;
            }

            if (breakEfficient.getValue() && selfDamage > enemyDamage) {
                return;
            }

            final float damage = enemyDamage - (selfDamage * 0.5f); // more efficient to value self damage less - cubic
            if (selfDamage > mc.player.getHealth() + mc.player.getAbsorptionAmount()) {
                return;
            }
            map.put(damage, entityEnderCrystal);
        }  ); //NOO SAD FACE IS BAD!!!!!11! );

        if (!map.isEmpty()) {
            return map.lastEntry().getValue();
        }

        return null;} //omg smile so good!!!

    private BlockPos pos(final EntityPlayer entityPlayer) {
        if (blyat.getValue()) {
            BlockPos blockPos = null;
            float maxDamage = 1.0F;

            float radius = placeRange.getValue();
            EntityPlayer target = null;
            BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain();
            for (float x = radius; x >= -radius; x--) {
                for (float z = radius; z >= -radius; z--) {
                    for (float y = radius; y >= -radius; y--) {
                        pos.setPos(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z);
                        final double distance = mc.player.getDistanceSq(pos);
                        if (distance > radius * radius)
                            continue;
                    }
                }
            }
        }

        final TreeMap<Float, BlockPos> map = new TreeMap<>(); // Create a TreeMap to store the damage and position of blocks

        // Get all blocks within a certain radius and filter out invalid blocks
        BlockUtil.getBlocksInRadius(targetRange.getValue()).stream().filter(pos -> BlockUtil.valid(pos, updated.getValue())).forEach(pos -> {
            AxisAlignedBB bb = mc.player.getEntityBoundingBox(); // Calculate the player's bounding box
            Vec3d center = new Vec3d(bb.minX + (bb.maxX - bb.minX) / 2, bb.minY + (bb.maxY - bb.minY) / 2, bb.minZ + (bb.maxZ - bb.minZ) / 2); // Calculate the center of the player's bounding box

            // Check if the enemy is behind a wall
            if (mc.world.rayTraceBlocks(center, new Vec3d(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5), false, true, false) != null) {
                // If the distance between the player and the block is greater than the PlaceWallRange (If the check above is not fulfilled) , return
                if (mc.player.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > placeWallRange.getValue())
                    return;
            }
            if (Math.sqrt(mc.player.getDistanceSq(pos)) > placeRange.getValue()) { // If the distance between the player and the block is greater than the PlaceRange, return
                return;
            }

            if (!mc.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(new BlockPos(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5))).isEmpty()) {

                return;
            }
            final float selfDamage = BlockUtil.calculatePosDamage(pos, mc.player); // Calculate the damage that would be inflicted on the player if the block were placed
            if (selfDamage > maximumDamage.getValue()) {
                return;
            }
            final float enemyDamage = BlockUtil.calculatePosDamage(pos, entityPlayer); // Calculate the damage that would be inflicted on the enemy player if the block were placed
            if (enemyDamage < minimumDamage.getValue()) {
                return;
            }

            if (placeEfficient.getValue() && selfDamage > enemyDamage) {
                return;
            }

            final float damage = enemyDamage - (selfDamage * 0.5f); // more efficient to value self damage less - cubic
            if (selfDamage > mc.player.getHealth() + mc.player.getAbsorptionAmount()) { // If the self damage is greater than the player's health plus absorption amount, return
                return;
            }
            map.put(damage, pos); // Add the net damage and position to the map
        });

        if (!map.isEmpty()) { // If the map is not empty, return the position with the highest net damage
            return map.lastEntry().getValue();
        }

        return null; // Otherwise, return null
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
        return null;} //zane like smile ;}

    //Render Code
    @EventListener
    public void onRender3D(final Render3DEvent event) {
        BlockPos renderPos = (placedPos != null) ? placedPos : lastPos;
        if (renderPos != null && mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL) || mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL)) {
            float newOpacity = (placedPos != null) ? defualtOpacityVal.getValue() : MathUtil.lerp(opacity.getValue(), 0f, 0.01f); //This code is too cramped in for me to comment out thanks to MrBubbleGum. thanks for improving tho
            opacity.setValue(Math.max(newOpacity, 0.0f)); // Ensure opacity doesn't go below 0
            GradientShader.setup(opacity.getValue());
            RenderUtil.boxShader(renderPos);
            RenderUtil.outlineShader(renderPos);
            RenderUtil.outlineShader(renderPos);
            GradientShader.finish();
            if (placedPos != null) {
                lastPos = placedPos; // Saves the lastPos (The block where fading happens)
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
