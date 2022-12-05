package me.zane.grassware.features.setting.impl;

import me.zane.grassware.features.setting.Setting;

import java.util.function.Predicate;

public class BindSetting extends Setting<Integer> {

    public BindSetting(final String name, final Integer value) {
        super(name, value);
    }

    @Override
    public BindSetting invokeVisibility(Predicate<Integer> visible) {
        super.invokeVisibility(visible);
        return this;
    }
}

