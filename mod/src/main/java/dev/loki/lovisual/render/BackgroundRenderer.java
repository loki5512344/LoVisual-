package dev.loki.lovisual.render;

import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BackgroundRenderer {
    private static final Random random = new Random();
    private static final List<Particle> particles = new ArrayList<>();
    private static float time;

    private record Particle(float x, float y, float speedX, float speedY, float size, float alpha) {}

    public static void init(int width, int height) {
        if (!particles.isEmpty()) return;
        for (int i = 0; i < 50; i++) {
            particles.add(new Particle(
                random.nextFloat() * width,
                random.nextFloat() * height,
                (random.nextFloat() - 0.5f) * 0.3f,
                (random.nextFloat() - 0.5f) * 0.3f,
                random.nextFloat() * 2f + 1f,
                random.nextFloat() * 0.5f + 0.2f
            ));
        }
    }

    public static void render(DrawContext ctx, int width, int height, float delta) {
        time += delta;

        int w = width;
        int h = height;
        float t = time * 0.002f;

        for (int y = 0; y < h; y += 4) {
            float progress = (float) y / h;
            float wave = (float) (Math.sin(t + progress * Math.PI * 2) * 0.3f + 0.5f);
            int r = (int) (10 + wave * 30);
            int g = (int) (0 + wave * 5);
            int b = (int) (0 + wave * 5);
            int color = (255 << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
            ctx.fill(0, y, w, y + 4, color);
        }

        for (int i = 0; i < particles.size(); i++) {
            Particle p = particles.get(i);
            float nx = p.x + p.speedX * delta;
            float ny = p.y + p.speedY * delta;

            if (nx < 0) nx += width;
            if (nx > width) nx -= width;
            if (ny < 0) ny += height;
            if (ny > height) ny -= height;

            particles.set(i, new Particle(nx, ny, p.speedX, p.speedY, p.size, p.alpha));

            int alpha = (int) (p.alpha * 255);
            int color = (alpha << 24) | (180 << 16) | (30 << 8) | 30;
            ctx.fill((int) nx, (int) ny, (int) (nx + p.size), (int) (ny + p.size), color);
        }
    }
}
