package me.zane.grassware.util;

public class Timer implements MC {
    private long time = -1L;

    public boolean passedMs(long ms) {
        return this.getMs(System.nanoTime() - this.time) >= ms;
    }

    public void sync() {
        this.time = System.nanoTime();
    }

    public long getMs(long time) {
        return time / 1000000L;
    }

    public long getTime(){
        return getMs(this.time);
    }
}

