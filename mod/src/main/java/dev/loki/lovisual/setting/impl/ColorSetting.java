package dev.loki.lovisual.setting.impl;

import dev.loki.lovisual.setting.Setting;

import java.awt.Color;

public class ColorSetting extends Setting<Color> {
    private boolean rainbow;

    public ColorSetting(String name, Color defaultValue) {
        super(name, defaultValue);
    }

    public static ColorSetting of(String name, Color defaultValue) {
        return new ColorSetting(name, defaultValue);
    }

    public boolean isRainbow() { return rainbow; }
    public void setRainbow(boolean rainbow) { this.rainbow = rainbow; }

    @Override
    public String serialize() {
        return String.format("#%02x%02x%02x %s", get().getRed(), get().getGreen(), get().getBlue(),
                rainbow ? "rainbow" : "solid");
    }

    @Override
    public void deserialize(String value) {
        String[] parts = value.split(" ");
        try {
            set(Color.decode(parts[0]));
            rainbow = parts.length > 1 && parts[1].equals("rainbow");
        } catch (Exception e) {
            set(Color.CYAN);
            rainbow = false;
        }
    }
}
