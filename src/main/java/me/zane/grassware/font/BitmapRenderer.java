package me.zane.grassware.font;

import me.zane.grassware.util.MC;
import net.minecraft.client.renderer.texture.TextureUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;

@SuppressWarnings("unused")
public class BitmapRenderer implements MC {
    private static final ArrayList<BitmapRenderer> activeFontRenderers = new ArrayList<>();
    private static int gcTicks = 0;
    private final Font font;
    private int fontHeight = -1;
    private final CharLocation[] charLocations;
    private final HashMap<String, CustomFont> cachedStrings = new HashMap<>();
    private int textureID = 0;
    private int textureWidth = 0;
    private int textureHeight = 0;

    public static void garbageCollectionTick() {
        if (gcTicks++ > 600) {
            activeFontRenderers.forEach(BitmapRenderer::collectGarbage);
            gcTicks = 0;
        }
    }

    public BitmapRenderer(Font font, int startChar, int stopChar) {
        this.font = font;
        charLocations = new CharLocation[stopChar];
        renderBitmap(startChar, stopChar);
        activeFontRenderers.add(this);
    }

    public BitmapRenderer(Font font) {
        this(font, 0, 255);
    }

    private void collectGarbage() {
        long currentTime = System.currentTimeMillis();
        cachedStrings.entrySet().stream().filter(entry -> currentTime - (entry.getValue()).getLastUsage() > 30000L).forEach(entry -> {
            glDeleteLists((entry.getValue()).getDisplayList(), 1);
            cachedStrings.remove(entry.getKey());
        });
    }

    public float getHeight() {
        return (fontHeight - 8f) / 2f;
    }

    public void drawString(String text, double x, double y, int color) {
        glScalef(0.25f, 0.25f, 0.25f);
        glTranslated(x * 2.0, y * 2.0 - 2.0, 0.0);
        glBindTexture(GL_TEXTURE_2D, textureID);
        float red = (float) (color >> 16 & 0xFF) / 255.0f;
        float green = (float) (color >> 8 & 0xFF) / 255.0f;
        float blue = (float) (color & 0xFF) / 255.0f;
        float alpha = (float) (color >> 24 & 0xFF) / 255.0f;
        glColor4f(red, green, blue, alpha);
        double currX = 0.0;
        CustomFont cached = cachedStrings.get(text);

        if (cached != null) {
            glCallList(cached.getDisplayList());
            cached.setLastUsage(System.currentTimeMillis());
            glPopMatrix();
            return;
        }

        int list = -1;
        boolean assumeNonVolatile = false;

        glBegin(7);
        for (char ch : text.toCharArray()) {
            CharLocation fontChar;
            if (charLocations.length <= ch || (fontChar = charLocations[ch]) == null) {
                continue;
            }

            drawChar(fontChar, (float) currX);
            currX += (double) fontChar.width - 8.0;
        }


        glEnd();

    }

    private void drawChar(CharLocation ch, float x) {
        float width = ch.width;
        float height = ch.height;
        float srcX = ch.x;
        float srcY = ch.y;
        float renderX = srcX / (float) textureWidth;
        float renderY = srcY / (float) textureHeight;
        float renderWidth = width / (float) textureWidth;
        float renderHeight = height / (float) textureHeight;
        glTexCoord2f(renderX, renderY);
        glVertex2f(x, (float) 0.0);
        glTexCoord2f(renderX, renderY + renderHeight);
        glVertex2f(x, (float) 0.0 + height);
        glTexCoord2f(renderX + renderWidth, renderY + renderHeight);
        glVertex2f(x + width, (float) 0.0 + height);
        glTexCoord2f(renderX + renderWidth, renderY);
        glVertex2f(x + width, (float) 0.0);
    }

    private void renderBitmap(int startChar, int stopChar) {
        BufferedImage[] fontImages = new BufferedImage[stopChar];
        int rowHeight = 0;
        int charX = 0;
        int charY = 0;

        for (int targetChar = startChar; targetChar < stopChar; ++targetChar) {
            BufferedImage fontImage = drawCharToImage((char) targetChar);
            CharLocation fontChar = new CharLocation(charX, charY, fontImage.getWidth(), fontImage.getHeight());

            if (fontChar.height > fontHeight)
                fontHeight = fontChar.height;

            if (fontChar.height > rowHeight)
                rowHeight = fontChar.height;

            if (charLocations.length <= targetChar)
                continue;

            charLocations[targetChar] = fontChar;
            fontImages[targetChar] = fontImage;

            if ((charX += fontChar.width) <= 2048)
                continue;

            if (charX > textureWidth)
                textureWidth = charX;

            charX = 0;
            charY += rowHeight;
            rowHeight = 0;
        }

        textureHeight = charY + rowHeight;
        BufferedImage bufferedImage = new BufferedImage(textureWidth, textureHeight, 2);
        Graphics2D graphics2D = (Graphics2D) bufferedImage.getGraphics();
        graphics2D.setFont(font);
        graphics2D.setColor(new Color(255, 255, 255, 0));
        graphics2D.fillRect(0, 0, textureWidth, textureHeight);
        graphics2D.setColor(Color.WHITE);

        for (int targetChar = startChar; targetChar < stopChar; ++targetChar) {
            if (fontImages[targetChar] == null || charLocations[targetChar] == null)
                continue;

            graphics2D.drawImage(fontImages[targetChar], charLocations[targetChar].x, charLocations[targetChar].y, null);
        }

        textureID = TextureUtil.uploadTextureImageAllocate(TextureUtil.glGenTextures(), bufferedImage, true, true);
    }

    private BufferedImage drawCharToImage(char ch) {
        int charHeight;
        Graphics2D graphics2D = (Graphics2D) new BufferedImage(1, 1, 2).getGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics2D.setFont(font);
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        int charWidth = fontMetrics.charWidth(ch) + 8;

        if (charWidth <= 8)
            charWidth = 7;

        if ((charHeight = fontMetrics.getHeight() + 3) <= 0)
            charHeight = font.getSize();

        BufferedImage fontImage = new BufferedImage(charWidth, charHeight, 2);
        Graphics2D graphics = (Graphics2D) fontImage.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setFont(font);
        graphics.setColor(Color.WHITE);
        graphics.drawString(String.valueOf(ch), 3, 1 + fontMetrics.getAscent());
        return fontImage;
    }

    public int getStringWidth(String text) {
        int width = 0;
        for (int ch : text.toCharArray()) {
            CharLocation fontChar;
            int index = ch < charLocations.length ? ch : 3;

            if (charLocations.length <= index || (fontChar = charLocations[index]) == null) {
                width += mc.fontRenderer.getStringWidth(String.valueOf(ch)) / 4.0;
                continue;
            }

            width += fontChar.width - 8.0;
        }

        return width / 2;
    }

    public Font getFont() {
        return font;
    }

    private static class CharLocation {
        private final int x;
        private final int y;
        private final int width;
        private final int height;

        CharLocation(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}