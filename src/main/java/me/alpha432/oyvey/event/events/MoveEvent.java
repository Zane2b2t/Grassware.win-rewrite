package me.alpha432.oyvey.event.events;

import me.alpha432.oyvey.event.bus.Event;
import net.minecraft.entity.MoverType;

public class MoveEvent extends Event {
    public double motionX;
    public double motionY;
    public double motionZ;
    public MoverType type;

    public MoveEvent(final MoverType type, final double x, final double y, final double z) {
        this.type = type;
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
    }

    public void setMotion(double x, double y, double z) {
        motionX = x;
        motionY = y;
        motionZ = z;
    }

    public void setMotionX(double motionX) {
        this.motionX = motionX;
    }

    public void setMotionY(double motionY) {
        this.motionY = motionY;
    }

    public void setMotionZ(double motionZ) {
        this.motionZ = motionZ;
    }
}