package me.zane.grassware.features;
//WARNING: ALL CONTENT BELONGS TO https://github.com/Zane2b2t , IF ANY OF THE CLASSES CONTAINING THIS WARNING ARENT IN https://github.com/Zane2b2t/Grassware.win-Rewrite INFORM GITHUB TO DMCA
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.Setting;
import me.zane.grassware.features.setting.impl.*;
import me.zane.grassware.util.MC;

import java.util.ArrayList;
import java.util.List;

public class Feature implements MC {
    public final ArrayList<Setting<?>> settings = new ArrayList<>();
    public String name;

    public Feature() {
    }

    public Feature(String name) {
        this.name = name;
    }

    public static boolean nullCheck() {
        return Feature.mc.player == null || Feature.mc.world == null;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<Setting<?>> getSettings() {
        return this.settings;
    }

    public boolean isEnabled() {
        if (this instanceof Module) {
            return ((Module) this).enabled.getValue();
        }
        return false;
    }

    public BindSetting register(final String name, final int value) {
        final BindSetting setting = new BindSetting(name, value);
        settings.add(setting);
        return setting;
    }

    public ModeSetting register(final String name, final String value, final List<String> values) {
        final ModeSetting setting = new ModeSetting(name, value, values);
        settings.add(setting);
        return setting;
    }

    public BooleanSetting register(final String name, final boolean value) {
        final BooleanSetting setting = new BooleanSetting(name, value);
        settings.add(setting);
        return setting;
    }

    public IntSetting register(final String name, final int value, final int min, final int max) {
        final IntSetting setting = new IntSetting(name, value);
        setting.min = min;
        setting.max = max;
        settings.add(setting);
        return setting;
    }

    public FloatSetting register(final String name, final float value, final float min, final float max) {
        final FloatSetting setting = new FloatSetting(name, value);
        setting.min = min;
        setting.max = max;
        settings.add(setting);
        return setting;
    }

    public StringSetting register(final String name, final String value) {
        final StringSetting setting = new StringSetting(name, value);
        settings.add(setting);
        return setting;
    }
}

