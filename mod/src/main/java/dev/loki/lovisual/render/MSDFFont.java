package dev.loki.lovisual.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Identifier;

public class MSDFFont {
    private final TextRenderer textRenderer;
    private final Identifier fontId;

    public MSDFFont(Identifier fontId) {
        this.fontId = fontId;
        this.textRenderer = MinecraftClient.getInstance().textRenderer;
    }

    public int getWidth(String text) {
        return textRenderer.getWidth(text);
    }

    public int getHeight() {
        return textRenderer.fontHeight;
    }

    public Identifier getFontId() {
        return fontId;
    }
}
