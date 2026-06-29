package dev.loki.lovisual.module.impl.render;

import dev.loki.lovisual.core.event.impl.Render3DEvent;
import dev.loki.lovisual.module.Module;
import dev.loki.lovisual.module.ModuleCategory;
import dev.loki.lovisual.module.ModuleInfo;
import dev.loki.lovisual.setting.impl.ColorSetting;
import dev.loki.lovisual.setting.impl.ModeSetting;
import dev.loki.lovisual.core.event.EventHandler;

import java.awt.Color;

@ModuleInfo(name = "PVision", desc = "Highlights players with a colored box", category = ModuleCategory.RENDER)
public class PVision extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Box", "Box", "Glow", "Outline");
    private final ColorSetting color = new ColorSetting("Color", Color.RED);

    @EventHandler
    private void onRender(Render3DEvent event) {
    }
}
