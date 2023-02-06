package me.zane.grassware.util;

import net.minecraft.util.math.MathHelper;

public class Timer implements MC {
    private long time = -1L;
    private long current;
    private long lastMS = 0L;
    
    
    public void sync() {
        this.time = System.nanoTime();
    }

    public boolean passedS(double s) {
        return this.passedMs((long)s * 1000L);
    }

    public boolean passedDms(double dms) {
        return this.passedMs((long)dms * 10L);
    }

    public boolean passedDs(double ds) {
        return this.passedMs((long)ds * 100L);
    }

    public boolean passedMs(long ms) {
        return this.passedNS(this.convertToNS(ms));
    }

    public void setMs(long ms) {
        this.time = System.nanoTime() - this.convertToNS(ms);
    }

    public boolean passedNS(long ns) {
        return System.nanoTime() - this.time >= ns;
    }

    public long getPassedTimeMs() {
        return this.getMs(System.nanoTime() - this.time);
    }

    public Timer reset() {
        this.time = System.nanoTime();
        return this;
    }

    public long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }

    public boolean delay(double var1) {
        return (double)MathHelper.clamp((float)(this.getCurrentMS() - this.lastMS), (float)0.0f, (float)((float)var1)) >= var1;
    }

    public boolean hasReached(long passedTime) {
        return System.currentTimeMillis() - this.current >= passedTime;
    }

    public boolean passed(double ms) {
        return (double)(System.currentTimeMillis() - this.current) >= ms;
    }

    public long getMs(long time) {
        return time / 1000000L;
    }

    public long convertToNS(long time) {
        return time * 1000000L;
    }

    public boolean passed(long delay) {
        return System.currentTimeMillis() - this.current >= delay;
    }
}
