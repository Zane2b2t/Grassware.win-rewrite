package me.zane.grassware.features.setting.impl;

import me.zane.grassware.features.setting.Setting;

import java.util.function.Predicate;

public class DoubleSetting extends Setting<Double> {
    public int min, max;

    public DoubleSetting(final String name, final Double value) {
        super(name, value);
    }

    @Override
    public DoubleSetting invokeVisibility(Predicate<Double> visible) {
        super.invokeVisibility(visible);
        return this;
    }

    public int getMax() {
        return max;
    }
}
