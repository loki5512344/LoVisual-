package dev.loki.lovisual.gui.hud;

import dev.loki.lovisual.core.event.EventBus;
import dev.loki.lovisual.core.event.impl.Render2DEvent;
import dev.loki.lovisual.core.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class HudManager {
    private static final List<HudElement> elements = new ArrayList<>();

    public static void init() {
        EventBus.register(new HudManager());
    }

    public static void add(HudElement element) {
        elements.add(element);
    }

    public static List<HudElement> getElements() { return elements; }

    @EventHandler
    private void onRender(Render2DEvent event) {
        for (HudElement e : elements) {
            if (!e.isVisible()) continue;
            e.render(event.getContext(), event.getTickCounter().getTickDelta(false));
        }
    }

    public static void onMouseClick(double mx, double my, int button) {
        for (HudElement e : elements) {
            if (e.mouseClicked(mx, my, button)) break;
        }
    }

    public static void onMouseRelease() {
        for (HudElement e : elements) {
            e.mouseReleased();
        }
    }

    public static void updatePositions(double mx, double my) {
        for (HudElement e : elements) {
            e.updatePosition(mx, my);
        }
    }
}
