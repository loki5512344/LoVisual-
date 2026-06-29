package dev.loki.lovisual.module.impl.render;

import dev.loki.lovisual.core.event.EventHandler;
import dev.loki.lovisual.core.event.impl.TickEvent;
import dev.loki.lovisual.module.Module;
import dev.loki.lovisual.module.ModuleCategory;
import dev.loki.lovisual.module.ModuleInfo;

@ModuleInfo(name = "NoWeather", desc = "Removes weather", category = ModuleCategory.RENDER)
public class NoWeather extends Module {
    @EventHandler
    private void onTick(TickEvent event) {
        if (mc.world == null) return;
        mc.world.setRainGradient(0);
        mc.world.setThunderGradient(0);
    }
}
