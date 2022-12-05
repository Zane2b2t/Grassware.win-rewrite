package me.zane.grassware.features.gui.components.items.buttons;

import me.zane.grassware.GrassWare;
import me.zane.grassware.features.gui.OyVeyGui;
import me.zane.grassware.features.gui.components.Component;
import me.zane.grassware.features.gui.components.items.Item;
import me.zane.grassware.features.modules.client.ClickGui;
import me.zane.grassware.util.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import java.awt.*;

public class Button extends Item {
    private boolean state;

    public Button(String name) {
        super(name);
        height = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        state = getState();
        if (state) {
            RenderUtil.rectGuiTex(x, y, x + width, y + height - 0.5f, isHovering(mouseX, mouseY) ? ClickGui.Instance.getColorAlpha() : ClickGui.Instance.getColor());
        } else {
            RenderUtil.rect(x, y, x + width, y + height - 0.5f, isHovering(mouseX, mouseY) ? new Color(0, 0, 0, 75) : new Color(0, 0, 0, 50));
        }
        GrassWare.textManager.renderString(getName(), x + 2.3f, y - 2.0f - OyVeyGui.getClickGui().getTextOffset(), getState() ? Color.WHITE : new Color(-5592406));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && isHovering(mouseX, mouseY)) {
            onMouseClick();
        }
    }

    public void onMouseClick() {
        state = !state;
        toggle();
        mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }

    public void toggle() {
    }

    public boolean getState() {
        return state;
    }

    @Override
    public int getHeight() {
        return 14;
    }

    public boolean isHovering(int mouseX, int mouseY) {
        for (Component component : OyVeyGui.getClickGui().getComponents()) {
            if (!component.drag) continue;
            return false;
        }
        return mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + height;
    }
}

