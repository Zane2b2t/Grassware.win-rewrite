package me.alpha432.oyvey.features.setting.impl;

import me.alpha432.oyvey.features.setting.Setting;

import java.util.function.Predicate;

public class FloatSetting extends Setting<Float> {
    public float min, max;

    public FloatSetting(final String name, final Float value) {
        super(name, value);
    }

    @Override
    public FloatSetting invokeVisibility(Predicate<Float> visible) {
        super.invokeVisibility(visible);
        return this;
    }
}

