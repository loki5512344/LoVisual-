package dev.loki.lovisual.setting.impl;

import dev.loki.lovisual.setting.Setting;

public class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String name, boolean defaultValue) {
        super(name, defaultValue);
    }

    public static BooleanSetting of(String name, boolean defaultValue) {
        return new BooleanSetting(name, defaultValue);
    }

    public void toggle() {
        set(!get());
    }

    @Override
    public String serialize() {
        return get() ? "true" : "false";
    }

    @Override
    public void deserialize(String value) {
        set(value.equalsIgnoreCase("true"));
    }
}
