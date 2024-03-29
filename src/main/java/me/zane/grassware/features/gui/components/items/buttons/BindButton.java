package me.zane.grassware.features.gui.components.items.buttons;

import me.zane.grassware.GrassWare;
import me.zane.grassware.features.gui.GrassWareGui;
import me.zane.grassware.features.modules.client.ClickGui;
import me.zane.grassware.features.setting.impl.BindSetting;
import me.zane.grassware.util.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class BindButton extends Button {
    private final BindSetting setting;
    public boolean isListening;

    public BindButton(BindSetting setting) {
        super(setting.getName());
        this.setting = setting;
        width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (getState()) {
            RenderUtil.rect(x, y, x + width + 7.4f, y + height - 0.5f, isHovering(mouseX, mouseY) ? new Color(0, 0, 0, 75) : new Color(0, 0, 0, 50));
        } else {
            RenderUtil.rectGuiTex(x, y, x + width + 7.4f, y + height - 0.5f, !isHovering(mouseX, mouseY) ? ClickGui.Instance.getColorAlpha() : ClickGui.Instance.getColor());
        }
        if (isListening) {
            GrassWare.textManager.renderString("Press a Key...", x + 2.3f, y - 1.7f - GrassWareGui.getClickGui().getTextOffset(), Color.WHITE);
        } else {
            GrassWare.textManager.renderString(getName(), x + 2.3f, y - 1.7f - GrassWareGui.getClickGui().getTextOffset(), Color.WHITE);
            GrassWare.textManager.renderString(setting.getValue() == -1 ? "None" : Keyboard.getKeyName(setting.getValue()).toUpperCase(), x + 2.3f + GrassWare.textManager.stringWidth(getName() + " "), y - 1.7f - GrassWareGui.getClickGui().getTextOffset(), Color.GRAY);

        }
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
    public void onKeyTyped(char typedChar, int keyCode) {
        if (isListening) {
            if (keyCode == Keyboard.KEY_DELETE || keyCode == Keyboard.KEY_BACK) {
                setting.invokeValue(Keyboard.KEY_NONE);
            } else {
                setting.invokeValue(keyCode);
            }
            onMouseClick();
        }
    }

    @Override
    public int getHeight() {
        return 14;
    }

    @Override
    public void toggle() {
        isListening = !isListening;
    }

    @Override
    public boolean getState() {
        return !isListening;
    }
}

