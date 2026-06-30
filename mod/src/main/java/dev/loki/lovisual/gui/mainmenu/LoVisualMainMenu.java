package dev.loki.lovisual.gui.mainmenu;

import dev.loki.lovisual.LoVisual;
import dev.loki.lovisual.render.Renderer2D;
import dev.loki.lovisual.render.animation.Animation;
import dev.loki.lovisual.render.animation.Easing;
import net.minecraft.client.gui.Click;
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
    // Color palette
    private static final int C_BLACK = 0xFF0B0B0B;
    private static final int C_DARK_RED = 0xFF3A0A0A;
    private static final int C_RED = 0xFFE53935;
    private static final int C_BRIGHT_RED = 0xFFFF4D4D;
    private static final int C_WHITE = 0xFFFFFFFF;

    // Glass panel
    private static final int PANEL_BG = 0xCC0D0D0D;
    private static final int PANEL_BORDER = 0x44E53935;
    private static final int PANEL_WIDTH = 280;
    private static final int PANEL_RADIUS = 12;

    // Buttons
    private static final String[] BTN_LABELS = {
        "Singleplayer", "Multiplayer", "Alt Manager", "Options", "Quit"
    };
    private static final int BTN_W = 200;
    private static final int BTN_H = 36;
    private static final int BTN_RADIUS = 8;

    // Particles
    private static final Random RANDOM = new Random();
    private final List<FogParticle> fog = new ArrayList<>();
    private final List<Ember> embers = new ArrayList<>();
    private float time;

    // Animations
    private final Animation fadeIn = new Animation(Easing.EASE_OUT_CUBIC, 600);
    private final Animation panelSlide = new Animation(Easing.EASE_OUT_CUBIC, 300);

    // Mouse parallax
    private float mouseSmoothX;
    private float mouseSmoothY;

    // Cached animated panel Y for click detection
    private int animatedPanelY;

    private record FogParticle(float x, float y, float speedX, float speedY, float size, float alpha, float phase) {}
    private record Ember(float x, float y, float speedY, float drift, float size, float alpha, float phase) {}

    public LoVisualMainMenu() {
        super(Text.literal("LoVisual Main Menu"));
        // btn animations removed
    }

    @Override
    protected void init() {
        fog.clear();
        embers.clear();

        for (int i = 0; i < 30; i++) {
            fog.add(new FogParticle(
                RANDOM.nextFloat() * width,
                RANDOM.nextFloat() * height,
                (RANDOM.nextFloat() - 0.5f) * 0.15f,
                (RANDOM.nextFloat() - 0.5f) * 0.08f - 0.04f,
                RANDOM.nextFloat() * 3f + 1f,
                RANDOM.nextFloat() * 0.25f + 0.05f,
                RANDOM.nextFloat() * (float) Math.PI * 2
            ));
        }

        for (int i = 0; i < 8; i++) {
            embers.add(new Ember(
                RANDOM.nextFloat() * width,
                RANDOM.nextFloat() * height,
                -(RANDOM.nextFloat() * 0.5f + 0.2f),
                (RANDOM.nextFloat() - 0.5f) * 0.3f,
                RANDOM.nextFloat() * 2f + 1f,
                RANDOM.nextFloat() * 0.4f + 0.1f,
                RANDOM.nextFloat() * (float) Math.PI * 2
            ));
        }

        mouseSmoothX = width / 2f;
        mouseSmoothY = height / 2f;

        fadeIn.reset();
        panelSlide.reset();
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        time += delta * 0.001f;
        fadeIn.update(delta);
        panelSlide.update(delta);

        mouseSmoothX += (mouseX - mouseSmoothX) * delta * 0.003f;
        mouseSmoothY += (mouseY - mouseSmoothY) * delta * 0.003f;
        float px = (mouseSmoothX / width - 0.5f) * 2;
        float py = (mouseSmoothY / height - 0.5f) * 2;

        float alpha = fadeIn.getValue();

        drawBackground(ctx, px, py, delta);
        drawLogo(ctx, alpha);
        drawPanel(ctx, mouseX, mouseY, delta, alpha);
        drawInfo(ctx, alpha);
    }

    // ======================== BACKGROUND ========================

    private void drawBackground(DrawContext ctx, float px, float py, float delta) {
        ctx.fillGradient(0, 0, width, height / 2, C_BLACK, C_DARK_RED);
        ctx.fillGradient(0, height / 2, width, height, C_DARK_RED, C_BLACK);

        // Fog particles
        for (int i = 0; i < fog.size(); i++) {
            FogParticle p = fog.get(i);
            float nx = p.x + p.speedX * delta;
            float ny = p.y + p.speedY * delta;

            if (nx < -10) nx += width + 20;
            if (nx > width + 10) nx -= width + 20;
            if (ny < -10) ny += height + 20;
            if (ny > height + 10) ny -= height + 20;

            fog.set(i, new FogParticle(nx, ny, p.speedX, p.speedY, p.size, p.alpha, p.phase));
            float flicker = (float) Math.sin(time * 2 + p.phase) * 0.3f + 0.7f;
            int fa = (int) (p.alpha * flicker * 200);
            ctx.fill((int) nx, (int) ny, (int) (nx + p.size), (int) (ny + p.size), (fa << 24) | 0xFF4D4D);
        }

        // Embers
        for (int i = 0; i < embers.size(); i++) {
            Ember e = embers.get(i);
            float ex = e.x + (float) Math.sin(time + e.phase) * e.drift * 50;
            float ey = e.y + e.speedY * delta;
            if (ey < -10) {
                ey = height + 10;
                ex = RANDOM.nextFloat() * width;
            }
            float flicker = (float) Math.sin(time * 3 + e.phase) * 0.4f + 0.6f;
            int ea = (int) (e.alpha * flicker * 200);
            embers.set(i, new Ember(ex, ey, e.speedY, e.drift, e.size, e.alpha, e.phase));
            ctx.fill((int) ex, (int) ey, (int) (ex + e.size), (int) (ey + e.size), (ea << 24) | 0xFF4D4D);
        }
    }

    // ======================== LOGO ========================

    private void drawLogo(DrawContext ctx, float alpha) {
        String title = "LoVisual";
        float scale = Math.min(3.5f, Math.max(1.5f, width / 120f));
        int titleX = (int) ((width - textRenderer.getWidth(title) * scale) / 2);
        int titleY = Math.max(10, height / 6);

        ctx.getMatrices().pushMatrix();
        ctx.getMatrices().translate(titleX, titleY);
        ctx.getMatrices().scale(scale, scale);

        ctx.drawText(textRenderer, title, 0, 0, (int) (255 * alpha) << 24 | 0xFFFFFFFF, false);

        ctx.getMatrices().popMatrix();

        String version = "v" + LoVisual.VERSION;
        int verAlpha = (int) (alpha * 180);
        ctx.drawText(textRenderer, version,
            (int) ((width - textRenderer.getWidth(version)) / 2f),
            titleY + (int) (12 * scale) + 8,
            (verAlpha << 24) | 0xCCCCCC, false);
    }

    // ======================== INFO ========================

    private void drawInfo(DrawContext ctx, float alpha) {
        Renderer2D r2d = new Renderer2D(ctx);

        String line1 = "Minecraft 1.21.11";
        String line2 = "LoVisual Client";

        int a = (int) (alpha * 150);
        int color = (a << 24) | 0x888888;

        r2d.drawCenteredText(line1, width / 2f, height - 28, color);
        r2d.drawCenteredText(line2, width / 2f, height - 16, color);

        // Account info
        String last = AccountManager.getLastAccount();
        if (last != null && !last.isEmpty()) {
            int aa = (int) (alpha * 180);
            ctx.drawText(textRenderer, "Logged in as: " + last,
                width - textRenderer.getWidth("Logged in as: " + last) - 10,
                height - 20, (aa << 24) | 0x888888, false);
        }
    }

    // ======================== PANEL & BUTTONS ========================

    private void drawPanel(DrawContext ctx, int mouseX, int mouseY, float delta, float alpha) {
        int panelX = (width - PANEL_WIDTH) / 2;
        int panelY = getPanelY();
        int panelH = 50 + BTN_LABELS.length * (BTN_H + 8) + 20;

        float panelProgress = panelSlide.getValue();
        int py = (int) (panelY + (1 - panelProgress) * 50);
        animatedPanelY = py;

        Renderer2D r2d = new Renderer2D(ctx);

        // Panel shadow
        r2d.drawRoundedRect(panelX + 3, py + 3, PANEL_WIDTH, panelH, PANEL_RADIUS, 0x40000000);

        // Panel background
        r2d.drawRoundedRect(panelX, py, PANEL_WIDTH, panelH, PANEL_RADIUS, PANEL_BG);

        // Panel border glow
        r2d.drawRoundedBorder(panelX, py, PANEL_WIDTH, panelH, PANEL_RADIUS, 1.5f, PANEL_BORDER);

        // Top accent line
        ctx.fillGradient(panelX + 20, py + 1, panelX + PANEL_WIDTH - 20, py + 2, 0xFFE53935, 0x44E53935);

        // Title in panel
        String panelTitle = "LoVisual Client";
        int titleW = textRenderer.getWidth(panelTitle);
        ctx.drawText(textRenderer, panelTitle,
            panelX + (PANEL_WIDTH - titleW) / 2,
            py + 12,
            (int) (255 * alpha) << 24 | 0xCCCCCC, false);

        // Separator line
        int sepY = py + 32;
        for (int i = 0; i < PANEL_WIDTH - 40; i++) {
            float t = (float) i / (PANEL_WIDTH - 40);
            int sa = (int) ((1 - Math.abs(t - 0.5f) * 2) * 60);
            ctx.fill(panelX + 20 + i, sepY, panelX + 21 + i, sepY + 1, (sa << 24) | 0xE53935);
        }

        drawButtons(ctx, mouseX, mouseY, delta, alpha, panelX, py);
    }

    private void drawButtons(DrawContext ctx, int mouseX, int mouseY, float delta, float alpha, int panelX, int panelY) {
        Renderer2D r2d = new Renderer2D(ctx);
        int btnStartY = panelY + 44;

        for (int i = 0; i < BTN_LABELS.length; i++) {
            int bx = panelX + (PANEL_WIDTH - BTN_W) / 2;
            int by = btnStartY + i * (BTN_H + 8);

            boolean hover = mouseX >= bx && mouseX <= bx + BTN_W && mouseY >= by && mouseY <= by + BTN_H;

            int drawX = bx;
            int drawY = by;

            r2d.drawRoundedRect(drawX + 2, drawY + 2, BTN_W, BTN_H, BTN_RADIUS, 0x60000000);

            int btnBg = hover ? 0xFF1A0000 : 0xCC111111;
            r2d.drawRoundedRect(drawX, drawY, BTN_W, BTN_H, BTN_RADIUS, btnBg);

            if (hover) {
                r2d.drawRoundedBorder(drawX, drawY, BTN_W, BTN_H, BTN_RADIUS, 1.5f, 0xFFFF4D4D);
            } else {
                r2d.drawRoundedBorder(drawX, drawY, BTN_W, BTN_H, BTN_RADIUS, 1f, 0x33333333);
            }

            int textColor = hover ? C_WHITE : 0xFFCCCCCC;
            ctx.drawText(textRenderer, BTN_LABELS[i],
                drawX + 20,
                drawY + BTN_H / 2 - 4,
                (int) (255 * alpha) << 24 | (textColor & 0xFFFFFF),
                false);
        }
    }

    // ======================== INTERACTION ========================

    @Override
    public boolean mouseClicked(Click click, boolean handled) {
        double mx = click.x();
        double my = click.y();
        int panelX = (width - PANEL_WIDTH) / 2;
        int py = animatedPanelY;
        int btnStartY = py + 44;

        for (int i = 0; i < BTN_LABELS.length; i++) {
            int bx = panelX + (PANEL_WIDTH - BTN_W) / 2;
            int by = btnStartY + i * (BTN_H + 8);
            if (mx >= bx && mx <= bx + BTN_W && my >= by && my <= by + BTN_H) {
                handleButton(i);
                return true;
            }
        }
        return super.mouseClicked(click, handled);
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

    // ======================== LAYOUT ========================

    private int getPanelY() {
        int totalHeight = 50 + BTN_LABELS.length * (BTN_H + 8) + 20;
        return Math.max(40, (height / 3) + 20);
    }

}
