package me.zane.grassware.features.gui.components.items.buttons;

import me.zane.grassware.GrassWare;
import me.zane.grassware.features.gui.GrassWareGui;
import me.zane.grassware.features.modules.client.ClickGui;
import me.zane.grassware.features.setting.impl.ModeSetting;
import me.zane.grassware.util.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import java.awt.*;

public class EnumButton extends Button {
    public ModeSetting setting;

    public EnumButton(final ModeSetting setting) {
        super(setting.getName());
        this.setting = setting;
        width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.rectGuiTex(x, y, x + width + 7.4f, y + height - 0.5f, isHovering(mouseX, mouseY) ? ClickGui.Instance.getColorAlpha() : ClickGui.Instance.getColor());
        GrassWare.textManager.renderString(getName(), x + 2.3f, y - 1.7f - GrassWareGui.getClickGui().getTextOffset(), Color.WHITE);
        GrassWare.textManager.renderString(setting.getValue() + "", x + 2.3f + GrassWare.textManager.stringWidth(getName() + " "), y - 1.7f - GrassWareGui.getClickGui().getTextOffset(), Color.GRAY);
    }

    @Override
    public void update() {
        setHidden(!setting.visible());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isHovering(mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }

    @Override
    public int getHeight() {
        return 14;
    }

    @Override
    public void toggle() {
        int modeIndex = (setting.values.indexOf(setting.getValue()) + 1) % setting.values.size();
        setting.invokeValue(setting.values.get(modeIndex));
    }

    @Override
    public boolean getState() {
        return true;
    }
}

