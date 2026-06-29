package dev.loki.lovisual.module.impl.render;

import dev.loki.lovisual.core.event.impl.Render2DEvent;
import dev.loki.lovisual.module.Module;
import dev.loki.lovisual.module.ModuleCategory;
import dev.loki.lovisual.module.ModuleInfo;
import dev.loki.lovisual.setting.impl.BooleanSetting;
import dev.loki.lovisual.setting.impl.ColorSetting;
import dev.loki.lovisual.core.event.EventHandler;

import java.awt.Color;

@ModuleInfo(name = "ArrayList", desc = "Shows enabled modules", category = ModuleCategory.RENDER)
public class ArrayListMod extends Module {

    private final BooleanSetting rainbow = BooleanSetting.of("Rainbow", true);
    private final ColorSetting color = ColorSetting.of("Color", Color.CYAN);

    @EventHandler
    private void onRender(Render2DEvent event) {
        // render enabled modules list
    }
}
