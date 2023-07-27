package me.zane.grassware.features.setting.impl;

import me.zane.grassware.features.setting.Setting;

import java.util.function.Predicate;

public class FloatSetting extends Setting<Float> {
    public float min, max;

    public FloatSetting(final String name, final Float value) {
        super(name, value);
    }

    public Float setValue(Float value) {
        this.value = value;
        return value;
    }

    @Override
    public FloatSetting invokeVisibility(Predicate<Float> visible) {
        super.invokeVisibility(visible);
        return this;
    }

    public float getMax() {
        return max;
    }

    public float getMin() {
        return min;
    }
}

