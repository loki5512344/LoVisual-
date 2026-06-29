package dev.loki.lovisual.gui.hud.impl;

import dev.loki.lovisual.core.event.EventBus;
import dev.loki.lovisual.core.event.impl.MouseClickEvent;
import dev.loki.lovisual.gui.hud.HudElement;
import dev.loki.lovisual.core.event.EventHandler;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

public class ComboCounter extends HudElement {
    private int combo;
    private long lastHit;

    public ComboCounter() {
        super("ComboCounter", 2, 260);
        visible = true;
        EventBus.register(this);
    }

    @EventHandler
    private void onMouse(MouseClickEvent event) {
        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_1 && event.getAction() == 1) {
            combo++;
            lastHit = System.currentTimeMillis();
        }
    }

    @Override
    public void render(DrawContext ctx, float delta) {
        if (System.currentTimeMillis() - lastHit > 3000) {
            combo = 0;
        }

        if (combo < 2) {
            setWidth(0);
            setHeight(0);
            return;
        }

        String text = combo + " Combo";
        int accentColor = getStyleColor(255, 100, 0);
        int fh = mc.textRenderer.fontHeight;
        int tw = mc.textRenderer.getWidth(text);

        ctx.fill((int) x, (int) y, (int) x + tw + 6, (int) y + fh + 4, 0x60000000);
        ctx.drawText(mc.textRenderer, text, (int) x + 3, (int) y + 2, accentColor, true);

        if (combo >= 5 && combo % 5 == 0) {
            ctx.drawText(mc.textRenderer, "HITSPLOSION!", (int) x + 3, (int) y + fh + 6, 0xFFFF4444, true);
            setHeight(fh * 2 + 8);
        } else {
            setHeight(fh + 4);
        }

        setWidth(tw + 6);
    }
}
