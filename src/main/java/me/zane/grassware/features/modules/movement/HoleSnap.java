package me.zane.grassware.features.modules.movement;

import java.util.Comparator;

import me.zane.grassware.GrassWare;
import me.zane.grassware.event.events.PacketEvent;
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.util.*;
import me.zane.grassware.util.Timer;
import me.zane.grassware.features.command.Command;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.Setting;
import me.zane.grassware.features.setting.impl.FloatSetting;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.features.setting.impl.IntSetting;
import me.zane.grassware.util.*;

import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HoleSnap
extends Module {
    /* synthetic */ HoleUtilSafety.Hole holes;
    private /* synthetic */ Setting<Boolean> motionstop;
    private final /* synthetic */ FloatSetting range = register("Range", 2.0f, 1.0f, 6.0f);
    private final /* synthetic */ FloatSetting range2 = register("Range2", 2.0f, 1.0f, 6.0f);
    /* synthetic */ Timer timer;
    private final /* synthetic */ Setting<Boolean> SpeedCheck;
    public /* synthetic */ FloatSetting timerfactor = register("TimeFactor", 2.0f, 1.0f, 10.0f);
    private /* synthetic */ IntSetting ticks = register("Ticks", 5.0f, 1.0f, 20.0f);
  //  public /* synthetic */ Setting<Mode> mode; "wtf is mode in holesnap??"

    @Override
    public void onTick() {
        BlockPos blockPos2;
        if (this.mode.getValue() == Mode.Instant) {
            blockPos2 = GrassWare.holeManager.calcHoles().stream().min(Comparator.comparing(blockPos -> HoleSnap.mc.player.getDistance((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()))).orElse(null);
            if (blockPos2 != null) {
                if (HoleSnap.mc.player.getDistance((double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ()) < (double)this.range.getValue().floatValue() + 1.5) {
                    HoleSnap.mc.player.setPosition((double)blockPos2.getX() + 0.5, (double)blockPos2.getY(), (double)blockPos2.getZ() + 0.5);
                    HoleSnap.mc.player.setPosition((double)blockPos2.getX() + 0.5, (double)blockPos2.getY(), (double)blockPos2.getZ() + 0.5);
                    Command.sendMessage("Accepting Teleport");
                } else {
                    Command.sendMessage("Out of range. disabling HoleSnap");
                }
            } else {
                Command.sendMessage("Unable to find hole, disabling HoleSnap");
            }
            this.disable();
        }
        if (this.mode.getValue() == Mode.Motion) {
            if (HoleSnap.fullNullCheck()) {
                return;
            }
            if (EntityUtil.isInLiquid()) {
                this.disable();
                return;
            }
            HoleSnap.mc.timer.tickLength = 50.0f / this.timerfactor.getValue().floatValue();
            this.holes = LuigiRotationUtil.getTargetHoleVec3D(this.range2.getValue().floatValue());
            if (this.holes == null) {
                Command.sendMessage("Unable to find hole, disabling HoleSnap");
                this.disable();
                return;
            }
            if (this.timer.passedMs(500L)) {
                this.disable();
                return;
            }
            if (HoleUtilSafety.isObbyHole(PlayerUtil.getPlayerPos()) || HoleUtilSafety.isBedrockHoles(PlayerUtil.getPlayerPos())) {
                this.disable();
                return;
            }
            if (HoleSnap.mc.world.getBlockState(this.holes.pos1).getBlock() != Blocks.AIR) {
                this.disable();
                return;
            }
            blockPos2 = this.holes.pos1;
            Vec3d vec3d = HoleSnap.mc.player.getPositionVector();
            Vec3d vec3d2 = new Vec3d((double)blockPos2.getX() + 0.5, HoleSnap.mc.player.posY, (double)blockPos2.getZ() + 0.5);
            double d = Math.toRadians(LuigiRotationUtil.getRotationTo((Vec3d)vec3d, (Vec3d)vec3d2).x);
            double d2 = vec3d.distanceTo(vec3d2);
            double d3 = HoleSnap.mc.player.onGround ? -Math.min(0.2805, d2 / 2.0) : -EntityUtil.getMaxSpeed() + 0.02;
            HoleSnap.mc.player.motionX = -Math.sin(d) * d3;
            HoleSnap.mc.player.motionZ = Math.cos(d) * d3;
        }
    }

    @Override
    public void onDisable() {
        this.timer.reset();
        this.holes = null;
        HoleSnap.mc.timer.tickLength = 50.0f;
    }

    @Override
    public void onEnable() {
        if (this.mode.getValue() == Mode.Motion && this.motionstop.getValue().booleanValue()) {
            HoleSnap.mc.player.motionX = 0.0;
            HoleSnap.mc.player.motionZ = 0.0;
        }
        if (this.SpeedCheck.getValue().booleanValue()) {
        }
        if (HoleSnap.fullNullCheck()) {
            return;
        }
        this.timer.reset();
        this.holes = null;
    }

    public HoleSnap() {
        super("HoleSnap", "Teleport to Hole", Module.Category.MOVEMENT, true, false, false);
        this.range = this.register(new Setting<Float>("Range", Float.valueOf(0.5f), Float.valueOf(0.1f), Float.valueOf(5.0f), f -> this.mode.getValue() == Mode.Instant));
        this.range2 = this.register(new Setting<Float>("Motion Range", Float.valueOf(4.0f), Float.valueOf(0.1f), Float.valueOf(10.0f), f -> this.mode.getValue() == Mode.Motion));
        this.mode = this.register(new Setting<Mode>("SnapMode", Mode.Motion));
        this.SpeedCheck = this.register(new Setting<Boolean>("Disable Speed", Boolean.valueOf(true), bl -> this.mode.getValue() == Mode.Motion));
        this.timerfactor = this.register(new Setting<Float>("Timer", Float.valueOf(2.0f), Float.valueOf(1.0f), Float.valueOf(5.0f), f -> this.mode.getValue() == Mode.Motion));
        this.motionstop = this.register(new Setting<Boolean>("StopMotion", Boolean.valueOf(true), bl -> this.mode.getValue() == Mode.Motion));
        this.timer = new Timer();
        this.ticks = 0;
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive receive) {
        if (this.isDisabled()) {
            return;
        }
        if (receive.getPacket() instanceof SPacketPlayerPosLook) {
            this.disable();
            return;
        }
    }

    public static enum Mode {
        Instant,
        Motion;

    }
}
