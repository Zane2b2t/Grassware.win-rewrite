package me.zane.grassware.util;

import me.zane.grassware.manager.RotationManager;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RotationUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static float[] getRotations(BlockPos pos, EnumFacing facing) {
        AxisAlignedBB bb = mc.world.getBlockState(pos).getBoundingBox(mc.world, pos);
        double x = pos.getX() + (bb.minX + bb.maxX) / 2.0;
        double y = pos.getY() + (bb.minY + bb.maxY) / 2.0;
        double z = pos.getZ() + (bb.minZ + bb.maxZ) / 2.0;

        if (facing != null) {
            x += facing.getDirectionVec().getX() * ((bb.minX + bb.maxX) / 2.0);
            y += facing.getDirectionVec().getY() * ((bb.minY + bb.maxY) / 2.0);
            z += facing.getDirectionVec().getZ() * ((bb.minZ + bb.maxZ) / 2.0);
        }

        return getRotations(x, y, z);
    }

    public static float[] getRotations(BlockPos pos) {
        return getRotations(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
    }

    public static float[] getRotations(double x, double y, double z) {
        double xDiff = x - mc.player.posX;
        double yDiff = y - PositionUtil.getEyeHeight(mc.player);
        double zDiff = z - mc.player.posZ;
        double dist = MathHelper.sqrt(xDiff * xDiff + zDiff * zDiff);

        float yaw = (float)(Math.atan2(zDiff, xDiff) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-(Math.atan2(yDiff, dist) * 180.0 / Math.PI));
        float diff = yaw - mc.player.rotationYaw;

        if (diff < -180.0f || diff > 180.0f) {
            float round = Math.round(Math.abs(diff / 360.0f));
            diff = diff < 0.0f ? diff + 360.0f * round : diff - (360.0f * round);
        }

        return new float[] {
                mc.player.rotationYaw + diff, pitch
        };
    }

    public static Vec3d getVec3d(float yaw, float pitch) {
        float vx = -MathHelper.sin(MathUtil.rad(yaw)) * MathHelper.cos(MathUtil.rad(pitch));
        float vz = MathHelper.cos(MathUtil.rad(yaw)) * MathHelper.cos(MathUtil.rad(pitch));
        float vy = -MathHelper.sin(MathUtil.rad(pitch));
        return new Vec3d(vx, vy, vz);
    }

    public enum RotationType {
        PACKET {
            @Override
            public void doRotation(float[] angles) {
                mc.getConnection().sendPacket(new CPacketPlayer.Rotation(angles[0], angles[1], mc.player.onGround));
            }
        },
        NORMAL{
            @Override
            public void doRotation(float[] angles) {
                RotationManager.getInstance().setPlayerRotations(angles[0], angles[1]);
            }
        },
        NONE {
            @Override
            public void doRotation(float[] angles) {

            }
        };

        public abstract void doRotation(float[] angles);
    }

}
