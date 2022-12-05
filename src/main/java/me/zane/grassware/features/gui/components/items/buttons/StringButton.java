package me.zane.grassware.features.gui.components.items.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.zane.grassware.GrassWare;
import me.zane.grassware.features.gui.OyVeyGui;
import me.zane.grassware.features.setting.impl.StringSetting;
import me.zane.grassware.util.RenderUtil;
import me.zane.grassware.util.Timer;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ChatAllowedCharacters;

import java.awt.*;

public class StringButton extends Button {
    private final StringSetting setting;
    public boolean isListening;
    private CurrentString currentString = new CurrentString("");

    public StringButton(StringSetting setting) {
        super(setting.getName());
        this.setting = setting;
        width = 15;
    }

    public static String removeLastChar(String str) {
        String output = "";
        if (str != null && str.length() > 0) {
            output = str.substring(0, str.length() - 1);
        }
        return output;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.rect(x, y, x + width + 7.4f, y + height - 0.5f, !isHovering(mouseX, mouseY) ? new Color(0, 0, 0, 75) : new Color(0, 0, 0, 50));
        if (isListening) {
            GrassWare.textManager.renderString(currentString.getString() + typingIcon(), x + 2.3f, y - 1.7f - (float) OyVeyGui.getClickGui().getTextOffset(), getState() ? Color.WHITE : new Color(-5592406));
        } else {
            GrassWare.textManager.renderString((setting.getName().equals("Buttons") ? "Buttons " : (setting.getName().equals("Prefix") ? "Prefix  " + ChatFormatting.GRAY : "")) + setting.getValue(), x + 2.3f, y - 1.7f - OyVeyGui.getClickGui().getTextOffset(), getState() ? Color.WHITE : new Color(-5592406));
        }
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
            switch (keyCode) {
                case 1: {
                    return;
                }
                case 28: {
                    enterString();
                }
                case 14: {
                    setString(StringButton.removeLastChar(currentString.getString()));
                }
            }
            if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                setString(currentString.getString() + typedChar);
            }
        }
    }

    @Override
    public void update() {
        setHidden(!setting.visible());
    }

    private void enterString() {
        if (currentString.getString().isEmpty()) {
            setting.invokeValue(setting.getValue());
        } else {
            setting.invokeValue(currentString.getString());
        }
        setString("");
        onMouseClick();
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

    public void setString(String newString) {
        currentString = new CurrentString(newString);
    }

    private final Timer idleTimer = new Timer();
    private boolean idling;

    public String typingIcon() {
        if (idleTimer.passedMs(500L)) {
            idling = !idling;
            idleTimer.sync();
        }
        if (idling) {
            return "_";
        }
        return "";
    }

    public static class CurrentString {
        private final String string;

        public CurrentString(String string) {
            this.string = string;
        }

        public String getString() {
            return string;
        }
    }
}

