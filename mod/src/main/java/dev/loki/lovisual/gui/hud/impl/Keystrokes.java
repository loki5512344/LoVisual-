package dev.loki.lovisual.gui.hud.impl;

import dev.loki.lovisual.core.event.EventBus;
import dev.loki.lovisual.core.event.impl.KeyPressEvent;
import dev.loki.lovisual.core.event.impl.MouseClickEvent;
import dev.loki.lovisual.gui.hud.HudElement;
import dev.loki.lovisual.core.event.EventHandler;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class Keystrokes extends HudElement {
    private final Map<Integer, Boolean> pressed = new HashMap<>();

    public Keystrokes() {
        super("Keystrokes", 2, 130);
        visible = true;
        EventBus.register(this);
    }

    @EventHandler
    private void onKey(KeyPressEvent event) {
        pressed.put(event.getKey(), event.getAction() != 0);
    }

    @EventHandler
    private void onMouse(MouseClickEvent event) {
        pressed.put(event.getButton(), event.getAction() != 0);
    }

    @Override
    public void render(DrawContext ctx, float delta) {
        int accentColor = getStyleColor(0, 255, 0);
        int box = 20;
        int gap = 2;
        int xOff = (int) x;

        int col0 = xOff;
        int col1 = xOff + box + gap;
        int col2 = xOff + (box + gap) * 2;
        int col3 = xOff + (box + gap) * 3;
        int row0 = (int) y;
        int row1 = (int) y + box + gap;
        int row2 = (int) y + (box + gap) * 2;
        int cw = box + gap;

        ctx.fill(col1, row0, col1 + box, row0 + box, getKeyColor(GLFW.GLFW_KEY_W, accentColor));
        ctx.drawText(mc.textRenderer, "W", col1 + 6, row0 + 6, 0xFFFFFFFF, true);

        ctx.fill(col0, row1, col0 + box, row1 + box, getKeyColor(GLFW.GLFW_KEY_A, accentColor));
        ctx.drawText(mc.textRenderer, "A", col0 + 6, row1 + 6, 0xFFFFFFFF, true);

        ctx.fill(col1, row1, col1 + box, row1 + box, getKeyColor(GLFW.GLFW_KEY_S, accentColor));
        ctx.drawText(mc.textRenderer, "S", col1 + 6, row1 + 6, 0xFFFFFFFF, true);

        ctx.fill(col2, row1, col2 + box, row1 + box, getKeyColor(GLFW.GLFW_KEY_D, accentColor));
        ctx.drawText(mc.textRenderer, "D", col2 + 6, row1 + 6, 0xFFFFFFFF, true);

        ctx.fill(col1, row2, col1 + box, row2 + box, getKeyColor(GLFW.GLFW_KEY_SPACE, accentColor));
        ctx.drawText(mc.textRenderer, "___", col1 + 4, row2 + 6, 0xFFFFFFFF, true);

        ctx.fill(col0, row2, col0 + box, row2 + box, getKeyColor(0, accentColor));
        ctx.drawText(mc.textRenderer, "LMB", col0 + 1, row2 + 6, 0xFFFFFFFF, true);

        ctx.fill(col2, row2, col2 + box, row2 + box, getKeyColor(1, accentColor));
        ctx.drawText(mc.textRenderer, "RMB", col2 + 1, row2 + 6, 0xFFFFFFFF, true);

        setWidth(box * 4 + gap * 3);
        setHeight(box * 3 + gap * 2);
    }

    private int getKeyColor(int key, int accent) {
        return pressed.getOrDefault(key, false) ? accent : 0xFF000000;
    }
}
