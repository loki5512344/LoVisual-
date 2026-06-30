package dev.loki.lovisual.setting.impl;

import dev.loki.lovisual.setting.Setting;

public class BindSetting extends Setting<Integer> {
    public BindSetting(String name, int defaultValue) {
        super(name, defaultValue);
    }

    public static BindSetting of(String name, int defaultValue) {
        return new BindSetting(name, defaultValue);
    }

    @Override
    public String serialize() {
        return String.valueOf(get());
    }

    @Override
    public void deserialize(String value) {
        try {
            set(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            set(getDefault());
        }
    }
}
