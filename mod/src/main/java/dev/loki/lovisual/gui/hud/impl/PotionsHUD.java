package dev.loki.lovisual.gui.hud.impl;

import dev.loki.lovisual.gui.hud.HudElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffectInstance;

public class PotionsHUD extends HudElement {
    public PotionsHUD() {
        super("PotionsHUD", 2, 40);
        setWidth(100);
        setHeight(80);
    }

    @Override
    public void render(DrawContext ctx, float delta) {
        if (mc.player == null) return;
        int yOff = (int) y + 4;
        int color = getStyleColor(100, 200, 255);

        for (StatusEffectInstance effect : mc.player.getStatusEffects()) {
            String name = effect.getEffectType().value().getName().getString();
            int seconds = effect.getDuration() / 20;
            String text = name + " " + (seconds / 60) + ":" + (seconds % 60);
            ctx.drawText(mc.textRenderer, text, (int) x + 4, yOff, color, false);
            yOff += 10;
            setWidth(Math.max(getWidth(), mc.textRenderer.getWidth(text) + 12));
        }
        setHeight((yOff - (int) y) + 4);
    }
}
