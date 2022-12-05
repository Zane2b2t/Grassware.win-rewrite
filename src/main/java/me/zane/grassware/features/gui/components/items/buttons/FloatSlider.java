package me.zane.grassware.features.gui.components.items.buttons;

import me.zane.grassware.GrassWare;
import me.zane.grassware.features.gui.GrassWareGui;
import me.zane.grassware.features.gui.components.Component;
import me.zane.grassware.features.modules.client.ClickGui;
import me.zane.grassware.features.setting.impl.FloatSetting;
import me.zane.grassware.util.RenderUtil;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class FloatSlider
        extends Button {
    public FloatSetting setting;

    public FloatSlider(FloatSetting setting) {
        super(setting.getName());
        this.setting = setting;
        width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        dragSetting(mouseX, mouseY);
        RenderUtil.rect(x, y, x + width + 7.4f, y + height - 0.5f, !isHovering(mouseX, mouseY) ? new Color(0, 0, 0, 75) : new Color(0, 0, 0, 50));
        RenderUtil.rectGuiTex(x, y, setting.getValue() <= setting.min ? x : x + (width + 7.4f) * partialMultiplier(), y + height - 0.5f, isHovering(mouseX, mouseY) ? ClickGui.Instance.getColorAlpha() : ClickGui.Instance.getColor());
        GrassWare.textManager.renderString(getName(), x + 2.3f, y - 1.7f - GrassWareGui.getClickGui().getTextOffset(), Color.WHITE);
        GrassWare.textManager.renderString(setting.getValue() + "", x + 2.3f + GrassWare.textManager.stringWidth(getName() + " "), y - 1.7f - GrassWareGui.getClickGui().getTextOffset(), Color.GRAY);
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
        for (Component component : GrassWareGui.getClickGui().getComponents()) {
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
        final float result = setting.min + ((setting.max - setting.min) * percent);
        setting.invokeValue(Math.min(setting.max, Math.round(10.0f * result) / 10.0f));
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