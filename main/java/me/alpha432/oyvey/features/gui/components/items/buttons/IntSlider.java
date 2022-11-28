package me.alpha432.oyvey.features.gui.components.items.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.gui.OyVeyGui;
import me.alpha432.oyvey.features.gui.components.Component;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.features.setting.impl.IntSetting;
import me.alpha432.oyvey.util.RenderUtil;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class IntSlider extends Button {
    public IntSetting setting;

    public IntSlider(final IntSetting setting) {
        super(setting.getName());
        this.setting = setting;
        width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        dragSetting(mouseX, mouseY);
        RenderUtil.rect(x, y, x + width + 7.4f, y + height - 0.5f, !isHovering(mouseX, mouseY) ? new Color(0, 0, 0, 75) : new Color(0, 0, 0, 50));
        RenderUtil.rectGuiTex(x, y, setting.getValue() <= setting.min ? x : x + (width + 7.4f) * partialMultiplier(), y + height - 0.5f, isHovering(mouseX, mouseY) ? ClickGui.Instance.getColorAlpha() : ClickGui.Instance.getColor());
        OyVey.textManager.renderString(getName(), x + 2.3f, y - 1.7f - OyVeyGui.getClickGui().getTextOffset(), Color.WHITE);
        OyVey.textManager.renderString(setting.getValue() + "", x + 2.3f + OyVey.textManager.stringWidth(getName() + " "), y - 1.7f - OyVeyGui.getClickGui().getTextOffset(), Color.GRAY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isHovering(mouseX, mouseY)) {
            setSettingFromX(mouseX);
        }
    }

    @Override
    public boolean isHovering(int mouseX, int mouseY) {
        for (Component component : OyVeyGui.getClickGui().getComponents()) {
            if (!component.drag) continue;
            return false;
        }
        return mouseX >= getX() && mouseX <= getX() + getWidth() + 8.0f && mouseY >= getY() && mouseY <= getY() + height;
    }

    @Override
    public void update() {
        setHidden(!setting.visible());
    }

    private void dragSetting(int mouseX, int mouseY) {
        if (isHovering(mouseX, mouseY) && Mouse.isButtonDown(0)) {
            setSettingFromX(mouseX);
        }
    }

    @Override
    public int getHeight() {
        return 14;
    }

    private void setSettingFromX(int mouseX) {
        final float percent = (mouseX - x) / (width + 7.4f);
        final float amount = setting.min + ((setting.max - setting.min) * percent);
        setting.invokeValue(Math.min(setting.max, (int) amount));
    }

    private float middle() {
        return setting.max - setting.min;
    }

    private float part() {
        return setting.getValue() - setting.min;
    }

    private float partialMultiplier() {
        return part() / middle();
    }
}

