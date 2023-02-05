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
    
    public boolean hasReached(long delay) {
        return System.currentTimeMillis() - this.current >= delay;
    }
    
        public boolean hasReached(long delay, boolean reset) {
        if (reset)
            reset();
        return System.currentTimeMillis() - this.current >= delay;
    }
    
     public final void reset() {
        this.current = System.currentTimeMillis();
    }
    

    public long getTime() {
        return getMs(this.time);
    }
}

