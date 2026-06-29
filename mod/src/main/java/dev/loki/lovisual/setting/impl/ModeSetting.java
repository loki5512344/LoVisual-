package dev.loki.lovisual.setting.impl;

import dev.loki.lovisual.setting.Setting;

import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting<String> {
    private final List<String> modes;
    private int index;

    public ModeSetting(String name, String defaultValue, String... modes) {
        super(name, defaultValue);
        this.modes = Arrays.asList(modes);
        this.index = this.modes.indexOf(defaultValue);
        if (index == -1) index = 0;
    }

    public static ModeSetting of(String name, String defaultValue, String... modes) {
        return new ModeSetting(name, defaultValue, modes);
    }

    public List<String> getModes() { return modes; }

    public void cycle() {
        index = (index + 1) % modes.size();
        set(modes.get(index));
    }

    public void cycleBack() {
        index = (index - 1 + modes.size()) % modes.size();
        set(modes.get(index));
    }

    @Override
    public String serialize() {
        return get();
    }

    @Override
    public void deserialize(String value) {
        int idx = modes.indexOf(value);
        if (idx != -1) index = idx;
        set(value);
    }
}
