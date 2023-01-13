package me.zane.grassware.manager;

import net.minecraft.client.Minecraft;

public class RotationManager {

    private static final RotationManager INSTANCE = new RotationManager();

    public static RotationManager getInstance() {
        return INSTANCE;
    }

    private final Minecraft mc = Minecraft.getMinecraft();

    private float yaw, pitch;
    private boolean rotated;
    private int ticksSinceNoRotate;

    public void updateRotations() {
        yaw = mc.player.rotationYaw;
        pitch = mc.player.rotationPitch;
    }

    public void restoreRotations() {
        ticksSinceNoRotate++;
        if (ticksSinceNoRotate > 2) {
            rotated = false;
        }
        mc.player.rotationYaw = yaw;
        mc.player.rotationYawHead = yaw;
        mc.player.rotationPitch = pitch;
    }

    public void setPlayerRotations(float yaw, float pitch) {
        rotated = true;
        ticksSinceNoRotate = 0;
        mc.player.rotationYaw = yaw;
        mc.player.rotationYawHead = yaw;
        mc.player.rotationPitch = pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public boolean isRotated() {
        return rotated;
    }

}
