package dev.loki.lovisual.setting;

import java.util.function.Supplier;

public abstract class Setting<T> {
    private final String name;
    private final T defaultValue;
    private T value;
    private Supplier<Boolean> visible = () -> true;

    public Setting(String name, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public T getDefault() { return defaultValue; }

    public String getName() { return name; }
    public T get() { return value; }
    public void set(T value) { this.value = value; }
    public boolean isVisible() { return visible.get(); }
    public Setting<T> visibility(Supplier<Boolean> visible) { this.visible = visible; return this; }

    public abstract String serialize();
    public abstract void deserialize(String value);
}
