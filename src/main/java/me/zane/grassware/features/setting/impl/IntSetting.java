package me.zane.grassware.features.setting.impl;

import me.zane.grassware.features.setting.Setting;

import java.util.function.Predicate;

public class IntSetting extends Setting<Integer> {
    public int min, max;

    public IntSetting(final String name, final Integer value) {
        super(name, value);
    }

    @Override
    public IntSetting invokeVisibility(Predicate<Integer> visible) {
        super.invokeVisibility(visible);
        return this;
    }
}
