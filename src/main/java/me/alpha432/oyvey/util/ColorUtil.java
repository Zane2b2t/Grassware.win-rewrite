package me.alpha432.oyvey.util;

import me.alpha432.oyvey.features.modules.client.ClickGui;

import java.awt.*;

public class ColorUtil {
    public static Color invert(Color c) {
        //  thanks! stackoverflow
        int a = c.getAlpha();
        int r = 255 - c.getRed();
        int g = 255 - c.getGreen();
        int b = 255 - c.getBlue();

        // if the resulting color is to light (e.g. initial color is black, resulting color is white...)
        if ((r + g + b > 740) || (r + g + b < 20)) {
            // return a standard yellow
            return new Color(255, 255, 40, a);
        } else {
            return new Color(r, g, b, a);
        }
    }
    public static int toARGB(int r, int g, int b, int a) {
        return new Color(r, g, b, a).getRGB();
    }

    public static int toRGBA(int r, int g, int b) {
        return ColorUtil.toRGBA(r, g, b, 255);
    }

    public static int toRGBA(int r, int g, int b, int a) {
        return (r << 16) + (g << 8) + b + (a << 24);
    }

    public static int toRGBA(float r, float g, float b, float a) {
        return ColorUtil.toRGBA((int) (r * 255.0f), (int) (g * 255.0f), (int) (b * 255.0f), (int) (a * 255.0f));
    }

    public static Color rainbow(int delay) {
        double rainbowState = Math.ceil((double) (System.currentTimeMillis() + (long) delay) / 20.0);
        return Color.getHSBColor((float) (rainbowState % 360.0 / 360.0), ClickGui.getInstance().rainbowSaturation.getValue() / 255.0f, ClickGui.getInstance().rainbowBrightness.getValue() / 255.0f);
    }


    public static int toRGBA(float[] colors) {
        if (colors.length != 4) {
            throw new IllegalArgumentException("colors[] must have a length of 4!");
        }
        return ColorUtil.toRGBA(colors[0], colors[1], colors[2], colors[3]);
    }

    public static int toRGBA(double[] colors) {
        if (colors.length != 4) {
            throw new IllegalArgumentException("colors[] must have a length of 4!");
        }
        return ColorUtil.toRGBA((float) colors[0], (float) colors[1], (float) colors[2], (float) colors[3]);
    }

    public static int toRGBA(Color color) {
        return ColorUtil.toRGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static
    class Colors {
        public static final int WHITE = ColorUtil.toRGBA(255, 255, 255, 255);
        public static final int BLACK = ColorUtil.toRGBA(0, 0, 0, 255);
        public static final int RED = ColorUtil.toRGBA(255, 0, 0, 255);
        public static final int GREEN = ColorUtil.toRGBA(0, 255, 0, 255);
        public static final int BLUE = ColorUtil.toRGBA(0, 0, 255, 255);
        public static final int ORANGE = ColorUtil.toRGBA(255, 128, 0, 255);
        public static final int PURPLE = ColorUtil.toRGBA(163, 73, 163, 255);
        public static final int GRAY = ColorUtil.toRGBA(127, 127, 127, 255);
        public static final int DARK_RED = ColorUtil.toRGBA(64, 0, 0, 255);
        public static final int YELLOW = ColorUtil.toRGBA(255, 255, 0, 255);
        public static final int RAINBOW = Integer.MIN_VALUE;
    }
}
