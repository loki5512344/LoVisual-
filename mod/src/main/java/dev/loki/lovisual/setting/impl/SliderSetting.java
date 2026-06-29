package dev.loki.lovisual.setting.impl;

import dev.loki.lovisual.setting.Setting;

public class SliderSetting extends Setting<Double> {
    private final double min;
    private final double max;
    private final double step;

    public SliderSetting(String name, double defaultValue, double min, double max) {
        this(name, defaultValue, min, max, 0.1);
    }

    public SliderSetting(String name, double defaultValue, double min, double max, double step) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public static SliderSetting of(String name, double defaultValue, double min, double max) {
        return new SliderSetting(name, defaultValue, min, max);
    }

    public double getMin() { return min; }
    public double getMax() { return max; }
    public double getStep() { return step; }

    @Override
    public String serialize() {
        return String.valueOf(get());
    }

    @Override
    public void deserialize(String value) {
        set(Double.parseDouble(value));
    }
}
