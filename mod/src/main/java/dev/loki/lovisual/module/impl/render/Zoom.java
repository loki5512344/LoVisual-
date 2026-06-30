package dev.loki.lovisual.module.impl.render;

import dev.loki.lovisual.core.event.impl.TickEvent;
import dev.loki.lovisual.module.Module;
import dev.loki.lovisual.module.ModuleCategory;
import dev.loki.lovisual.module.ModuleInfo;
import dev.loki.lovisual.core.event.EventHandler;
import dev.loki.lovisual.setting.impl.BooleanSetting;
import dev.loki.lovisual.setting.impl.SliderSetting;

@ModuleInfo(name = "Zoom", desc = "Scroll to zoom", category = ModuleCategory.RENDER, key = 0)
public class Zoom extends Module {
    public final SliderSetting maxZoom = new SliderSetting("Max Zoom", 30, 5, 100, 1);
    public final SliderSetting speed = new SliderSetting("Speed", 0.15, 0.01, 0.5, 0.01);
    public final BooleanSetting scroll = new BooleanSetting("Scroll", true);

    private double currentFov = 1.0;
    private double targetFov = 1.0;

    @Override
    protected void onEnable() {
        currentFov = 1.0;
        targetFov = maxZoom.get() / 100.0;
    }

    @Override
    protected void onDisable() {
        targetFov = 1.0;
    }

    @EventHandler
    private void onTick(TickEvent event) {
        currentFov += (targetFov - currentFov) * speed.get();
    }

    public double getCurrentFov() {
        return currentFov;
    }
}
