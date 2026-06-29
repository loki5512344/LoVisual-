package dev.loki.lovisual.core.event.impl;

import dev.loki.lovisual.core.event.Event;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;

public class Render3DEvent extends Event {
    private final MatrixStack matrices;
    private final Camera camera;
    private final float tickDelta;

    public Render3DEvent(MatrixStack matrices, Camera camera, float tickDelta) {
        this.matrices = matrices;
        this.camera = camera;
        this.tickDelta = tickDelta;
    }

    public MatrixStack getMatrices() {
        return matrices;
    }

    public Camera getCamera() {
        return camera;
    }

    public float getTickDelta() {
        return tickDelta;
    }
}
