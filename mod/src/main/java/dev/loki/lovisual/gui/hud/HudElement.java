package dev.loki.lovisual.gui.hud;

import dev.loki.lovisual.setting.Setting;
import dev.loki.lovisual.setting.impl.ModeSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public abstract class HudElement {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();

    private final String name;
    protected float x, y;
    protected float width, height;
    protected boolean visible;
    protected boolean dragging;
    private float dragOffX, dragOffY;

    protected final ModeSetting style;

    private final List<Setting<?>> settings = new ArrayList<>();

    public HudElement(String name, float x, float y) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.style = new ModeSetting("Style", "Custom", "Custom", "Pulse", "Rockstar", "Skycore", "Shade", "4E");
        settings.add(style);
    }

    public abstract void render(DrawContext ctx, float delta);

    public boolean mouseClicked(double mx, double my, int button) {
        if (mx >= x && mx <= x + width && my >= y && my <= y + height && button == 0) {
            dragging = true;
            dragOffX = (float) mx - x;
            dragOffY = (float) my - y;
            return true;
        }
        return false;
    }

    public void mouseReleased() {
        dragging = false;
    }

    public void updatePosition(double mx, double my) {
        if (dragging) {
            x = (float) Math.max(0, Math.min(mc.getWindow().getScaledWidth() - width, mx - dragOffX));
            y = (float) Math.max(0, Math.min(mc.getWindow().getScaledHeight() - height, my - dragOffY));
        }
    }

    public String getName() { return name; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean v) { this.visible = v; }
    public void setWidth(float w) { this.width = w; }
    public void setHeight(float h) { this.height = h; }
    public List<Setting<?>> getSettings() { return settings; }

    protected int getStyleColor(int baseR, int baseG, int baseB) {
        String s = style.get();
        return switch (s) {
            case "Pulse" -> 0xFF6366F1;
            case "Rockstar" -> 0xFFFF4444;
            case "Skycore" -> 0xFF00AAFF;
            case "Shade" -> 0xFFAA44FF;
            case "4E" -> 0xFF00FF88;
            default -> packColor(baseR, baseG, baseB);
        };
    }

    protected int packColor(int r, int g, int b) {
        return (255 << 24) | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
    }
}
