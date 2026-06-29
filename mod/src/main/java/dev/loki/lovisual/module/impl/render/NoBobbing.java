package dev.loki.lovisual.module.impl.render;

import dev.loki.lovisual.core.event.EventHandler;
import dev.loki.lovisual.core.event.impl.TickEvent;
import dev.loki.lovisual.module.Module;
import dev.loki.lovisual.module.ModuleCategory;
import dev.loki.lovisual.module.ModuleInfo;

@ModuleInfo(name = "NoBobbing", desc = "Disables view bobbing", category = ModuleCategory.RENDER)
public class NoBobbing extends Module {
    @EventHandler
    private void onTick(TickEvent event) {
        if (mc.options.getBobView().getValue()) {
            mc.options.getBobView().setValue(false);
        }
    }
}
