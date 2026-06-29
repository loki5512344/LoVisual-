package dev.loki.lovisual.gui.hud.impl;

import dev.loki.lovisual.gui.hud.HudElement;
import dev.loki.lovisual.setting.impl.ModeSetting;
import net.minecraft.client.gui.DrawContext;

public class Scoreboard extends HudElement {
    public Scoreboard() {
        super("Scoreboard", 2, 2);
        setWidth(140);
        setHeight(60);
    }

    @Override
    public void render(DrawContext ctx, float delta) {
        if (mc.world == null || mc.player == null) return;
        int color = getStyleColor(200, 200, 200);
        ctx.drawText(mc.textRenderer, "Scoreboard", (int) x + 4, (int) y + 4, color, false);
    }
}
