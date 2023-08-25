package me.zane.grassware.util;
//WARNING: ALL CONTENT BELONGS TO https://github.com/Zane2b2t , IF ANY OF THE CLASSES CONTAINING THIS WARNING ARENT IN https://github.com/Zane2b2t/Grassware.win-Rewrite INFORM GITHUB TO DMCA
public class MathUtil implements MC {


    public static double round(final double value, final int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    public static float clamp(float num, float min, float max) {
        if (num < min) {
            return min;
        } else {
            return Math.min(num, max);
        }
    }

    public static float lerp(float current, float target, float lerp) {
        current -= (current - target) * clamp(lerp, 0, 1);
        return current;
    }
}
