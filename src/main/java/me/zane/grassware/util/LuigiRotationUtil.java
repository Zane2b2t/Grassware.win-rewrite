package me.zane.grassware.util;

import java.util.Comparator;
import me.zane.grassware.util.*;
import me.zane.grassware.features.modules.client.ClickGui;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class LuigiRotationUtil
implements Util {
    private static /* synthetic */ float yaw;
    private static /* synthetic */ float pitch;

    public static float[] getAngle(Entity entity) {
        return MathUtil.calcAngle(LuigiRotationUtil.mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionEyes(mc.getRenderPartialTicks()));
    }

    public static void faceVectorPacketInstant(Vec3d vec3d) {
        float[] arrf = LuigiRotationUtil.getLegitRotations(vec3d);
        LuigiRotationUtil.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(arrf[0], arrf[1], LuigiRotationUtil.mc.player.onGround));
    }

    public static Vec3d getEyesPos() {
        return new Vec3d(LuigiRotationUtil.mc.player.posX, LuigiRotationUtil.mc.player.posY + (double)LuigiRotationUtil.mc.player.getEyeHeight(), LuigiRotationUtil.mc.player.posZ);
    }

    public static void restoreRotations() {
        LuigiRotationUtil.mc.player.rotationYaw = yaw;
        LuigiRotationUtil.mc.player.rotationYawHead = yaw;
        LuigiRotationUtil.mc.player.rotationPitch = pitch;
    }

    public static String getDirection4D(boolean bl) {
        int n = LuigiRotationUtil.getDirection4D();
        if (n == 0) {
            return "South (+Z)";
        }
        if (n == 1) {
            return "West (-X)";
        }
        if (n == 2) {
            return String.valueOf(new StringBuilder().append(bl ? "\u00a7c" : "").append("North (-Z)"));
        }
        if (n == 3) {
            return "East (+X)";
        }
        return "Loading...";
    }

    public static double yawDist(Entity entity) {
        if (entity != null) {
            Vec3d vec3d = entity.getPositionVector().add(0.0, (double)(entity.getEyeHeight() / 2.0f), 0.0).subtract(LuigiRotationUtil.mc.player.getPositionEyes(mc.getRenderPartialTicks()));
            double d = Math.abs((double)LuigiRotationUtil.mc.player.rotationYaw - (Math.toDegrees(Math.atan2(vec3d.z, vec3d.x)) - 90.0)) % 360.0;
            return d > 180.0 ? 360.0 - d : d;
        }
        return 0.0;
    }

    public static int getDirection4D() {
        return MathHelper.floor((double)((double)(LuigiRotationUtil.mc.player.rotationYaw * 4.0f / 360.0f) + 0.5)) & 3;
    }

    public static float[] getRotationsBlock(BlockPos blockPos, EnumFacing enumFacing, boolean bl) {
        double d = (double)blockPos.getX() + 0.5 - Wrapper.mc.player.posX + (double)enumFacing.getXOffset() / 2.0;
        double d2 = (double)blockPos.getZ() + 0.5 - Wrapper.mc.player.posZ + (double)enumFacing.getZOffset() / 2.0;
        double d3 = (double)blockPos.getY() + 0.5;
        if (bl) {
            d3 += 0.5;
        }
        double d4 = Wrapper.mc.player.posY + (double)Wrapper.mc.player.getEyeHeight() - d3;
        double d5 = MathHelper.sqrt((double)(d * d + d2 * d2));
        float f = (float)(Math.atan2(d2, d) * 180.0 / Math.PI) - 90.0f;
        float f2 = (float)(Math.atan2(d4, d5) * 180.0 / Math.PI);
        if (f < 0.0f) {
            f += 360.0f;
        }
        return new float[]{f, f2};
    }

    public static boolean isInFov(Entity entity) {
        return entity == null || !(LuigiRotationUtil.mc.player.getDistanceSq(entity) < 4.0) && !(LuigiRotationUtil.yawDist(entity) < (double)(LuigiRotationUtil.getHalvedfov() + 2.0f));
    }

    public static HoleUtilSafety.Hole getTargetHoleVec3D(double d) {
        return HoleUtilSafety.getHoles(d, LuigiRotationUtil.getPlayerPos(), false).stream().filter(hole -> LuigiRotationUtil.mc.player.getPositionVector().distanceTo(new Vec3d((double)hole.pos1.getX() + 0.5, LuigiRotationUtil.mc.player.posY, (double)hole.pos1.getZ() + 0.5)) <= d).min(Comparator.comparingDouble(hole -> LuigiRotationUtil.mc.player.getPositionVector().distanceTo(new Vec3d((double)hole.pos1.getX() + 0.5, LuigiRotationUtil.mc.player.posY, (double)hole.pos1.getZ() + 0.5)))).orElse(null);
    }

    public static void faceEntity(Entity entity) {
        float[] arrf = MathUtil.calcAngle(LuigiRotationUtil.mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionEyes(mc.getRenderPartialTicks()));
        LuigiRotationUtil.faceYawAndPitch(arrf[0], arrf[1]);
    }

    public static float getHalvedfov() {
        return LuigiRotationUtil.getFov() / 2.0f;
    }

    public static BlockPos getPlayerPos() {
        double d = LuigiRotationUtil.mc.player.posY - Math.floor(LuigiRotationUtil.mc.player.posY);
        return new BlockPos(LuigiRotationUtil.mc.player.posX, d > 0.8 ? Math.floor(LuigiRotationUtil.mc.player.posY) + 1.0 : Math.floor(LuigiRotationUtil.mc.player.posY), LuigiRotationUtil.mc.player.posZ);
    }

    public static boolean isInFov(Vec3d vec3d, Vec3d vec3d2) {
        if (LuigiRotationUtil.mc.player.rotationPitch > 30.0f ? vec3d2.y > LuigiRotationUtil.mc.player.posY : LuigiRotationUtil.mc.player.rotationPitch < -30.0f && vec3d2.y < LuigiRotationUtil.mc.player.posY) {
            return true;
        }
        float f = MathUtil.calcAngleNoY(vec3d, vec3d2)[0] - LuigiRotationUtil.transformYaw();
        if (f < -270.0f) {
            return true;
        }
        float f2 = (ClickGui.getInstance().customFov.getValue() != false ? ClickGui.getInstance().fov.getValue().floatValue() : LuigiRotationUtil.mc.gameSettings.fovSetting) / 2.0f;
        return f < f2 + 10.0f && f > -f2 - 10.0f;
    }

    public static void faceVector(Vec3d vec3d, boolean bl) {
        float[] arrf = LuigiRotationUtil.getLegitRotations(vec3d);
        LuigiRotationUtil.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(arrf[0], bl ? (float)MathHelper.normalizeAngle((int)((int)arrf[1]), (int)360) : arrf[1], LuigiRotationUtil.mc.player.onGround));
    }

    public static double normalizeAngle(Double d) {
        double d2 = 0.0;
        double d3 = d;
        d3 %= 360.0;
        if (d2 >= 180.0) {
            d3 -= 360.0;
        }
        if (d3 < -180.0) {
            d3 += 360.0;
        }
        return d3;
    }

    public static void faceYawAndPitch(float f, float f2) {
        LuigiRotationUtil.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(f, f2, LuigiRotationUtil.mc.player.onGround));
    }

    public static Vec2f getRotationFromVec(Vec3d vec3d) {
        double d = Math.hypot(vec3d.x, vec3d.z);
        float f = (float)LuigiRotationUtil.normalizeAngle(Math.toDegrees(Math.atan2(vec3d.z, vec3d.x)) - 90.0);
        float f2 = (float)LuigiRotationUtil.normalizeAngle(Math.toDegrees(-Math.atan2(vec3d.y, d)));
        return new Vec2f(f, f2);
    }

    public static void updateRotations() {
        yaw = LuigiRotationUtil.mc.player.rotationYaw;
        pitch = LuigiRotationUtil.mc.player.rotationPitch;
    }

    public static float getFov() {
        return ClickGui.getInstance().customFov.getValue() != false ? ClickGui.getInstance().fov.getValue().floatValue() : LuigiRotationUtil.mc.gameSettings.fovSetting;
    }

    public static void setPlayerRotations(float f, float f2) {
        LuigiRotationUtil.mc.player.rotationYaw = f;
        LuigiRotationUtil.mc.player.rotationYawHead = f;
        LuigiRotationUtil.mc.player.rotationPitch = f2;
    }

    public static boolean isInFov(BlockPos blockPos) {
        return blockPos == null || !(LuigiRotationUtil.mc.player.getDistanceSq(blockPos) < 4.0) && !(LuigiRotationUtil.yawDist(blockPos) < (double)(LuigiRotationUtil.getHalvedfov() + 2.0f));
    }

    public static double yawDist(BlockPos blockPos) {
        if (blockPos != null) {
            Vec3d vec3d = new Vec3d((Vec3i)blockPos).subtract(LuigiRotationUtil.mc.player.getPositionEyes(mc.getRenderPartialTicks()));
            double d = Math.abs((double)LuigiRotationUtil.mc.player.rotationYaw - (Math.toDegrees(Math.atan2(vec3d.z, vec3d.x)) - 90.0)) % 360.0;
            return d > 180.0 ? 360.0 - d : d;
        }
        return 0.0;
    }

    public static float[] getRotations(Vec3d vec3d, Vec3d vec3d2) {
        double d = vec3d2.x - vec3d.x;
        double d2 = (vec3d2.y - vec3d.y) * -1.0;
        double d3 = vec3d2.z - vec3d.z;
        double d4 = MathHelper.sqrt((double)(d * d + d3 * d3));
        return new float[]{(float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(d3, d)) - 90.0)), (float)MathHelper.wrapDegrees((double)Math.toDegrees(Math.atan2(d2, d4)))};
    }

    public static float transformYaw() {
        float f = LuigiRotationUtil.mc.player.rotationYaw % 360.0f;
        if (LuigiRotationUtil.mc.player.rotationYaw > 0.0f) {
            if (f > 180.0f) {
                f = -180.0f + (f - 180.0f);
            }
        } else if (f < -180.0f) {
            f = 180.0f + (f + 180.0f);
        }
        if (f < 0.0f) {
            return 180.0f + f;
        }
        return -180.0f + f;
    }

    public static float[] simpleFacing(EnumFacing enumFacing) {
        switch (enumFacing) {
            case DOWN: {
                return new float[]{LuigiRotationUtil.mc.player.rotationYaw, 90.0f};
            }
            case UP: {
                return new float[]{LuigiRotationUtil.mc.player.rotationYaw, -90.0f};
            }
            case NORTH: {
                return new float[]{180.0f, 0.0f};
            }
            case SOUTH: {
                return new float[]{0.0f, 0.0f};
            }
            case WEST: {
                return new float[]{90.0f, 0.0f};
            }
        }
        return new float[]{270.0f, 0.0f};
    }

    public static float[] getLegitRotations(Vec3d vec3d) {
        Vec3d vec3d2 = LuigiRotationUtil.getEyesPos();
        double d = vec3d.x - vec3d2.x;
        double d2 = vec3d.y - vec3d2.y;
        double d3 = vec3d.z - vec3d2.z;
        double d4 = Math.sqrt(d * d + d3 * d3);
        float f = (float)Math.toDegrees(Math.atan2(d3, d)) - 90.0f;
        float f2 = (float)(-Math.toDegrees(Math.atan2(d2, d4)));
        return new float[]{LuigiRotationUtil.mc.player.rotationYaw + MathHelper.wrapDegrees((float)(f - LuigiRotationUtil.mc.player.rotationYaw)), LuigiRotationUtil.mc.player.rotationPitch + MathHelper.wrapDegrees((float)(f2 - LuigiRotationUtil.mc.player.rotationPitch))};
    }

    public static Vec2f getRotationTo(Vec3d vec3d, Vec3d vec3d2) {
        return LuigiRotationUtil.getRotationFromVec(vec3d.subtract(vec3d2));
    }
}
