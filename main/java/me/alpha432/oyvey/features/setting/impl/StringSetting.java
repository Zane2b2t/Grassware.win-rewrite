package me.alpha432.oyvey.features.setting.impl;

import me.alpha432.oyvey.features.setting.Setting;

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
