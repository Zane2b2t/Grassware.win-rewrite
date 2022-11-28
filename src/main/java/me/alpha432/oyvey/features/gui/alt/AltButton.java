package me.alpha432.oyvey.features.gui.alt;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.util.RenderUtil;

import java.awt.*;

public class AltButton {
    public final String email, password;
    public final Alt.AltType altType;
    public float x, y, width, height;

    public AltButton(final String email, final String password, final Alt.AltType altType, final float x, final float y, final float width, final float height) {
        this.altType = altType;
        this.email = email;
        this.password = password;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void drawScreen(final int mouseX, final int mouseY) {
        RenderUtil.rect(x, y, x + width, y + height, altType.equals(Alt.AltType.MICROSOFT) ? Color.CYAN : Color.RED);
        RenderUtil.rect(x + 0.5f, y + 0.5f, x + width - 0.5f, y + height - 0.5f, new Color(18, 18, 18));
        OyVey.textManager.renderString(email, x + 5.0f, y + 5.0f, Color.WHITE);
        final StringBuilder string = new StringBuilder();
        for (int i = 0; i < password.length(); i++) {
            string.append("*");
        }
        OyVey.textManager.renderString(string.toString(), x + 5.0f, y + 17.5f, Color.WHITE);

        RenderUtil.rect(x + 5.0f, y + height - 17.5f, x + width / 2.0f - 5.0f, y + height - 2.5f, new Color(15, 15, 15));
        OyVey.textManager.renderString("Login", x + 2.5f + ((width / 2.0f - 5.0f) / 2.0f) - OyVey.textManager.stringWidth("Login") / 2.0f, y + height - 7.5f - OyVey.textManager.stringHeight() / 2.0f, Color.WHITE);
        if (mouseX > x + 5.0f && mouseX < x + width / 2.0f - 5.0f && mouseY > y + height - 17.5f && mouseY < y + height - 2.5f) {
            RenderUtil.rect(x + 5.0f, y + height - 17.5f, x + width / 2.0f - 5.0f, y + height - 2.5f, new Color(0, 0, 0, 50));
        }

        RenderUtil.rect(x + width / 2.0f + 2.5f, y + height - 17.5f, x + width - 5.0f, y + height - 2.5f, new Color(15, 15, 15));
        if (mouseX > x + width / 2.0f + 2.5f && mouseX < x + width - 5.0f && mouseY > y + height - 17.5f && mouseY < y + height - 2.5f) {
            RenderUtil.rect(x + width / 2.0f + 2.5f, y + height - 17.5f, x + width - 5.0f, y + height - 2.5f, new Color(0, 0, 0, 50));
        }

        OyVey.textManager.renderString("Delete", x + width / 2.0f + 2.5f + ((width / 2.0f - 5.0f) / 2.0f) - OyVey.textManager.stringWidth("Delete") / 2.0f, y + height - 7.5f - OyVey.textManager.stringHeight() / 2.0f, Color.RED);
    }

    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if (mouseButton != 0) {
            return;
        }
        if (mouseX > x + 5.0f && mouseX < x + width / 2.0f - 5.0f && mouseY > y + height - 17.5f && mouseY < y + height - 2.5f) {
            new Alt(email, password, altType).login();
        }
        if (mouseX > x + width / 2.0f + 2.5f && mouseX < x + width - 5.0f && mouseY > y + height - 17.5f && mouseY < y + height - 2.5f) {
            AltGui.altButtons.remove(this);
            AltGui.updateButtons();
        }
    }
}
