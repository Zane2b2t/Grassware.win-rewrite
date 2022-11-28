package me.alpha432.oyvey.features.modules;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.setting.impl.BindSetting;
import me.alpha432.oyvey.features.setting.impl.BooleanSetting;
import net.minecraftforge.common.MinecraftForge;

public class Module extends Feature {
    public float anim = 0.0f;
    public Category category;
    public BooleanSetting enabled = register("Enabled", false);
    public BooleanSetting drawn = register("Drawn", true);
    public BindSetting bind = register("Keybind", -1);
    public String info;

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onToggle() {
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            enable();
        } else {
            disable();
        }
    }

    public void enable() {
        if (!enabled.getValue()) {
            OyVey.eventBus.registerListener(this);
            onToggle();
            onEnable();
            Command.sendRemovableMessage(OyVey.commandManager.getClientMessage() + " " + ChatFormatting.WHITE + name + " has been " + ChatFormatting.GREEN + "Enabled", 1);
            enabled.invokeValue(true);
        }
    }

    public void disable() {
        if (enabled.getValue()) {
            OyVey.eventBus.unregisterListener(this);
            onToggle();
            onDisable();
            Command.sendRemovableMessage(OyVey.commandManager.getClientMessage() + " " + ChatFormatting.WHITE + name + " has been " + ChatFormatting.RED + "Disabled", 1);
            enabled.invokeValue(false);
        }
    }

    public void toggle() {
        setEnabled(!isEnabled());
        if (isEnabled()) {
            enable();
        } else {
            disable();
        }
    }


    public Module setName(final String name) {
        this.name = name;
        return this;
    }

    public Module setCategory(final Category category) {
        this.category = category;
        return this;
    }

    public float totalStringWidth() {
        return stringWidth() + infoWidth();
    }

    public float stringWidth() {
        return -OyVey.textManager.stringWidth(name + (info == null || info.equals("") ? "" : " "));
    }

    public Category getCategory() {
        return category;
    }

    public int getBind() {
        return bind.getValue();
    }

    public float infoWidth() {
        return -OyVey.textManager.stringWidth(getInfo());
    }

    public String getInfo() {
        return info;
    }

    public void invokeInfo(final String info) {
        this.info = info;
    }

    public enum Category {
        COMBAT("Combat"),
        MISC("Misc"),
        RENDER("Render"),
        MOVEMENT("Movement"),
        PLAYER("Player"),
        CLIENT("Client");

        private final String name;

        Category(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}

