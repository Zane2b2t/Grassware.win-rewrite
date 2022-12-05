package me.zane.grassware.features.setting.impl;

import me.zane.grassware.features.setting.Setting;

import java.util.function.Predicate;

public class StringSetting extends Setting<String> {

    public StringSetting(final String name, final String value) {
        super(name, value);
    }

    @Override
    public StringSetting invokeVisibility(Predicate<String> visible) {
        super.invokeVisibility(visible);
        return this;
    }
}
