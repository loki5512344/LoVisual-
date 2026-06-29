package dev.loki.lovisual.gui.hud.impl;

import dev.loki.lovisual.gui.hud.HudElement;
import dev.loki.lovisual.module.Module;
import dev.loki.lovisual.module.ModuleManager;
import dev.loki.lovisual.setting.impl.BooleanSetting;
import dev.loki.lovisual.setting.impl.ModeSetting;
import net.minecraft.client.gui.DrawContext;

import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleList extends HudElement {
    private final BooleanSetting rainbow = new BooleanSetting("Rainbow", false);
    private final ModeSetting sortMode = new ModeSetting("Sort", "Length", "Length", "ABC");

    public ModuleList() {
        super("ModuleList", 2, 2);
        visible = true;
        getSettings().add(rainbow);
        getSettings().add(sortMode);
    }

    @Override
    public void render(DrawContext ctx, float delta) {
        List<Module> enabled = ModuleManager.getAll().stream()
                .filter(Module::isEnabled)
                .collect(Collectors.toList());

        if (sortMode.get().equals("Length")) {
            enabled.sort(Comparator.comparingInt(m -> -mc.textRenderer.getWidth(m.getName())));
        } else {
            enabled.sort(Comparator.comparing(m -> m.getName().toLowerCase()));
        }

        int yOff = (int) y;
        int accentColor = getStyleColor(255, 255, 255);
        int fh = mc.textRenderer.fontHeight;
        int maxWidth = 0;

        for (Module mod : enabled) {
            String name = mod.getName();
            int textWidth = mc.textRenderer.getWidth(name);
            if (textWidth > maxWidth) maxWidth = textWidth;

            ctx.fill((int) x, yOff, (int) x + textWidth + 4, yOff + fh + 2, 0x60000000);
            ctx.fill((int) x, yOff, (int) x + 2, yOff + fh + 2, accentColor);

            int color = 0xFFFFFFFF;
            if (rainbow.get()) {
                float hue = (System.currentTimeMillis() % 3600) / 3600f;
                color = Color.HSBtoRGB(hue, 0.8f, 1f);
            }

            ctx.drawText(mc.textRenderer, name, (int) x + 4, yOff + 1, color, true);
            yOff += fh + 3;
        }

        setWidth(maxWidth + 4);
        setHeight(enabled.size() * (fh + 3));
    }
}
