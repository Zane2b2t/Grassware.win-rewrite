package me.zane.grassware.features.modules.combat;
//WARNING: ALL CONTENT BELONGS TO https://github.com/Zane2b2t , IF ANY OF THE CLASSES CONTAINING THIS WARNING ARENT IN https://github.com/Zane2b2t/Grassware.win-Rewrite INFORM GITHUB TO DMCA

import com.mojang.realmsclient.gui.ChatFormatting;
import me.zane.grassware.GrassWare;
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.PacketEvent;
import me.zane.grassware.event.events.Render3DEvent;
import me.zane.grassware.event.events.Render3DPreEvent;
import me.zane.grassware.event.events.UpdatePlayerWalkingEvent;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.modules.client.ClickGui;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.features.setting.impl.FloatSetting;
import me.zane.grassware.features.setting.impl.IntSetting;
import me.zane.grassware.features.setting.impl.ModeSetting;
import me.zane.grassware.mixin.mixins.ICPacketUseEntity;
import me.zane.grassware.shader.impl.GradientShader;
import me.zane.grassware.util.*;

import net.minecraft.block.BlockFire;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketPlayerDigging;
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

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static net.minecraft.network.play.client.CPacketUseEntity.Action.ATTACK;
import static org.lwjgl.opengl.GL11.*;

public class AutoCrystal extends Module {
    private final ModeSetting mode = register("Mode", "Sequential", Arrays.asList("Sequential", "Adaptive"));
    private final ModeSetting syncMode = register("SynMode", "instant", Arrays.asList("Instant", "Sound"));
    private final ModeSetting logic = register("Logic", "BreakPlace", Arrays.asList("BreakPlace", "PlaceBreak"));
    private final FloatSetting placeRange = register("Place Range", 5.0f, 1.0f, 6.0f);
    private final FloatSetting placeWallRange = register("Place Wall Range", 3.0f, 1.0f, 6.0f);
    private final FloatSetting breakRange = register("BreakRange", 5.0f, 1.0f, 6.0f);
    private final FloatSetting breakWallRange = register("Break Wall Range", 3.0f, 1.0f, 6.0f);
    private final FloatSetting targetRange = register("Target Range", 5.0f, 0.1f, 15.0f);
    private final FloatSetting minimumDamage = register("Minimum Damage", 6.0f, 0.1f, 12.0f);
    private final FloatSetting maximumDamage = register("Maximum Damage", 8.0f, 0.1f, 12.0f);
    private final BooleanSetting waitForBreak = register("WaitForBreak", false);
    private final BooleanSetting antiStuck = register("AntiStuck", false);
    private final FloatSetting placeDelay = register("Place Delay", 0.0f, 0f, 500.0f);
    private final BooleanSetting placeEfficient = register("PlaceEfficient", true);
    private final FloatSetting breakDelay = register("Break Delay", 50.0f, 0f, 500.0f);
    private final BooleanSetting breakEfficient = register("BreakEfficient", true);

    private final BooleanSetting updated = register("1.13+", false);
    private final BooleanSetting await = register("Await", false);
    private final BooleanSetting fireBreaker = register("FireBreaker", false);
    private final ModeSetting setDead = register("Set Dead", "Set Dead", Arrays.asList("None", "Set Dead", "Remove", "Both"));
    private final BooleanSetting fastRemove = register("Fast Remove", false);
    private final BooleanSetting soundRemove = register("Sound Remove", false);
    private final BooleanSetting ping = register("PingCalc", false);
    private final BooleanSetting interact = register("Interact", false); //dev setting
    private final BooleanSetting brr = register("BRR", false); //dev setting
    private final IntSetting latencyd = register("Latency", 50, 1, 100);
    private final BooleanSetting instantExplode = register("InstantBreak", false); //dev setting
    private final BooleanSetting breakMop = register("breakMap", false); //dev setting
    private final BooleanSetting bongo = register("bongo", false);
    private final BooleanSetting predict = register("Predict", false);

    private final BooleanSetting inhibit = register("Inhibit", false);
    private final IntSetting packetAmount = register("PacketAmount", 1, 1, 20);
    private final IntSetting extraTicks = register("ExtraPolationTicks", 4, 0, 20);
    private final FloatSetting opacity = register("Opacity", 0.5f, 0.1f, 1.0f);
    private final FloatSetting defualtOpacityVal = register("DOV", 0.5f, 0.1f, 1.0f);
    private final BooleanSetting renderRing = register("Ring", false); //for some reason this is banga langa. when disabled it renders ring. when enabled it doesn't?
    private final Map<Integer, Long> breakMap = new ConcurrentHashMap<>();
    ArrayList<EntityEnderCrystal> crystals = new ArrayList<>();
    private BlockPos currentPos;
    private BlockPos placedPos;
    private BlockPos lastPos;
    private BlockPos firePos;
    private EntityPlayer targetPlayer;
    private long placeTime;
    private long breakTime;
    private float i = 0.0f;
    private EnumHand enumHand;
    private boolean hasPlaced = false;
    private boolean hasBroken = false;
    private static final float OFFSET = 0.5f;


    @Override
    public void onDisable() {
        crystals.clear();
    }

    @Override
    public void onEnable() {
        swingHand();
    }

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

    public void swingHand() {
        if (mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL)) {
            mc.player.swingArm(EnumHand.MAIN_HAND);
            enumHand = EnumHand.MAIN_HAND;
        } else if (mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL)) {
            mc.player.swingArm(EnumHand.OFF_HAND);
            enumHand = EnumHand.OFF_HAND;
        }
    }

    public void doAwait (BlockPos pos, EntityPlayer entityPlayer ) {
        if (await.getValue()) {
            final EntityEnderCrystal entityEnderCrystal = crystal(entityPlayer);
            if (hasBroken) {
                placeCrystal(pos);
          //      (mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.UP, enumHand, 0.5f, 0.5f, 0.5f));
            }
        //    if (hasPlaced) {
            //    (mc.getConnection()).sendPacket(new CPacketUseEntity(entityEnderCrystal));
          //  }
        }
    }
    private void attackFire(BlockPos pos) {
        firePos = pos.up();
        if (mc.world.getBlockState(firePos).getBlock() instanceof BlockFire) {
            mc.playerController.clickBlock(firePos, EnumFacing.UP);
        }
    }

    public void placeCrystal(BlockPos pos) {
        hasPlaced = false;
        if (pos == null) {
            placedPos = null;
            return;
        }
        if (waitForBreak.getValue() && !hasBroken) {
            return;
        }
        if (System.currentTimeMillis() - placeTime > placeDelay.getValue()) {
            if (enumHand != null) {
                (mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.UP, enumHand, OFFSET, OFFSET, OFFSET));
                swingHand();
                hasPlaced = true;
            }
            placedPos = pos;
            placeTime = System.currentTimeMillis();
            if (placedPos != null && bongo.getValue()) {
                (mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(placedPos, EnumFacing.UP, enumHand, OFFSET, OFFSET, OFFSET));
            }
        }
    }

    public void breakCrystal(EntityPlayer entityPlayer) {
        if (await.getValue() && !hasPlaced) {
            return;
        }
        final EntityEnderCrystal entityEnderCrystal = crystal(entityPlayer);
        if (entityEnderCrystal == null) {
            return;
        }
        hasBroken = false;
        final boolean isCrystalNotListed = !inhibit.getValue() || !crystals.contains(entityEnderCrystal);
        if (System.currentTimeMillis() - breakTime > breakDelay.getValue() && isCrystalNotListed) {
            crystals.add(entityEnderCrystal);
            (mc.getConnection()).sendPacket(new CPacketUseEntity(entityEnderCrystal));
            hasBroken = true;
            swingHand();
            handleSetDead(entityEnderCrystal);
            if (fastRemove.getValue()) {
                mc.addScheduledTask(() -> {
                    mc.world.removeEntity(entityEnderCrystal);
                    mc.world.removeEntityDangerously(entityEnderCrystal);
                });
            }
        }
        breakTime = System.currentTimeMillis();
        try {
            breakMap.put(entityEnderCrystal.getEntityId(), System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
            final float enemyDamage = BlockUtil.calculateEntityDamage(crystal, entityPlayer); //cba to make it extrapolate
            if (enemyDamage < minimumDamage.getValue()) {
                return;
            }
            if (selfDamage > mc.player.getHealth() + mc.player.getAbsorptionAmount()) {
                return;
            }
            (mc.getConnection()).sendPacket(new CPacketUseEntity(crystal));
            if (predict.getValue()) {
                if (await.getValue() && !hasPlaced) {
                    return;
                }
                hasBroken = false;
                CPacketUseEntity packetUseEntity = new CPacketUseEntity();
                packetUseEntity.entityId = packet.getEntityID();
                packetUseEntity.action = ATTACK;
                handleFastRemove(crystal);
            }
            swingHand();
            hasBroken = true;
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
                    if (breakMap.containsKey(id) && breakMap.containsKey(packet.getEntityIDs()) && breakMap.get(id) > 1500) {
                        breakMap.remove(id);
                        continue;
                    }
                    if (!fastRemove.getValue()) continue; //!?
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
        SPacketSpawnObject spawnedCrystal = new SPacketSpawnObject();
        if (event.getPacket() instanceof SPacketSpawnObject && (spawnedCrystal = event.getPacket()).getType() == 51 && this.instantExplode.getValue()) {
            CPacketUseEntity attackPacket = new CPacketUseEntity();
            ((ICPacketUseEntity) attackPacket).setEntityId(spawnedCrystal.getEntityID());
            ((ICPacketUseEntity) attackPacket).setAction(ATTACK);

            for (int i = 1; i <= packetAmount.getValue(); i++) {
                mc.player.connection.sendPacket(attackPacket);
            }
        }
                                 //dueto the delay between the recieving and sending of the cclient and server. this only works if you have low ping.
                                //if you have high ping the highestEntity on your client may not be the highestentity on the server Anymore.
        if (brr.getValue() && event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock) event.getPacket();

            Entity highestEntity = null;
            int entityId = 0;
            for (Entity entity : mc.world.loadedEntityList) {
                if (entity instanceof EntityEnderCrystal && entity.getEntityId() > entityId && (!inhibit.getValue() || !crystals.contains(entity))) {
                    entityId = entity.getEntityId();
                    highestEntity = entity;
                }
            }
            if (highestEntity != null) {
                int latency = (mc.getConnection()).getPlayerInfo(mc.getConnection().getGameProfile().getId()).getResponseTime() / latencyd.getValue();
                for (int i = latency; i < latency + 10; i++) {
                    try {
                        CPacketUseEntity cPacketUseEntity = new CPacketUseEntity();
                        ((ICPacketUseEntity) cPacketUseEntity).setEntityId(ping.getValue() ? highestEntity.getEntityId() + i : highestEntity.getEntityId());
                        ((ICPacketUseEntity) cPacketUseEntity).setAction(ATTACK);
                        PacketUtil.invoke(cPacketUseEntity);
                        if (fastRemove.getValue()) {
                            mc.world.removeEntityFromWorld(entityId);
                        }
                        crystals.add((EntityEnderCrystal) highestEntity);
                    } catch (Exception ignored) {
                    }
                }
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
      //  if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
      //      handleCPacketPlayerTryUseItemOnBlock(event);
     //   } else if (event.getPacket() instanceof CPacketUseEntity) {
      //      handleCPacketUseEntity(event);
      //  }
    }

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
            if (fastRemove.getValue()) {
                mc.addScheduledTask(() -> {
                    mc.world.removeEntity(crystal);
                    mc.world.removeEntityDangerously(crystal);
                });
            }
            if (breakMop.getValue()) {
                breakMap.put(packet.getEntityID(), breakMap.containsKey(packet.getEntityID()) ? breakMap.get(packet.getEntityID()) + 1 : 1);
            }
            AutoCrystal.mc.player.connection.sendPacket(crystalPacket);
            crystals.add(crystal);
        }

    }


    public EntityEnderCrystal getCrystal(BlockPos pos) {
        if (mc.player == null || mc.world == null) {
            return null;
        }
        java.util.List<Entity> entities = mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos));
        for (Entity entity : entities) {
            if(antiStuck.getValue()){
                AxisAlignedBB aabb = new AxisAlignedBB(
                        pos.getX() -1,
                        pos.getY() - 0.5,
                        pos.getZ() - 1,
                        pos.getX() + 2,
                        pos.getY() + 1,
                        pos.getZ() + 2
                );

                if(!mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, aabb).isEmpty())
                    continue;
            }
            if (entity instanceof EntityEnderCrystal) {
                return (EntityEnderCrystal) entity;
            }
        }
        return null;
    }

    private void handleSetDead(EntityEnderCrystal crystal) {
        mc.getConnection().sendPacket(new CPacketUseEntity(crystal));
        if (setDead.getValue().equals("Set Dead") || setDead.getValue().equals("Both"))
            crystal.setDead();
        if (setDead.getValue().equals("Remove") || setDead.getValue().equals("Both"))
            mc.world.removeEntity(crystal);
    }
    private void handleFastRemove(EntityEnderCrystal crystal) {
        if (fastRemove.getValue()) {
            crystal.setDead();
            mc.addScheduledTask(() -> {
                mc.world.removeEntity(crystal);
                mc.world.removeEntityDangerously(crystal);
            }); // sad );
        }
    }

    public static Color interpolate(Color start, Color end, float progress) {
        float[] startComponents = new float[4];
        start.getRGBComponents(startComponents);
        float[] endComponents = new float[4];
        end.getRGBComponents(endComponents);
        return new Color(
                startComponents[0] + (endComponents[0] - startComponents[0]) * progress,
                startComponents[1] + (endComponents[1] - startComponents[1]) * progress,
                startComponents[2] + (endComponents[2] - startComponents[2]) * progress,
                startComponents[3] + (endComponents[3] - startComponents[3]) * progress
        );
    }
    @EventListener
    public void onRender3DPre(final Render3DPreEvent event) {
        final EntityPlayer entityPlayer = EntityUtil.entityPlayer(targetRange.getValue());
        if (entityPlayer == null || !mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL) && !mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL)) {
            return;
        }
        if (renderRing.getValue()) {
            final Vec3d vec = RenderUtil.interpolateEntity(entityPlayer);
            final Color color = ClickGui.Instance.getGradient()[0];
            final Color color2 = ClickGui.Instance.getGradient()[1];
            final Color color3 = ClickGui.Instance.getGradient()[2];
            final Color color4 = ClickGui.Instance.getGradient()[3];
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
            for (double i = 0; i <= 360; i += 0.5) {
                final double x = ((Math.cos(i * Math.PI / 180F) * entityPlayer.width) + vec.x);
                final double y = (vec.y + (entityPlayer.height / 2.0f));
                final double z = ((Math.sin(i * Math.PI / 180F) * entityPlayer.width) + vec.z);
                if (i <= 120) {
                    RenderUtil.glColor(interpolate(color, color2, (float) (i / 120.0)));
                } else if (i <= 240) {
                    RenderUtil.glColor(interpolate(color2, color3, (float) ((i - 120) / 120.0)));
                } else {
                    RenderUtil.glColor(interpolate(color3, color4, (float) ((i - 240) / 120.0)));
                }
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

    }

    @EventListener
    public void onRender3D(final Render3DEvent event) {
        BlockPos renderPos = (placedPos != null) ? placedPos : lastPos;
        if (renderPos != null && mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL) || mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL)) {
            float newOpacity = (placedPos != null) ? defualtOpacityVal.getValue() : MathUtil.lerp(opacity.getValue(), 0f, 0.05f);
            opacity.setValue(Math.max(newOpacity, 0.0f)); // Ensure opacity doesn't go below 0
            GradientShader.setup(opacity.getValue());
            RenderUtil.boxShader(renderPos);
            RenderUtil.outlineShader(renderPos);
            RenderUtil.outlineShader(renderPos);
            GradientShader.finish();
            if (placedPos != null) {
                lastPos = placedPos;
            }
        }
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
            if (breakEfficient.getValue() && selfDamage > enemyDamage) {
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

    private float breakRange(Entity entity) {
        if (mc.player.canEntityBeSeen(entity))
            return breakRange.getValue();
        return breakWallRange.getValue();
    }

    private BlockPos pos(final EntityPlayer entityPlayer) {
        final TreeMap<Float, BlockPos> map = new TreeMap<>();

        BlockUtil.getBlocksInRadius(targetRange.getValue()).stream().filter(pos -> {
            if (fireBreaker.getValue() && mc.world.getBlockState(pos.up()).getBlock() instanceof BlockFire) {
                attackFire(pos);
            }

            return BlockUtil.valid(pos, updated.getValue());
        }).forEach(pos -> {
            if (mc.world.rayTraceBlocks(mc.player.getPositionVector().add(0, mc.player.eyeHeight, 0), new Vec3d(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5), false, true, false) != null) {
                if (mc.player.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > placeWallRange.getValue())
                    return;
            }
            if (Math.sqrt(mc.player.getDistanceSq(pos)) > placeRange.getValue()) {
                return;
            }
            if (!mc.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(new BlockPos(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5))).isEmpty()) {
                return;
            }
            // Check for dropped items on the pos
            if (!mc.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(new BlockPos(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5))).isEmpty()) {
                return;
            }
            // Check for arrows on the pos
            if (!mc.world.getEntitiesWithinAABB(EntityArrow.class, new AxisAlignedBB(new BlockPos(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5))).isEmpty()) {
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
            if (placeEfficient.getValue() && selfDamage > enemyDamage) {
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
        final TreeMap<Float, EntityPlayer> map = new TreeMap<>();
        mc.world.playerEntities.stream().filter(e -> !e.equals(mc.player) && !e.isDead).forEach(entityPlayer -> {
            if (entityPlayer.getHealth() <= 0)
                return;
            final float distance = entityPlayer.getDistance(mc.player);
            if (distance < range && !GrassWare.friendManager.isFriend(entityPlayer.getName())) {
                // Calculate the damage dealt to the enemy at their current position
                float enemyDamage = BlockUtil.calculatePosDamage(entityPlayer.getPosition(), entityPlayer);
                // Store the damage in the map
                map.put(enemyDamage, entityPlayer);
            }
        });
        if (!map.isEmpty()) {
            // Return the enemy with the highest damage
            return map.lastEntry().getValue();
        }
        return null;
    }


    @Override
    public String getInfo() {
        if (placedPos != null) {
            return " [" + ChatFormatting.WHITE + "Active" + ChatFormatting.RESET + "]";
        } else {
            return " [" + ChatFormatting.WHITE + "Idle" + ChatFormatting.RESET + "]";
        }
    }
}

