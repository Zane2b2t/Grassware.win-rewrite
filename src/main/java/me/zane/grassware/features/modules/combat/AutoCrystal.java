package me.zane.grassware.features.modules.combat;
//WARNING: ALL CONTENT BELONGS TO https://github.com/Zane2b2t , IF ANY OF THE CLASSES CONTAINING THIS WARNING ARENT IN https://github.com/Zane2b2t/Grassware.win-Rewrite INFORM GITHUB TO DMCA
//TODO: make it so we only rotate to visible hitbox of a crystal.
import com.mojang.realmsclient.gui.ChatFormatting;
import me.zane.grassware.GrassWare;
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.*;
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
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static me.zane.grassware.util.BlockUtil.calculateRotations;
import static me.zane.grassware.util.BlockUtil.setPlayerRotations;
import static net.minecraft.network.play.client.CPacketUseEntity.Action.ATTACK;
import static org.lwjgl.opengl.GL11.*;

public class AutoCrystal extends Module {
    private final ModeSetting mode = register("Mode", "Sequential", Arrays.asList("Sequential", "Adaptive"));
    private final ModeSetting breakingMode = register("BreakingMode", "Calculated", Arrays.asList("Calculated", "Instant"));
    private final ModeSetting syncMode = register("SynMode", "instant", Arrays.asList("Instant", "Sound"));
    public final ModeSetting rotateMode = register("RotateMode", "PlaceBreak", Arrays.asList("Place", "Break", "PlaceBreak", "None"));

    private final ModeSetting logic = register("Logic", "BreakPlace", Arrays.asList("BreakPlace", "PlaceBreak"));
    private final FloatSetting placeRange = register("Place Range", 5.0f, 1.0f, 6.0f);
    private final FloatSetting placeWallRange = register("Place Wall Range", 3.0f, 1.0f, 6.0f);
    private final FloatSetting breakRange = register("BreakRange", 5.0f, 1.0f, 6.0f);
    private final FloatSetting breakWallRange = register("Break Wall Range", 3.0f, 1.0f, 6.0f);
    private final FloatSetting targetRange = register("Target Range", 5.0f, 0.1f, 15.0f);
    private final FloatSetting minimumDamage = register("Minimum Damage", 6.0f, 0.1f, 12.0f);
    private final FloatSetting facePlaceHP = register("FacePlaceHP", 6.0f, 0.1f, 12.0f);
    private final FloatSetting maximumDamage = register("Maximum Damage", 8.0f, 0.1f, 12.0f);
    private final BooleanSetting waitForBreak = register("WaitForBreak", false);
    private final BooleanSetting debugRotations = register("DebugRotations", false);
    private final BooleanSetting antiStuck = register("AntiStuck", false);
    private final BooleanSetting rebreakStuck = register("ReBreakStuck", false);
    private final IntSetting antiStuckTicks = register("AntiStuckTicks", 8, 0, 40);
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
    private final BooleanSetting brr = register("BRR", false); //dev setting
    private final IntSetting latencyd = register("Latency", 50, 1, 100);
    private final BooleanSetting instantExplode = register("InstantBreak", false); //dev setting
    private final BooleanSetting breakMop = register("breakMap", false); //dev setting
    private final BooleanSetting bongo = register("bongo", false);
    private final BooleanSetting second = register("Second", false);
    private final BooleanSetting predict = register("Predict", false);
    private final BooleanSetting strictDir = register("StrictDirection", false);

    private final BooleanSetting inhibit = register("Inhibit", false);
    private final IntSetting packetAmount = register("PacketAmount", 1, 1, 20);
    private final FloatSetting opacity = register("Opacity", 0.5f, 0.1f, 1.0f);
    private final FloatSetting defualtOpacityVal = register("DOV", 0.5f, 0.1f, 1.0f);
    private final BooleanSetting renderRing = register("Ring", false); //for some reason this is banga langa. when disabled it renders ring. when enabled it doesn't?
    private final Map<Integer, Long> breakMap = new ConcurrentHashMap<>();
    ArrayList<EntityEnderCrystal> crystals = new ArrayList<>();

    public boolean rotating;
    public BlockPos placedPos;
    private BlockPos lastPos;
    private long placeTime;
    private long breakTime;
    private float i = 0.0f;
    private float[] rotations;
    private EnumHand enumHand;
    private boolean hasPlaced = false;
    private boolean hasBroken = false;
    private static final float OFFSET = 0.5f;
    public static AutoCrystal Instance = new AutoCrystal();
    private final Set<Integer> attackedCrystalIds = new HashSet<>(); //this only used for stuck crystals. not inhibit
    @Override
    public void onDisable() {
        crystals.clear();
        attackedCrystalIds.clear();
        rotating = false;
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
            rotating = false;
        } else {
            final BlockPos pos = pos(entityPlayer);
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

    private void attackFire(BlockPos pos) {
        BlockPos firePos = pos.up();
        if (mc.world.getBlockState(firePos).getBlock() instanceof BlockFire) {
            mc.playerController.clickBlock(firePos, EnumFacing.UP);
        }
    }

    public void placeCrystal(BlockPos pos) {
        hasPlaced = false;
        if (pos == null) {
            placedPos = null;
            rotating = false;
            return;
        }
        if (rebreakStuck.getValue()) {
            List<EntityEnderCrystal> crystals = mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class,
                    new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(2, 3, 2)));
            for (EntityEnderCrystal crystal : crystals) {
                if (crystal.getPosition().down().equals(pos)
                        && attackedCrystalIds.contains(crystal.getEntityId()) && crystals.contains(crystal)
                        && crystal.ticksExisted >= antiStuckTicks.getValue() / 1.47) {
                    EntityUtil.breakCrystal(crystal, rotating, false, strictDir.getValue(), breakRange(crystal));
                }
            }
        }
        if (waitForBreak.getValue() && !hasBroken) {
            return;
        }

        if (System.currentTimeMillis() - placeTime > placeDelay.getValue()) {
            if (enumHand != null) {
                if (rotateMode.getValue().equals("PlaceBreak") || rotateMode.getValue().equals("Place")) {
                    rotating = true;
                    rotations = calculateRotations(pos, true, true, true);
                }
                EnumFacing facing = EnumFacing.UP;

                if (placedPos != null && placedPos.getY() > mc.player.posY + mc.player.getEyeHeight()) {
                    if (strictDir.getValue()) {
                        facing = BlockUtil.getStrictDirection(pos, rotations, placeRange.getValue());
                        if (facing == null) return;
                    }
                }

                (mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, facing, enumHand, OFFSET, OFFSET, OFFSET));

                if (debugRotations.getValue()) {
                    setPlayerRotations(rotations[0], rotations[1]);
                }
                swingHand();
                hasPlaced = true;


                placedPos = pos;
                placeTime = System.currentTimeMillis();

                if (placedPos != null && bongo.getValue()) {
                    (mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(placedPos, EnumFacing.UP, enumHand, OFFSET, OFFSET, OFFSET));
                }
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
        rotating = false;
        hasBroken = false;
        final boolean isCrystalNotListed = !inhibit.getValue() || !crystals.contains(entityEnderCrystal);
        if (System.currentTimeMillis() - breakTime > breakDelay.getValue() && isCrystalNotListed) {
            if (rotateMode.getValue().equals("PlaceBreak") || rotateMode.getValue().equals("Break")) {
                if (hasPlaced)
                    rotating = true;
                rotations = calculateRotations(entityEnderCrystal.getPosition().down().add(0, 0.5, 0), true, true, true);
                if (debugRotations.getValue())
                    setPlayerRotations(rotations[0], rotations[1]);
            }


            (mc.getConnection()).sendPacket(new CPacketUseEntity(entityEnderCrystal));
            hasBroken = true;
            swingHand();
            crystals.add(entityEnderCrystal);
            handleSetDead(entityEnderCrystal);
            handleFastRemove(entityEnderCrystal);
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
            if (mc.player.getPositionEyes(1).squareDistanceTo(crystal.getPositionVector()) > breakRange(crystal) * breakRange(crystal)) return;

            final EntityPlayer entityPlayer = target(targetRange.getValue());
            if (entityPlayer == null)
                return;
            if (breakingMode.getValue().equals("Calculated")) {
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
            }

            rotating = rotateMode.getValue().equals("PlaceBreak") || rotateMode.getValue().equals("Break") || rotateMode.getValue().equals("Place");

            if (hasPlaced)
                rotations = calculateRotations(crystal.getPosition().add(0, -0.5, 0), true, true, true);
             if (debugRotations.getValue())
                setPlayerRotations(rotations[0], rotations[1]);



            (mc.getConnection()).sendPacket(new CPacketUseEntity(crystal));
            if (predict.getValue() && !crystals.contains(crystal)) {
                if (await.getValue() && !hasPlaced) {
                    return;
                }
                hasBroken = false;
                CPacketUseEntity packetUseEntity = new CPacketUseEntity();
                packetUseEntity.entityId = packet.getEntityID();
                packetUseEntity.action = ATTACK;
                mc.getConnection().sendPacket(packetUseEntity);
                crystals.add(crystal);
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

        if (event.getPacket() instanceof SPacketExplosion) {
            mc.addScheduledTask(() -> {
                for (Entity crystal : mc.world.loadedEntityList) {
                    BlockPos explosionPos = new BlockPos(((SPacketExplosion) event.getPacket()).posX, ((SPacketExplosion) event.getPacket()).posY - 1, ((SPacketExplosion) event.getPacket()).posZ);
                        hasBroken = explosionPos.equals(placedPos);

                    if (crystal == null || crystal.isDead || !(crystal instanceof EntityEnderCrystal))
                        continue;

                    final double range = crystal.getDistanceSq(((SPacketExplosion) event.getPacket()).getX() + 0.5, ((SPacketExplosion) event.getPacket()).getY() + 0.5, ((SPacketExplosion) event.getPacket()).getZ() + 0.5);

                    if (range * range > ((SPacketExplosion) event.getPacket()).getStrength())
                        continue;

                    if (explosionPos.equals(placedPos) && bongo.getValue()) {
                        mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(placedPos, EnumFacing.UP, enumHand, OFFSET, OFFSET, OFFSET));
                    }
                    if (soundRemove.getValue()) {
                        crystal.setDead();
                    }
                    handleFastRemove((EntityEnderCrystal) crystal);
                    handleSetDead((EntityEnderCrystal) crystal);
                }
            });
        }

        if (event.getPacket() instanceof SPacketDestroyEntities) {
            SPacketDestroyEntities packet = event.getPacket();
            for (int id : packet.getEntityIDs()) {
                if (mc.world.getEntityByID(id).getPosition().down().equals(placedPos)) {
                    hasBroken = true;
                }
                try {
                    if (breakMap.containsKey(id) && breakMap.containsKey(packet.getEntityIDs()) && breakMap.get(id) > 1500) {
                        breakMap.remove(id);
                        continue;
                    }
                    if (!fastRemove.getValue()) continue; //!?
                    if (!breakMap.containsKey(id)) continue;
                    hasBroken = false;
                    mc.world.removeEntityFromWorld(id);
                    hasBroken = true;
                } catch (Exception ignored) {
                }
            }
        }

        if (event.getPacket() instanceof SPacketSoundEffect && soundRemove.getValue()) {
            final SPacketSoundEffect packet =  event.getPacket();
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                mc.addScheduledTask(() -> {
                    for (Entity entity : mc.world.loadedEntityList) {
                        hasBroken = false;
                        if (entity instanceof EntityEnderCrystal && entity.getDistanceSq(packet.getX(), packet.getY(), packet.getZ()) < 36) {
                            entity.setDead();
                            hasBroken = true;
                            if (setDead.getValue().equals("Both") || soundRemove.getValue()) {
                                mc.world.removeEntity(entity);
                                mc.world.removeEntityDangerously(entity);
                            }
                        }
                    }
                });
            }
        }
        SPacketSpawnObject spawnedCrystal;
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
            hasBroken = true;
            (packet.getEntityFromWorld(AutoCrystal.mc.world)).setDead();
            AutoCrystal.mc.world.removeEntityFromWorld(packet.entityId);
        }
    }

    @EventListener
    public void onPredict(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSpawnObject && predict.getValue()) {
            SPacketSpawnObject packet = event.getPacket();

            if (packet.getType() != 51) {
                return;
            }
            hasBroken = false;
            EntityEnderCrystal crystal = new EntityEnderCrystal(AutoCrystal.mc.world, packet.getX(), packet.getY(), packet.getZ());
            if (mc.player.getPositionEyes(1).squareDistanceTo(crystal.getPositionVector()) > breakRange(crystal) * breakRange(crystal)) return;

            if (rotateMode.getValue().equals("PlaceBreak") || rotateMode.getValue().equals("Break")) {
                if (hasPlaced)
                    rotating = true;
                rotations = calculateRotations(crystal.getPosition().down().add(0, 0.5, 0), true, true, true);
                if (debugRotations.getValue())
                    setPlayerRotations(rotations[0], rotations[1]);
            }

            CPacketUseEntity crystalPacket = new CPacketUseEntity();
            crystalPacket.entityId = packet.getEntityID();
            crystalPacket.action = ATTACK;
            if (breakMop.getValue()) {
                breakMap.put(packet.getEntityID(), breakMap.containsKey(packet.getEntityID()) ? breakMap.get(packet.getEntityID()) + 1 : 1);
            }
            hasBroken = true;
            AutoCrystal.mc.player.connection.sendPacket(crystalPacket);
            crystals.add(crystal);
            handleFastRemove(crystal);
        }
    }


    @EventListener
    public void onMotionUpdate(MotionUpdateEvent event) {
        if (rotating && placedPos != null) {
            event.setYaw(rotations[0]);
            event.setPitch(rotations[1]);
        }
    }

    private void handleSetDead(EntityEnderCrystal crystal) {
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
            RenderUtil.renderLine(new Vec3d(placedPos.getX() + 0.5, placedPos.getY() + 1, placedPos.getZ() + 0.5), entityPlayer.getPositionVector());
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
        if (placedPos == null && lastPos == null) return;
        BlockPos renderPos = (placedPos != null) ? placedPos : lastPos;
        if (mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL) || mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL)) {
            float newOpacity = (placedPos != null) ? defualtOpacityVal.getValue() : MathUtil.lerp(opacity.getValue(), 0.0f, 0.05f);
            opacity.setValue(Math.max(newOpacity, 0.0f));
            GradientShader.setup(opacity.getValue());
            RenderUtil.boxShader(renderPos);
            GradientShader.finish();
            GradientShader.setup(placedPos != null ? 1.0f : opacity.getValue());
            RenderUtil.outlineShader(renderPos);
            GradientShader.finish();
            if (placedPos != null) {
                lastPos = placedPos;
            }
        }
    }

    private EntityEnderCrystal crystal(final EntityPlayer entityPlayer) {
        final TreeMap<Float, EntityEnderCrystal> map = new TreeMap<>();

        mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityEnderCrystal && !(mc.player.getDistanceSq(entity) > breakRange(entity) * breakRange(entity))).map(entity -> (EntityEnderCrystal) entity).forEach(entityEnderCrystal -> {

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

    //TODO: somehow optimize this or make code look better
    private BlockPos pos(final EntityPlayer entityPlayer) {
        return BlockUtil.getBlocksInRadius(targetRange.getValue()).stream()
                .filter(pos -> {
                    if (fireBreaker.getValue() && mc.world.getBlockState(pos.up()).getBlock() instanceof BlockFire) {
                        attackFire(pos);
                    }

                    if (second.getValue())
                        if (!BlockUtil.canPlaceCrystal(pos, true)) return false;

                    if (!BlockUtil.valid(pos, updated.getValue())) return false;

                    if (mc.world.rayTraceBlocks(mc.player.getPositionEyes(1), new Vec3d(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5), false, true, false) != null) {
                        if (mc.player.getPositionEyes(1).squareDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > placeWallRange.getValue() * placeWallRange.getValue()) {
                            return false;
                        }
                    }

                    if (mc.player.getPositionEyes(1).squareDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > placeRange.getValue() * placeRange.getValue()) {
                        return false;
                    }

                    if (!mc.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos.add(0.5, 1.0, 0.5))).isEmpty()) {
                        return false;
                    }
                    // Check for dropped items on the pos
                    if (!mc.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.add(0.5, 1.0, 0.5))).isEmpty()) {
                        return false;
                    }
                    // Check for arrows on the pos
                    if (!mc.world.getEntitiesWithinAABB(EntityArrow.class, new AxisAlignedBB(pos.add(0.5, 1.0, 0.5))).isEmpty()) {
                        return false;
                    }
                    float selfDamage = BlockUtil.calculatePosDamage(pos, mc.player);
                    if (selfDamage > maximumDamage.getValue()) {
                        return false;
                    }

                    if (entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount() <= facePlaceHP.getValue()) {
                        return true;
                    }

                    float enemyDamage = BlockUtil.calculatePosDamage(pos, entityPlayer);
                    if (enemyDamage < minimumDamage.getValue()) {
                        return false;
                    }

                    if (placeEfficient.getValue() && selfDamage > enemyDamage) {
                        return false;
                    }

                    if (selfDamage > mc.player.getHealth() + mc.player.getAbsorptionAmount()) {
                        return false;
                    }

                    List<EntityEnderCrystal> crystals = mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class,
                            new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(2, 3, 2)));
                    for (EntityEnderCrystal crystal : crystals) {
                        if (crystal.ticksExisted >= antiStuckTicks.getValue() / 2 && rebreakStuck.getValue()) {
                            int crystalId = crystal.getEntityId();
                            if (!attackedCrystalIds.contains(crystalId)) {
                                //mc.playerController.attackEntity(mc.player, crystal);
                                EntityUtil.breakCrystal(crystal, rotating, false, strictDir.getValue(), breakRange(crystal));
                                handleSetDead(crystal);
                                handleFastRemove(crystal);
                                attackedCrystalIds.add(crystalId);
                            }
                        }
                    }

                    if (antiStuck.getValue()) {
                        for (EntityEnderCrystal crystal : crystals) {
                            if (crystal.ticksExisted > antiStuckTicks.getValue() && crystals.contains(crystal)) {
                                return false;
                            }
                        }
                    }

                    return true;

                })
                .max(Comparator.comparingDouble(pos -> {
                    float enemyDamage = BlockUtil.calculatePosDamage(pos, entityPlayer);
                    float selfDamage = BlockUtil.calculatePosDamage(pos, mc.player);
                    return enemyDamage - selfDamage;
                }))
                .orElse(null);
    }

    private EntityPlayer target(final float range) {
        final TreeMap<Float, EntityPlayer> map = new TreeMap<>();
        mc.world.playerEntities.stream().filter(e -> !e.equals(mc.player) && !e.isDead).forEach(entityPlayer -> {
            if (entityPlayer.getHealth() <= 0)
                return;
            final double distance = mc.player.getPositionEyes(1).squareDistanceTo(entityPlayer.getPositionVector());
            if (distance <= range * range && !GrassWare.friendManager.isFriend(entityPlayer.getName())) {
                float enemyDamage = BlockUtil.calculatePosDamage(entityPlayer.getPosition().add(entityPlayer.motionX * mc.getConnection().getPlayerInfo(mc.player.getName()).getResponseTime() / 2, entityPlayer.motionY * mc.getConnection().getPlayerInfo(mc.player.getName()).getResponseTime() / 2, entityPlayer.motionZ * mc.getConnection().getPlayerInfo(mc.player.getName()).getResponseTime() / 2), entityPlayer);
                map.put(enemyDamage, entityPlayer);
            }
        });
        if (!map.isEmpty()) {
            return map.lastEntry().getValue();
        }
        return null;
    }

    @Override
    public String getInfo() {
        if (placedPos != null) {
            return " [" + ChatFormatting.WHITE + target(targetRange.getValue()).getName() + ChatFormatting.RESET + "]";
        } else {
            return " [" + ChatFormatting.WHITE + "Idle" + ChatFormatting.RESET + "]";
        }
    }
}
