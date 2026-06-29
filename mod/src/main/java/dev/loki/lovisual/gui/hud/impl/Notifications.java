package dev.loki.lovisual.gui.hud.impl;

import dev.loki.lovisual.gui.hud.HudElement;
import dev.loki.lovisual.setting.impl.SliderSetting;
import net.minecraft.client.gui.DrawContext;

import java.util.Iterator;
import java.util.LinkedList;

public class Notifications extends HudElement {
    private static final LinkedList<Notification> queue = new LinkedList<>();
    private final SliderSetting maxVisible = new SliderSetting("Max Visible", 5, 1, 10, 1);

    public Notifications() {
        super("Notifications", 2, 280);
        visible = true;
        getSettings().add(maxVisible);
    }

    public static void info(String text) {
        queue.add(new Notification(text, Type.INFO, System.currentTimeMillis()));
    }

    public static void warn(String text) {
        queue.add(new Notification(text, Type.WARN, System.currentTimeMillis()));
    }

    public static void error(String text) {
        queue.add(new Notification(text, Type.ERROR, System.currentTimeMillis()));
    }

    public static void toggle(String text) {
        queue.add(new Notification(text, Type.TOGGLE, System.currentTimeMillis()));
    }

    @Override
    public void render(DrawContext ctx, float delta) {
        int accentColor = getStyleColor(200, 200, 255);
        int fh = mc.textRenderer.fontHeight;
        int yOff = (int) y;
        int count = 0;
        int max = maxVisible.get().intValue();
        int maxW = 0;

        Iterator<Notification> it = queue.iterator();
        while (it.hasNext()) {
            Notification n = it.next();
            long elapsed = System.currentTimeMillis() - n.time;
            if (elapsed > 5000) {
                it.remove();
                continue;
            }

            if (count >= max) {
                it.remove();
                continue;
            }

            int alpha = elapsed > 4500 ? (int) (255 * (5000 - elapsed) / 500) : 255;
            int bgColor = (alpha << 24) | 0x000000;
            int textColor = getTypeColor(n.type, accentColor);
            String text = "[" + n.type + "] " + n.text;
            int tw = mc.textRenderer.getWidth(text);
            if (tw > maxW) maxW = tw;

            ctx.fill((int) x, yOff, (int) x + tw + 6, yOff + fh + 4, bgColor);
            ctx.drawText(mc.textRenderer, text, (int) x + 3, yOff + 2, textColor, true);

            yOff += fh + 6;
            count++;
        }

        setWidth(maxW + 6);
        setHeight(yOff - (int) y);
    }

    private int getTypeColor(Type type, int accent) {
        return switch (type) {
            case INFO -> accent;
            case WARN -> 0xFFFFFF00;
            case ERROR -> 0xFFFF4444;
            case TOGGLE -> 0xFF00FF88;
        };
    }

    private enum Type { INFO, WARN, ERROR, TOGGLE }

    private static class Notification {
        final String text;
        final Type type;
        final long time;

        Notification(String text, Type type, long time) {
            this.text = text;
            this.type = type;
            this.time = time;
        }
    }
}
