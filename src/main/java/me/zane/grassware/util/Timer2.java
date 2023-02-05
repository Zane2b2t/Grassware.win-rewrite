package me.zane.grassware.util;

public class Timer {

    private long current;

    public Timer() {
        current = -1;
    }

    public final boolean hasReached(final long delay) {
        return System.currentTimeMillis() - this.current >= delay;
    }

    public boolean hasReached(final long delay, boolean reset) {
        if (reset)
            reset();
        return System.currentTimeMillis() - this.current >= delay;
    }

    public final void reset() {
        this.current = System.currentTimeMillis();
    }

    public long time() {
        return System.nanoTime() / 1000000L - current;
    }

}
