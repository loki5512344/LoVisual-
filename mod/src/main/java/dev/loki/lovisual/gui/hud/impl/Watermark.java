package dev.loki.lovisual.gui.hud.impl;

import dev.loki.lovisual.gui.hud.HudElement;
import net.minecraft.client.gui.DrawContext;

public class Watermark extends HudElement {
    public Watermark() {
        super("Watermark", 2, 2);
        visible = true;
    }

    @Override
    public void render(DrawContext ctx, float delta) {
        String text = "LoVisual";
        int color = getStyleColor(150, 200, 255);
        int fh = mc.textRenderer.fontHeight;
        int tw = mc.textRenderer.getWidth(text);

        ctx.fill((int) x, (int) y, (int) x + tw + 6, (int) y + fh + 4, getThemeBg());
        ctx.drawText(mc.textRenderer, text, (int) x + 3, (int) y + 2, color, true);

        setWidth(tw + 6);
        setHeight(fh + 4);
    }
}
