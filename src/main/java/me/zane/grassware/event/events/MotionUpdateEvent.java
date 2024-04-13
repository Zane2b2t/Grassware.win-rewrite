package me.zane.grassware.event.events;

import me.zane.grassware.event.bus.Event;

public class MotionUpdateEvent extends Event {
    private double x, y,  z;
    private  double prevX, prevY, prevZ;
    private float rotationYaw;
    private float rotationPitch;
    private  float prevYaw, prevPitch;
    private boolean onGround;
    private  boolean prevOnGround;

    private float yaw;
    private float pitch;

    public MotionUpdateEvent(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}