package dev.loki.lovisual.gui.mainmenu;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LoVisualMainMenu extends Screen {
    private static final int ACCENT = 0xFFDC1C1C;
    private static final int BTN_NORMAL = 0x90000000;
    private static final int BTN_HOVER = 0x80AA0000;
    private float time;
    private float fadeAlpha;
    private final List<Particle> particles = new ArrayList<>();
    private final Random random = new Random();

    private record Particle(float x, float y, float speedX, float speedY, float size, float alpha) {}

    public LoVisualMainMenu() {
        super(Text.literal("LoVisual Main Menu"));
    }

    @Override
    protected void init() {
        particles.clear();
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

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        time += delta;
        fadeAlpha = Math.min(1f, fadeAlpha + delta * 0.02f);

        drawBackground(ctx, mouseX, mouseY, delta);
        drawParticles(ctx, delta);
        drawTitle(ctx);
        drawButtons(ctx, mouseX, mouseY, delta);
        drawAccountInfo(ctx);
    }

    private void drawBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {
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
    }

    private void drawParticles(DrawContext ctx, float delta) {
        for (int i = 0; i < particles.size(); i++) {
            Particle p = particles.get(i);
            float nx = p.x + p.speedX * delta;
            float ny = p.y + p.speedY * delta;

            if (nx < 0) nx += width;
            if (nx > width) nx -= width;
            if (ny < 0) ny += height;
            if (ny > height) ny -= height;

            particles.set(i, new Particle(nx, ny, p.speedX, p.speedY, p.size, p.alpha));

            int alpha = (int) (p.alpha * fadeAlpha * 255);
            int color = (alpha << 24) | (180 << 16) | (30 << 8) | 30;
            ctx.fill((int) nx, (int) ny, (int) (nx + p.size), (int) (ny + p.size), color);
        }
    }

    private void drawTitle(DrawContext ctx) {
        int titleX = 40;
        int titleY = 50;

        String title = "LoVisual";
        float scale = 3.5f;

        ctx.getMatrices().push();
        ctx.getMatrices().translate(titleX, titleY, 0);
        ctx.getMatrices().scale(scale, scale, 1);

        int alpha = (int) (fadeAlpha * 255);
        int color = (alpha << 24) | 0xDC1C1C;
        ctx.drawText(textRenderer, title, 0, 0, color, false);
        ctx.getMatrices().pop();

        String version = "v1.0.0";
        int verAlpha = (int) (fadeAlpha * 180);
        int verColor = (verAlpha << 24) | 0x888888;
        ctx.drawText(textRenderer, version, titleX + 2, titleY + (int) (12 * scale) + 6, verColor, false);
    }

    private void drawButtons(DrawContext ctx, int mouseX, int mouseY, float delta) {
        String[] labels = {"Singleplayer", "Multiplayer", "Alt Manager", "Options", "Quit"};
        int btnW = 220;
        int btnH = 36;
        int centerX = width / 2;
        int startY = height / 2 - ((labels.length * (btnH + 8)) / 2);

        for (int i = 0; i < labels.length; i++) {
            int bx = centerX - btnW / 2;
            int by = startY + i * (btnH + 8);

            boolean hover = mouseX >= bx && mouseX <= bx + btnW && mouseY >= by && mouseY <= by + btnH;

            int bg = hover ? BTN_HOVER : BTN_NORMAL;
            int border = hover ? ACCENT : 0x40000000;

            int alpha = (int) (fadeAlpha * ((bg >> 24) & 0xFF));
            int bgFade = (alpha << 24) | (bg & 0xFFFFFF);

            ctx.fill(bx, by, bx + btnW, by + btnH, bgFade);

            int bw = 1;
            if (hover) {
                float pulse = (float) (Math.sin(time * 0.005) * 0.3 + 0.7);
                int ba = (int) (pulse * 255);
                border = (ba << 24) | 0xAA0000;
                bw = 2;
            }
            ctx.fill(bx, by, bx + btnW, by + bw, border);
            ctx.fill(bx, by + btnH - bw, bx + btnW, by + btnH, border);
            ctx.fill(bx, by, bx + bw, by + btnH, border);
            ctx.fill(bx + btnW - bw, by, bx + btnW, by + btnH, border);

            int textColor = hover ? 0xFFFFFFFF : 0xFFCCCCCC;
            ctx.drawText(textRenderer, labels[i],
                bx + btnW / 2 - textRenderer.getWidth(labels[i]) / 2,
                by + btnH / 2 - 4,
                (int) (fadeAlpha * 255) << 24 | (textColor & 0xFFFFFF), false);
        }
    }

    private void drawAccountInfo(DrawContext ctx) {
        String last = AccountManager.getLastAccount();
        if (last == null || last.isEmpty()) return;

        String info = "Logged in as: " + last;
        int alpha = (int) (fadeAlpha * 180);
        int color = (alpha << 24) | 0x888888;
        ctx.drawText(textRenderer, info,
            width - textRenderer.getWidth(info) - 10,
            height - 20,
            color, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        String[] labels = {"Singleplayer", "Multiplayer", "Alt Manager", "Options", "Quit"};
        int btnW = 220;
        int btnH = 36;
        int centerX = width / 2;
        int startY = height / 2 - ((labels.length * (btnH + 8)) / 2);

        for (int i = 0; i < labels.length; i++) {
            int bx = centerX - btnW / 2;
            int by = startY + i * (btnH + 8);

            if (mouseX >= bx && mouseX <= bx + btnW && mouseY >= by && mouseY <= by + btnH) {
                handleButton(i);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void handleButton(int index) {
        switch (index) {
            case 0 -> client.setScreen(new SelectWorldScreen(this));
            case 1 -> client.setScreen(new MultiplayerScreen(this));
            case 2 -> client.setScreen(new AltManagerScreen(this));
            case 3 -> client.setScreen(new OptionsScreen(this, client.options));
            case 4 -> client.scheduleStop();
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
