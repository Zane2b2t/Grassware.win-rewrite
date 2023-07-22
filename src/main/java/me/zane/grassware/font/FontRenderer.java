package me.zane.grassware.font;

import com.mojang.realmsclient.gui.ChatFormatting;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class FontRenderer {
    protected final BitmapRenderer defaultFont;
    protected final int FONT_HEIGHT;

    public FontRenderer(Font font) {
        defaultFont = new BitmapRenderer(font);
        FONT_HEIGHT = (int) getHeight();
    }

    public float getHeight() {
        return defaultFont.getHeight() / 2f;
    }


    public void drawStringWithShadow(String text, float x, float y, int color) {
        drawString(text, x, y, color, true);
    }

    public void drawString(String text, float x, float y, int color, boolean dropShadow) {
        y -= 3.0f;
        final String symbol = String.valueOf(ChatFormatting.PREFIX_CODE);
        if (text.contains(symbol)) {
            final String[] split = text.split(symbol);
            float deltaX = x;
            for (int i = 0; i < split.length; i++) {
                final String string = split[i];
                int color1 = color;
                if (i != 0) {
                    color1 = getColorByMinecraftColor(String.valueOf(string.charAt(0)), color);
                }
                StringBuilder newString = new StringBuilder();
                char[] charArray = string.toCharArray();
                for (int j = 0; j < charArray.length; j++) {
                    final Character character = charArray[j];
                    if (i == 0 || j != 0) {
                        newString.append(character);
                    }
                }
                final String builtString = String.valueOf(newString);
                if (dropShadow)
                    drawText(builtString, deltaX + 0.5f, y + 0.5f, new Color(0, 0, 0, 150).getRGB());
                drawText(builtString, deltaX, y, color1);
                deltaX += getStringWidth(builtString);
            }
        } else {
            if (dropShadow) {
                drawText(text, Integer.MAX_VALUE, Integer.MAX_VALUE, new Color(0, 0, 0, 0).getRGB());
                drawText(text, x + 0.5f, y + 0.5f, new Color(0, 0, 0, 150).getRGB());
            }
            drawText(text, x, y, color);
        }
    }

    private void drawText(String text, float x, float y, int color) {
        if (text == null) {
            return;
        }

        if (text.isEmpty()) {
            return;
        }

        glPushMatrix();
        glTranslated((double) x - 1.5, (double) y + 0.5, 0.0);
        glEnable(GL_ALPHA_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);

        glEnable(GL_LINE_SMOOTH);

        int currentColor = color;

        if ((currentColor & 0xFC000000) == 0) currentColor |= 0xFF000000;

        defaultFont.drawString(text, 0.0, 0.0, currentColor);

        glDisable(GL_LINE_SMOOTH);

        glDisable(GL_BLEND);
        glTranslated(-((double) x - 1.5), -((double) y + 0.5), 0.0);
        glEnable(GL_TEXTURE_2D);

        glPopMatrix();
    }

    public int getStringWidth(String text) {
        return defaultFont.getStringWidth(text) / 2;
    }

    public int getColorByMinecraftColor(final String string, final int color) {
        switch (string) {
            case "0":
                return new Color(0).getRGB();
            case "1":
                return new Color(0x0000AA).getRGB();
            case "2":
                return new Color(0x00AA00).getRGB();
            case "3":
                return new Color(0x00AAAA).getRGB();
            case "4":
                return new Color(0xAA0000).getRGB();
            case "5":
                return new Color(0xAA00AA).getRGB();
            case "6":
                return new Color(0xFFAA00).getRGB();
            case "7":
                return new Color(0xAAAAAA).getRGB();
            case "8":
                return new Color(0x555555).getRGB();
            case "9":
                return new Color(0x5555FF).getRGB();
            case "a":
                return new Color(0x55FF55).getRGB();
            case "b":
                return new Color(0x55FFFF).getRGB();
            case "c":
                return new Color(0xFF5555).getRGB();
            case "d":
                return new Color(0xFF55FF).getRGB();
            case "e":
                return new Color(0xFFFF55).getRGB();
            case "f":
                return new Color(0xFFFFFF).getRGB();
            case "r":
                return color;
        }
        return Color.WHITE.getRGB();
    }
}