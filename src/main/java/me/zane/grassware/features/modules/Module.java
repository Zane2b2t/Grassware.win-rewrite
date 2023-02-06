package me.zane.grassware.features.modules;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.zane.grassware.GrassWare;
import me.zane.grassware.features.Feature;
import me.zane.grassware.features.command.Command;
import me.zane.grassware.features.setting.impl.BindSetting;
import me.zane.grassware.features.setting.impl.BooleanSetting;


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
            GrassWare.eventBus.registerListener(this);
            onToggle();
            onEnable();
            Command.sendRemovableMessage(ChatFormatting.WHITE + name + " Turned " + ChatFormatting.GREEN + "ON", 1);
            enabled.invokeValue(true);
        }
    }

    public void disable() {
        if (enabled.getValue()) {
            GrassWare.eventBus.unregisterListener(this);
            onToggle();
            onDisable();
            Command.sendRemovableMessage(ChatFormatting.WHITE + name + " Turned " + ChatFormatting.RED + "OFF", 1);
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

    public float totalStringWidth() {
        return stringWidth() + infoWidth();
    }

    public float stringWidth() {
        return -GrassWare.textManager.stringWidth(name + (info == null || info.equals("") ? "" : " "));
    }

    public Category getCategory() {
        return category;
    }

    public Module setCategory(final Category category) {
        this.category = category;
        return this;
    }

    public int getBind() {
        return bind.getValue();
    }

    public float infoWidth() {
        return -GrassWare.textManager.stringWidth(getInfo());
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

