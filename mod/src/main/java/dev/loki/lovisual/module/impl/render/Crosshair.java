package dev.loki.lovisual.module.impl.render;

import dev.loki.lovisual.core.event.impl.Render2DEvent;
import dev.loki.lovisual.module.Module;
import dev.loki.lovisual.module.ModuleCategory;
import dev.loki.lovisual.module.ModuleInfo;
import dev.loki.lovisual.setting.impl.ColorSetting;
import dev.loki.lovisual.setting.impl.ModeSetting;
import dev.loki.lovisual.setting.impl.SliderSetting;
import dev.loki.lovisual.core.event.EventHandler;
import net.minecraft.client.gui.DrawContext;

import java.awt.Color;

@ModuleInfo(name = "Crosshair", desc = "Custom crosshair overlay", category = ModuleCategory.RENDER)
public class Crosshair extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Dynamic", "Dynamic", "Static", "Dot", "Cross");
    private final ColorSetting color = new ColorSetting("Color", Color.RED);
    private final SliderSetting width = new SliderSetting("Width", 2.0, 1.0, 5.0);
    private final SliderSetting gap = new SliderSetting("Gap", 4.0, 0.0, 10.0);

    @EventHandler
    private void onRender(Render2DEvent event) {
        DrawContext context = event.getContext();
        int centerX = mc.getWindow().getScaledWidth() / 2;
        int centerY = mc.getWindow().getScaledHeight() / 2;
        int w = width.get().intValue();
        int g = gap.get().intValue();
        int c = color.get().getRGB();

        switch (mode.get()) {
            case "Cross" -> {
                context.fill(centerX - w, centerY - g - w, centerX + w, centerY - g, c);
                context.fill(centerX - w, centerY + g, centerX + w, centerY + g + w, c);
                context.fill(centerX - g - w, centerY - w, centerX - g, centerY + w, c);
                context.fill(centerX + g, centerY - w, centerX + g + w, centerY + w, c);
            }
            case "Dot" -> context.fill(centerX - w, centerY - w, centerX + w, centerY + w, c);
            case "Static" -> {
                context.fill(centerX - g - w, centerY - w, centerX - g, centerY + w, c);
                context.fill(centerX + g, centerY - w, centerX + g + w, centerY + w, c);
                context.fill(centerX - w, centerY - g - w, centerX + w, centerY - g, c);
                context.fill(centerX - w, centerY + g, centerX + w, centerY + g + w, c);
            }
            case "Dynamic" -> {
                int spread = g + (mc.player != null && mc.player.hurtTime > 0 ? 2 : 0);
                context.fill(centerX - spread - w, centerY - w, centerX - spread, centerY + w, c);
                context.fill(centerX + spread, centerY - w, centerX + spread + w, centerY + w, c);
                context.fill(centerX - w, centerY - spread - w, centerX + w, centerY - spread, c);
                context.fill(centerX - w, centerY + spread, centerX + w, centerY + spread + w, c);
            }
        }
    }
}
