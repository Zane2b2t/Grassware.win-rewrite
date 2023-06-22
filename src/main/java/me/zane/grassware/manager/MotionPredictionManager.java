package me.zane.grassware.manager;

import me.zane.grassware.util.MC;
import me.zane.grassware.util.BlockUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;


public class MotionPredictionManager implements MC {

    public Vec3d getPredictedPosByPlayer(EntityPlayer entityPlayer, int amount) {
        Vec3d vec = new Vec3d(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ);
        Vec3d prev = null;
        for (int i = 1; i < amount; i++) {
            Vec3d vec1 = vec.add(entityPlayer.motionX * i, 0, entityPlayer.motionZ * i);
            if (!BlockUtil.isReplaceable(new BlockPos(vec1.x, vec1.y, vec1.z))) {
                break;
            } else {
                prev = vec1;
            }
        }
        return prev == null ? vec : prev;
    }

}