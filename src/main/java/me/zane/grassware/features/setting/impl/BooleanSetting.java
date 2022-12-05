package me.zane.grassware.features.setting.impl;


import me.zane.grassware.features.setting.Setting;

import java.util.function.Predicate;

public class BooleanSetting extends Setting<Boolean> {

    public BooleanSetting(final String name, final Boolean value) {
        super(name, value);
    }

    @Override
    public BooleanSetting invokeVisibility(Predicate<Boolean> visible) {
        super.invokeVisibility(visible);
        return this;
    }
}
