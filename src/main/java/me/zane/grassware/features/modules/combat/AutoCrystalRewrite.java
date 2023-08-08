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
import me.zane.grassware.util.MathUtil;
import me.zane.grassware.util.RenderUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.*;

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

    //Break Page
    private final ModeSetting setDead = register("SetDead", "Both", Arrays.asList("SetDead", "Remove", "Both")).invokeVisibility(z -> page.getValue().equals("Break"));
    private final FloatSetting breakRange = register("BreakRange", 4.5f, 1.0f, 6.0f).invokeVisibility(z -> page.getValue().equals("Break"));
    private final FloatSetting breakWallRange = register("BreakTrace", 4.5f, 1.0f, 6.0f).invokeVisibility(z -> page.getValue().equals("Break"));
    private final BooleanSetting predict = register("Predict", false).invokeVisibility(z -> page.getValue().equals("Misc"));
    private final BooleanSetting await = register("Await", false).invokeVisibility(z -> page.getValue().equals("Break"));
    private final BooleanSetting fastRemove = register("FastRemove", false).invokeVisibility(z -> page.getValue().equals("Break"));

    //Render Page
    private final FloatSetting opacity = register("Opacity", 0.5f, 0.0f, 1.0f).invokeVisibility(z -> page.getValue().equals("Render"));
    private final FloatSetting defualtOpacityVal = register("DOV", 0.5f, 0.0f, 1.0f).invokeVisibility(z -> page.getValue().equals("Render"));

    //Misc Page
    private final ModeSetting mode = register("Mode", "Sequential", Arrays.asList("Sequential", "Adaptive")).invokeVisibility(z -> page.getValue().equals("Misc"));
    private final ModeSetting logic = register("Logic", "BreakPlace", Arrays.asList("BreakPlace", "PlaceBreak")).invokeVisibility(z -> page.getValue().equals("Misc"));
    private final BooleanSetting bongo = register("Bongo", false).invokeVisibility(z -> page.getValue().equals("Misc"));

    //The stuff
    private BlockPos placedPos;
    private BlockPos lastPos;
    private BlockPos currentPos;
    private EntityPlayer targetPlayer;
    private EnumHand enumHand;
    private boolean hasPlaced = false;
    private final boolean hasBroken = false;
    private long placeTime;

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
                   // breakCrystal(entityPlayer);
                    break;
                case "BreakPlace":
                   // breakCrystal(entityPlayer);       we dont have break yet lol
                    placeCrystal(pos);
                    break;
            }
        }
    }


    //Place Code
    public void placeCrystal(BlockPos pos) {
        hasPlaced = false;
        if (pos == null) {
            placedPos = null;
            return;
        }
        if (System.currentTimeMillis() - placeTime > placeDelay.getValue()) {
            if (enumHand != null) {
                placePacket(pos);
                swingHand();
            }
            hasPlaced = true;
            placedPos = pos;
            placeTime = System.currentTimeMillis();
        }
    }

    private void placePacket(BlockPos pos) {
        (mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.UP, enumHand, 0.5f, 0.5f, 0.5f));
        if (placedPos != null && bongo.getValue()) {
            (mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(placedPos, EnumFacing.UP, enumHand, 0.5f, 0.5f, 0.5f));

        }
    }

    //Break Code
    private void breakCrystal() {
        if (await.getValue()) {
            if (!hasPlaced) {// If Await is enabled, the method only executes if we placed a crystal

            }
        }
        //TODO: Actually code this lol

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
          /*  if (breakMop.getValue()) {
                breakMap.put(packet.getEntityID(), breakMap.containsKey(packet.getEntityID()) ? breakMap.get(packet.getEntityID()) + 1 : 1);
            } */
            AutoCrystal.mc.player.connection.sendPacket(crystalPacket);
            // crystals.add(crystal);
        }

    }
    //no explaination needed
    private void breakPacket(EntityEnderCrystal crystal) {
        (mc.getConnection()).sendPacket(new CPacketUseEntity(crystal));
    }
    //no explaination needed
    private float breakRange(Entity entity) {
        if (mc.player.canEntityBeSeen(entity))
            return breakRange.getValue();
        return breakWallRange.getValue();
    }
//no explaination needed
    private void handleSetDead(EntityEnderCrystal crystal) {
        (mc.getConnection()).sendPacket(new CPacketUseEntity(crystal));
        if (setDead.getValue().equals("Set Dead") || setDead.getValue().equals("Both"))
            crystal.setDead();
        if (setDead.getValue().equals("Remove") || setDead.getValue().equals("Both"))
            mc.world.removeEntity(crystal);
    }
    //no explaination needed
    private void handleFastRemove(EntityEnderCrystal crystal) {
        if (fastRemove.getValue()) {
            mc.addScheduledTask(() -> {
                mc.world.removeEntity(crystal);
                mc.world.removeEntityDangerously(crystal);
            }); // sad );
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
        }
    } //TODO: Expand this when we make breakCrystal

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
            final float damage = enemyDamage - selfDamage;
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
            if (!mc.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(new BlockPos(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5))).isEmpty()) {              // If there are any players within a certain distance of the block, return

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
            final float damage = enemyDamage - selfDamage; // Calculate the net damage (enemy damage - self damage)
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
