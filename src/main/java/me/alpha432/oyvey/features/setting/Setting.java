package me.alpha432.oyvey.features.setting;

import java.util.function.Predicate;

public abstract class Setting<T> {
    public final String name;
    public T value;
    public Predicate<T> visible;

    public Setting(final String name, final T value) {
        this.name = name;
        this.value = value;
    }

    public String getName(){
        return name;
    }

    public T getValue(){
        return value;
    }

    public void invokeValue(final T value) {
        this.value = value;
    }

    public Setting<T> invokeVisibility(final Predicate<T> visible) {
        this.visible = visible;
        return this;
    }

    public boolean visible() {
        return visible == null || visible.test(this.value);
    }
}