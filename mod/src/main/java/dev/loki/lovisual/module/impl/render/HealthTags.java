package dev.loki.lovisual.module.impl.render;

import dev.loki.lovisual.core.event.impl.Render2DEvent;
import dev.loki.lovisual.module.Module;
import dev.loki.lovisual.module.ModuleCategory;
import dev.loki.lovisual.module.ModuleInfo;
import dev.loki.lovisual.core.event.EventHandler;

@ModuleInfo(name = "HealthTags", desc = "Shows health indicators above players", category = ModuleCategory.RENDER)
public class HealthTags extends Module {

    @EventHandler
    private void onRender(Render2DEvent event) {
    }
}
