package me.zane.grassware.features.modules.combat;

import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.PacketEvent;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.features.setting.impl.FloatSetting;
import me.zane.grassware.features.setting.impl.IntSetting;
import me.zane.grassware.util.BlockUtil;
import me.zane.grassware.util.InventoryUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class Surround extends Module {

    //private final FloatSetting delay = register("Delay", 0f, 0f, 10f);
    private final BooleanSetting extend = register("Extend", false);
    private final BooleanSetting center = register("Center", true);
    private final BooleanSetting rotate = register("Rotate", false);
    private final BooleanSetting packet = register("Packet", false);
    private final BooleanSetting instant = register("Instant", true);
    private final BooleanSetting confirm = register("Confirm", true);

    private final BooleanSetting crystalBreaker = register("Crystal Breaker", false);
    private final BooleanSetting fast = register("Fast", true);
    private final IntSetting packets = register("Packets", 1, 1, 5);

    private final BooleanSetting autoDisable = register("Auto Disable", false);

    //private final Queue<BlockPos> queue = new ConcurrentLinkedQueue<>();
    private int ticks;
    private int slot;
    private int oldSlot;
    private final List<BlockPos> unconfirmed = new Vector<>();
    private final List<BlockPos> surroundBlocks = new Vector<>();
    private double originalY;

    @Override
    public void onEnable() {
        if (nullCheck()) {
            disable();
            return;
        }

        MinecraftForge.EVENT_BUS.register(this);

        //queue.clear();
        ticks = 0;
        slot = -1;
        oldSlot = -1;
        unconfirmed.clear();
        surroundBlocks.clear();
        originalY = Math.floor(mc.player.posY);

        if (center.getValue()) {
            double x = Math.floor(mc.player.posX) + 0.5;
            double z = Math.floor(mc.player.posZ) + 0.5;
            mc.player.connection.sendPacket(new CPacketPlayer.Position(x, mc.player.posY, z, mc.player.onGround));
            mc.player.setPosition(x, mc.player.posY, z);
        }
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (nullCheck()) {
            return;
        }

        if (mc.player.posY - originalY > 0.4 || !mc.player.onGround) {
            disable();
            return;
        }

        slot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
        if (slot == -1) {
            return;
        }
        oldSlot = mc.player.inventory.currentItem;

        surroundBlocks.clear();
        surroundBlocks.addAll(getBlocks());
        placeBlocks(surroundBlocks);

        if (autoDisable.getValue()) {
            disable();
        }
    }

    @EventListener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketBlockChange) {
            SPacketBlockChange packet = event.getPacket();

            if (!surroundBlocks.contains(packet.getBlockPosition())) {
                return;
            }

            if (packet.getBlockState().getBlock() != Blocks.AIR) {
                return;
            }

            unconfirmed.add(packet.getBlockPosition());

            if (instant.getValue()) {
                event.setCancelled(true);
                mc.player.connection.handleBlockChange(packet);

                placeBlocks(Collections.singletonList(packet.getBlockPosition()));
            }
        }

        if (event.getPacket() instanceof SPacketMultiBlockChange) {
            SPacketMultiBlockChange packet = event.getPacket();

            List<BlockPos> toPlace = new ArrayList<>();

            for (SPacketMultiBlockChange.BlockUpdateData data : packet.getChangedBlocks()) {

                if (!surroundBlocks.contains(data.getPos())) {
                    continue;
                }

                if (data.getBlockState().getBlock() != Blocks.AIR) {
                    continue;
                }

                unconfirmed.add(data.getPos());
                toPlace.add(data.getPos());
            }

            if (instant.getValue()) {
                event.setCancelled(true);
                mc.player.connection.handleMultiBlockChange(packet);

                placeBlocks(toPlace);
            }
        }

        if (event.getPacket() instanceof SPacketDestroyEntities && instant.getValue()) {
            SPacketDestroyEntities packet = event.getPacket();

            for (int entityID : packet.getEntityIDs()) {
                Entity entity = mc.world.getEntityByID(entityID);
                if (!(entity instanceof EntityEnderCrystal)) {
                    continue;
                }

                if (mc.player.getDistance(entity) < 3) {
                    event.setCancelled(true);
                    mc.player.connection.handleDestroyEntities(packet);
                    placeBlocks(surroundBlocks);
                    return;
                }
            }
        }
    }

    private List<BlockPos> getBlocks() {
        if (extend.getValue()) {
            return getExtendingBlocks();
        }

        List<BlockPos> blocks = new ArrayList<>();
        BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);

        for (EnumFacing facing : EnumFacing.HORIZONTALS) {

            if (BlockUtil.getPossibleSides(playerPos.offset(facing)).isEmpty()) {
                blocks.add(playerPos.offset(facing).down());
            }

            blocks.add(playerPos.offset(facing));
        }

        return blocks.stream()
                .filter(pos -> mc.world.getBlockState(pos).getBlock().isReplaceable(mc.world, pos) || unconfirmed.contains(pos))
                .collect(Collectors.toList());
    }

    private List<BlockPos> getExtendingBlocks() {
        List<BlockPos> blocks = new ArrayList<>();
        List<BlockPos> offsets = Arrays.asList(new BlockPos[]{
                new BlockPos(mc.player.posX + 0.3, mc.player.posY, mc.player.posZ + 0.3),
                new BlockPos(mc.player.posX + 0.3, mc.player.posY, mc.player.posZ - 0.3),
                new BlockPos(mc.player.posX - 0.3, mc.player.posY, mc.player.posZ + 0.3),
                new BlockPos(mc.player.posX - 0.3, mc.player.posY, mc.player.posZ - 0.3),
        });
        for (BlockPos pos : offsets) {
            for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                blocks.add(pos.offset(facing));
            }
        }
        blocks.remove(offsets);
        List<BlockPos> theBlocks = new ArrayList<>(blocks);
        for (BlockPos pos : blocks) {
            theBlocks.add(0, pos);
        }
        return blocks.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private void placeBlocks(List<BlockPos> blocks) {

        if (blocks.isEmpty()) {
            return;
        }

        if (crystalBreaker.getValue()) {
            List<EntityEnderCrystal> crystals = new ArrayList<>();
            for (BlockPos pos : blocks) {
                crystals.addAll(mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(pos)));
            }

            breakCrystals(crystals);
        }

        for (BlockPos pos : blocks) {
            if (!hasEntities(pos, confirm.getValue())) {
                continue;
            }
            return;
        }

        swap(slot);

        for (BlockPos pos : blocks) {

            if (hasEntities(pos, confirm.getValue())) {
                continue;
            }

            if (rotate.getValue()) {
                float[] rots = calcAngle(mc.player.getPositionEyes(1f), new Vec3d(pos.add(0.5, 0.5, 0.5)));
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rots[0], rots[1], mc.player.onGround));
            }

            BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, packet.getValue(), false);

            if (rotate.getValue()) {
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround));
            }

            unconfirmed.remove(pos);
        }

        swap(oldSlot);
    }

    private boolean hasEntities(BlockPos pos, boolean crystals) {
        for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (entity instanceof EntityItem || entity instanceof EntityXPOrb || (entity instanceof EntityEnderCrystal && !crystals)) {
                continue;
            }
            return true;
        }
        return false;
    }

    private void breakCrystals(List<EntityEnderCrystal> crystals) {
        if (crystals.isEmpty()) {
            return;
        }

        breakCrystal(crystals.get(0));

        if (fast.getValue()) {
            for (EntityEnderCrystal c : crystals) {
                mc.world.removeEntity(c);
            }
        }
    }

    private void breakCrystal(EntityEnderCrystal crystal) {
        if (rotate.getValue()) {
            float[] rots = calcAngle(mc.player.getPositionEyes(1f), crystal.getPositionVector().add(0, 0.5, 0));
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rots[0], rots[1], mc.player.onGround));
        }

        for (int i = 0; i < packets.getValue(); i++) {
            CPacketUseEntity packet = new CPacketUseEntity();
            packet.action = CPacketUseEntity.Action.ATTACK;
            packet.entityId = crystal.entityId;
            mc.player.connection.sendPacket(packet);
        }

        if (rotate.getValue()) {
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround));
        }
    }

    private float[] calcAngle(Vec3d from, Vec3d to) {
        return new float[] {
                (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(to.z - from.z, to.x - from.x)) - 90.0), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2((to.y - from.y) * -1.0, MathHelper.sqrt(Math.pow(to.x - from.x, 2) + Math.pow(to.z - from.z, 2)))))
        };
    }

    private void swap(int slot) {
        mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        mc.player.inventory.currentItem = slot;
    }
}
