package me.zane.grassware.features.gui.components.items.buttons;

import me.zane.grassware.GrassWare;
import me.zane.grassware.features.gui.GrassWareGui;
import me.zane.grassware.features.modules.client.ClickGui;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.util.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import java.awt.*;

public class BooleanButton
        extends Button {
    private final BooleanSetting setting;

    public BooleanButton(BooleanSetting setting) {
        super(setting.getName());
        this.setting = setting;
        width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (setting.getValue()) {
            RenderUtil.rectGuiTex(x, y, x + width + 7.4f, y + height - 0.5f, isHovering(mouseX, mouseY) ? ClickGui.Instance.getColorAlpha() : ClickGui.Instance.getColor());
        } else {
            RenderUtil.rect(x, y, x + width + 7.4f, y + height - 0.5f, isHovering(mouseX, mouseY) ? new Color(0, 0, 0, 75) : new Color(0, 0, 0, 50));
        }
        GrassWare.textManager.renderString(getName(), x + 2.3f, y - 1.7f - GrassWareGui.getClickGui().getTextOffset(), getState() ? Color.WHITE : new Color(-5592406));
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
        setting.invokeValue(!setting.getValue());
    }

    @Override
    public boolean getState() {
        return setting.getValue();
    }
}

