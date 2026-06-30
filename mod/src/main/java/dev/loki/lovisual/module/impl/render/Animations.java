package dev.loki.lovisual.module.impl.render;

import dev.loki.lovisual.module.Module;
import dev.loki.lovisual.module.ModuleCategory;
import dev.loki.lovisual.module.ModuleInfo;
import dev.loki.lovisual.setting.impl.BooleanSetting;
import dev.loki.lovisual.setting.impl.ModeSetting;
import dev.loki.lovisual.setting.impl.SliderSetting;

@ModuleInfo(name = "Animations", desc = "Custom item animations", category = ModuleCategory.RENDER, key = 0)
public class Animations extends Module {
    public static Animations INSTANCE;

    public final ModeSetting swingMode = new ModeSetting("Swing Mode", "Normal", "Normal", "1.7", "None");
    public final SliderSetting swingSpeed = new SliderSetting("Swing Speed", 1.0, 0.5, 2.0, 0.1);
    public final BooleanSetting noBreakDelay = new BooleanSetting("No BreakDelay", false);
    public final SliderSetting itemScale = new SliderSetting("Item Scale", 1.0, 0.5, 1.5, 0.05);

    public Animations() {
        INSTANCE = this;
    }

    public String getSwingMode() {
        return swingMode.get();
    }

    public double getSwingSpeed() {
        return swingSpeed.get();
    }

    public double getItemScale() {
        return itemScale.get();
    }

    public boolean isNoBreakDelay() {
        return noBreakDelay.get();
    }
}
