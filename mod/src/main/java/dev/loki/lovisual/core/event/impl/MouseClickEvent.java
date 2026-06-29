package dev.loki.lovisual.core.event.impl;

import dev.loki.lovisual.core.event.Event;

public class MouseClickEvent extends Event {
    private final int button;
    private final int action;
    private final int modifiers;

    public MouseClickEvent(int button, int action, int modifiers) {
        this.button = button;
        this.action = action;
        this.modifiers = modifiers;
    }

    public int getButton() {
        return button;
    }

    public int getAction() {
        return action;
    }

    public int getModifiers() {
        return modifiers;
    }
}
