package dev.loki.lovisual.gui.hud.impl;

import dev.loki.lovisual.gui.hud.HudElement;
import net.minecraft.client.gui.DrawContext;

public class Coords extends HudElement {
    public Coords() {
        super("Coords", 2, 195);
        visible = true;
    }

    @Override
    public void render(DrawContext ctx, float delta) {
        if (mc.player == null || mc.world == null) return;

        int accentColor = getStyleColor(255, 200, 50);
        int fh = mc.textRenderer.fontHeight;
        int yOff = (int) y;

        String coords = String.format("XYZ %.1f / %.1f / %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ());
        String direction = getDirection(mc.player.getYaw());
        String biome = mc.world.getBiome(mc.player.getBlockPos())
                .getKey().map(k -> k.getValue().getPath()).orElse("unknown");

        String[] lines = {coords, direction, biome};

        for (String line : lines) {
            ctx.fill((int) x, yOff, (int) x + mc.textRenderer.getWidth(line) + 6, yOff + fh + 2, 0x60000000);
            ctx.drawText(mc.textRenderer, line, (int) x + 3, yOff + 1, accentColor, true);
            yOff += fh + 3;
        }

        setWidth(120);
        setHeight(yOff - (int) y);
    }

    private String getDirection(float yaw) {
        yaw = ((yaw % 360) + 360) % 360;
        if (yaw < 45 || yaw >= 315) return "Direction: South (S)";
        if (yaw < 135) return "Direction: West (W)";
        if (yaw < 225) return "Direction: North (N)";
        return "Direction: East (E)";
    }
}
