package me.zane.grassware.util;

import me.zane.grassware.util.Timer;

public class Timer2 {

    private long current;

    public Timer2() {
        current = -1;
      return Timer;
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
