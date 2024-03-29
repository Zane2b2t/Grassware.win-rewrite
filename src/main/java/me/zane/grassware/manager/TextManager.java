package me.zane.grassware.manager;

import me.zane.grassware.features.Feature;

import java.awt.*;

public class TextManager extends Feature {
    private final int height = mc.fontRenderer.FONT_HEIGHT;

    public void renderString(String text, float x, float y, Color color) {
        renderString(text, x + 0.5f, y + 0.5f, new Color(0, 0, 0, 150), false);
        renderString(text, x, y, color, false);
    }

    public void renderStringShadowOnly(String text, float x, float y) {
        renderString(text, x + 0.5f, y + 0.5f, new Color(0, 0, 0, 0), false);
    }

    public void renderStringNoShadow(String text, float x, float y, Color color) {
        mc.fontRenderer.drawString(text, x, y, color.getRGB(), false);
    }

    public void renderString(String text, float x, float y, Color color, boolean shadow) {
        mc.fontRenderer.drawString(text, x, y, color.getRGB(), shadow);
    }

    public int stringWidth(final String text) {
        return TextManager.mc.fontRenderer.getStringWidth(text);
    }

    public int stringHeight() {
        return height;
    }
}

