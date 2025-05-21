package me.zane.grassware.features.modules.movement;

import me.zane.grassware.GrassWare;
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.PacketEvent;
import me.zane.grassware.event.events.TickEvent;
import me.zane.grassware.manager.HoleManager;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.features.setting.impl.FloatSetting;
import me.zane.grassware.util.BlockUtil;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.Comparator;
// this garbage works but also not at the same time

public class HoleSnap extends Module {
    private final FloatSetting boundingBoxSize = register("Bounding Box Size", 1.0f, 0.1f, 10.0f);
    private final BooleanSetting timeoutOnBlockPlacement = register("Timeout On Block Placement", true);
    private final BooleanSetting down = register("Down", true);
    private final FloatSetting motionY = register("Motion Y", 1.0f, 0.1f, 5.0f);
    private final FloatSetting motionXZ = register("Motion XZ", 0.2f, 0.05f, 1.0f); // New setting for horizontal speed
    private long sys;

    @EventListener
    public void onTick(final TickEvent event) {
        if (BlockUtil.isPlayerSafe(mc.player)) {
            sys = System.currentTimeMillis();
        }
        if (System.currentTimeMillis() - sys < 1000) {
            return;
        }
        if (!mc.player.onGround) {
            return;
        }
        HoleManager.HolePos closest = GrassWare.holeManager.getHoles().stream()
                .min(Comparator.comparingDouble(hole -> Math.sqrt(mc.player.getDistanceSq(BlockUtil.center(hole.getPos())))))
                .orElse(null);
        if (closest == null) {
            return;
        }
        BlockPos pos = BlockUtil.center(closest.getPos()).add(0.0f, 1.0f, 0.0f);
        AxisAlignedBB bb = new AxisAlignedBB(pos).shrink(0.1f * (boundingBoxSize.max - (boundingBoxSize.getValue() + 5.0f)));

        if (mc.player.getEntityBoundingBox().intersects(bb)) {
            mc.player.setPosition(pos.getX() + 0.5f, mc.player.posY, pos.getZ() + 0.5f);
            if (down.getValue()) {
                mc.player.motionY = -motionY.getValue();
            }
            sys = System.currentTimeMillis();
        } else {
            double dx = (pos.getX() + 0.5f) - mc.player.posX;
            double dz = (pos.getZ() + 0.5f) - mc.player.posZ;
            double distance = Math.sqrt(dx * dx + dz * dz);

            if (distance > 0.1) {
                double speed = motionXZ.getValue();
                mc.player.motionX = (dx / distance) * speed;
                mc.player.motionZ = (dz / distance) * speed;
            } else {
                // if close enough, snap to center
                mc.player.setPosition(pos.getX() + 0.5f, mc.player.posY, pos.getZ() + 0.5f);
            }
        }
    }

    @EventListener
    public void onPacketSend(final PacketEvent event) {
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && timeoutOnBlockPlacement.getValue()) {
            sys = System.currentTimeMillis();
        }
    }
}
