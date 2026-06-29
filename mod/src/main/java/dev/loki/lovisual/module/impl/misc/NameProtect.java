package dev.loki.lovisual.module.impl.misc;

import dev.loki.lovisual.core.event.impl.TickEvent;
import dev.loki.lovisual.module.Module;
import dev.loki.lovisual.module.ModuleCategory;
import dev.loki.lovisual.module.ModuleInfo;
import dev.loki.lovisual.setting.impl.ModeSetting;
import dev.loki.lovisual.core.event.EventHandler;

@ModuleInfo(name = "NameProtect", desc = "Hides player names for streaming", category = ModuleCategory.MISC, key = -1)
public class NameProtect extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Stream", "Stream", "Replace");

    @EventHandler
    private void onTick(TickEvent event) {
        if (mc.player == null) return;
    }
}
