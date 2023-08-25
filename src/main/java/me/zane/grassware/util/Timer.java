package me.zane.grassware.util;
//WARNING: ALL CONTENT BELONGS TO https://github.com/Zane2b2t , IF ANY OF THE CLASSES CONTAINING THIS WARNING ARENT IN https://github.com/Zane2b2t/Grassware.win-Rewrite INFORM GITHUB TO DMCA

public class Timer implements MC {
    private long time = -1L;
    private long current;

    public void sync() {
        this.time = System.nanoTime();
    }


    public boolean passedMs(long ms) {
        return this.passedNS(this.convertToNS(ms));
    }


    public boolean passedNS(long ns) {
        return System.nanoTime() - this.time >= ns;
    }


    public Timer reset() {
        this.time = System.nanoTime();
        return this;
    }
    public Timer() {
        current = -1;
    }

    public long convertToNS(long time) {
        return time * 1000000L;
    }

    public boolean passed(long delay) {
        return System.currentTimeMillis() - this.current >= delay;
    }
    public boolean passedF(float delay) {return System.currentTimeMillis() - this.current >= delay;}

}
