package dev.loki.lovisual.module.impl.render;

import dev.loki.lovisual.core.event.impl.Render2DEvent;
import dev.loki.lovisual.module.Module;
import dev.loki.lovisual.module.ModuleCategory;
import dev.loki.lovisual.module.ModuleInfo;
import dev.loki.lovisual.setting.impl.BooleanSetting;
import dev.loki.lovisual.setting.impl.ModeSetting;
import dev.loki.lovisual.core.event.EventHandler;

@ModuleInfo(name = "Nametags", desc = "Custom nametags above players", category = ModuleCategory.RENDER)
public class Nametags extends Module {
    private final ModeSetting style = new ModeSetting("Style", "Clean", "Clean", "Pulse", "Rockstar");
    private final BooleanSetting background = new BooleanSetting("Background", true);
    private final BooleanSetting shadow = new BooleanSetting("Shadow", true);
    private final BooleanSetting healthBar = new BooleanSetting("HealthBar", true);

    @EventHandler
    private void onRender(Render2DEvent event) {
    }
}
