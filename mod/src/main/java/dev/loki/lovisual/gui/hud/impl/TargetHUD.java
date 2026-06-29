package dev.loki.lovisual.gui.hud.impl;

import dev.loki.lovisual.gui.hud.HudElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;

public class TargetHUD extends HudElement {
    public TargetHUD() {
        super("TargetHUD", 2, 30);
        visible = true;
    }

    @Override
    public void render(DrawContext ctx, float delta) {
        if (!(mc.targetedEntity instanceof LivingEntity target)) {
            setWidth(0);
            setHeight(0);
            return;
        }

        String name = target.getDisplayName().getString();
        float health = target.getHealth();
        float maxHealth = target.getMaxHealth();
        String healthStr = String.format("%.0f/%.0f", health, maxHealth);

        int accentColor = getStyleColor(255, 80, 80);
        int fh = mc.textRenderer.fontHeight;
        int nameWidth = mc.textRenderer.getWidth(name);
        int healthWidth = mc.textRenderer.getWidth(healthStr);
        int boxWidth = Math.max(nameWidth, healthWidth) + 10;
        int boxHeight = fh * 2 + 8;

        ctx.fill((int) x, (int) y, (int) x + boxWidth, (int) y + boxHeight, getThemeBg());
        ctx.fill((int) x, (int) y, (int) x + 2, (int) y + boxHeight, accentColor);

        ctx.drawText(mc.textRenderer, name, (int) x + 6, (int) y + 3, 0xFFFFFFFF, true);
        ctx.drawText(mc.textRenderer, healthStr, (int) x + 6, (int) y + fh + 6, accentColor, true);

        float healthPct = Math.min(1, health / maxHealth);
        int barWidth = boxWidth - 12;
        int barX = (int) x + 6;
        int barY = (int) y + boxHeight - 4;
        ctx.fill(barX, barY, barX + barWidth, barY + 2, 0xFF333333);
        ctx.fill(barX, barY, barX + (int) (barWidth * healthPct), barY + 2, accentColor);

        setWidth(boxWidth);
        setHeight(boxHeight);
    }
}
