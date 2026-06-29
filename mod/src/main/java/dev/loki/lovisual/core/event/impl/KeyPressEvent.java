package dev.loki.lovisual.core.event.impl;

import dev.loki.lovisual.core.event.Event;

public class KeyPressEvent extends Event {
    private final int key;
    private final int action;

    public KeyPressEvent(int key, int action) {
        this.key = key;
        this.action = action;
    }

    public int getKey() {
        return key;
    }

    public int getAction() {
        return action;
    }

    public boolean isPressed() {
        return action == 1;
    }
}
