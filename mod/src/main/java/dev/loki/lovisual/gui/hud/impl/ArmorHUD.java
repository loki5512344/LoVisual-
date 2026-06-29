package dev.loki.lovisual.gui.hud.impl;

import dev.loki.lovisual.gui.hud.HudElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ArmorHUD extends HudElement {
    public ArmorHUD() {
        super("ArmorHUD", 2, 2);
        setWidth(80);
        setHeight(20);
    }

    @Override
    public void render(DrawContext ctx, float delta) {
        if (mc.player == null) return;
        int color = getStyleColor(200, 200, 200);
        int xOff = (int) x;

        for (ItemStack stack : mc.player.getArmorItems()) {
            String name = stack.getItem().getName().getString();
            ctx.drawText(mc.textRenderer, name.substring(0, Math.min(3, name.length())), xOff, (int) y, color, false);
            xOff += 20;
        }

        setWidth(xOff - (int) x);
    }
}
