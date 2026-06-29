package dev.loki.lovisual.gui.mainmenu;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AltManagerScreen extends Screen {
    private static final int ACCENT = 0xFFDC1C1C;
    private static final int BTN_NORMAL = 0x90000000;
    private static final int BTN_HOVER = 0x80AA0000;

    private final Screen parent;
    private float time;
    private float fadeAlpha;
    private final List<Particle> particles = new ArrayList<>();
    private final Random random = new Random();

    private List<String> accounts;
    private int selectedIndex = -1;
    private float scrollY;
    private TextFieldWidget addField;

    private record Particle(float x, float y, float speedX, float speedY, float size, float alpha) {}

    public AltManagerScreen(Screen parent) {
        super(Text.literal("Alt Manager"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        accounts = AccountManager.getAccounts();

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

        addField = new TextFieldWidget(textRenderer, width / 2 - 100, height - 60, 160, 20, Text.empty());
        addField.setMaxLength(32);
        addField.setDrawsBackground(false);
        addField.setSuggestion("Username");
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        time += delta;
        fadeAlpha = Math.min(1f, fadeAlpha + delta * 0.02f);

        drawBackground(ctx);
        drawParticles(ctx, delta);
        drawTitle(ctx);
        drawAccountList(ctx, mouseX, mouseY);
        drawActions(ctx, mouseX, mouseY, delta);
        drawAddBar(ctx, mouseX, mouseY, delta);
    }

    private void drawBackground(DrawContext ctx) {
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
        int alpha = (int) (fadeAlpha * 255);
        int color = (alpha << 24) | 0xDC1C1C;
        ctx.drawText(textRenderer, "Alt Manager", 20, 16, color, false);
    }

    private void drawAccountList(DrawContext ctx, int mouseX, int mouseY) {
        int listX = 20;
        int listY = 40;
        int listW = width - 240;
        int listH = height - 100;
        int entryH = 28;

        ctx.fill(listX, listY, listX + listW, listY + listH, 0x60000000);

        int contentH = accounts.size() * entryH;
        int maxScroll = Math.max(0, contentH - listH + 10);
        scrollY = Math.max(-maxScroll, Math.min(0, scrollY));

        ctx.getMatrices().push();
        ctx.enableScissor(listX, listY, listX + listW, listY + listH);

        for (int i = 0; i < accounts.size(); i++) {
            int ey = listY + i * entryH + (int) scrollY;
            if (ey + entryH < listY || ey > listY + listH) continue;

            boolean hover = mouseX >= listX && mouseX <= listX + listW && mouseY >= ey && mouseY <= ey + entryH;
            boolean sel = i == selectedIndex;

            int bg = sel ? 0x80AA0000 : (hover ? 0x60000000 : 0x30000000);
            ctx.fill(listX, ey, listX + listW, ey + entryH, bg);

            if (sel) {
                ctx.fill(listX, ey, listX + 2, ey + entryH, ACCENT);
            }

            int textColor = sel ? 0xFFFFFFFF : (hover ? 0xFFCCCCCC : 0xFF888888);
            ctx.drawText(textRenderer, accounts.get(i), listX + 10, ey + 6, textColor, false);
        }

        ctx.disableScissor();
        ctx.getMatrices().pop();
    }

    private void drawActions(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int ax = width - 200;
        int ay = 40;
        int btnW = 180;
        int btnH = 30;

        String[] actions = {"Login", "Remove", "Back"};
        for (int i = 0; i < actions.length; i++) {
            int by = ay + i * (btnH + 8);
            boolean hover = mouseX >= ax && mouseX <= ax + btnW && mouseY >= by && mouseY <= by + btnH;

            int bg = hover ? BTN_HOVER : BTN_NORMAL;
            ctx.fill(ax, by, ax + btnW, by + btnH, bg);

            int border = hover ? ACCENT : 0x40000000;
            ctx.fill(ax, by, ax + btnW, by + 1, border);
            ctx.fill(ax, by + btnH - 1, ax + btnW, by + btnH, border);
            ctx.fill(ax, by, ax + 1, by + btnH, border);
            ctx.fill(ax + btnW - 1, by, ax + btnW, by + btnH, border);

            int textColor = hover ? 0xFFFFFFFF : 0xFFCCCCCC;
            ctx.drawText(textRenderer, actions[i],
                ax + btnW / 2 - textRenderer.getWidth(actions[i]) / 2,
                by + btnH / 2 - 4, textColor, false);
        }
    }

    private void drawAddBar(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int barX = width / 2 - 130;
        int barY = height - 65;

        ctx.fill(barX - 2, barY - 2, barX + 264, barY + 24, 0x60000000);
        addField.setX(barX);
        addField.setY(barY);
        addField.render(ctx, mouseX, mouseY, delta);

        int btnX = barX + 164;
        int btnW = 100;
        int btnH = 22;
        boolean hover = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= barY && mouseY <= barY + btnH;
        int bg = hover ? BTN_HOVER : BTN_NORMAL;
        ctx.fill(btnX, barY, btnX + btnW, barY + btnH, bg);

        int border = hover ? ACCENT : 0x40000000;
        ctx.fill(btnX, barY, btnX + btnW, barY + 1, border);
        ctx.fill(btnX, barY + btnH - 1, btnX + btnW, barY + btnH, border);
        ctx.fill(btnX, barY, btnX + 1, barY + btnH, border);
        ctx.fill(btnX + btnW - 1, barY, btnX + btnW, barY + btnH, border);

        ctx.drawText(textRenderer, "Add",
            btnX + btnW / 2 - textRenderer.getWidth("Add") / 2,
            barY + btnH / 2 - 4, 0xFFCCCCCC, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int ax = width - 200;
        int ay = 40;
        int btnW = 180;
        int btnH = 30;

        String[] actions = {"Login", "Remove", "Back"};
        for (int i = 0; i < actions.length; i++) {
            int by = ay + i * (btnH + 8);
            if (mouseX >= ax && mouseX <= ax + btnW && mouseY >= by && mouseY <= by + btnH) {
                if (i == 0) handleLogin();
                else if (i == 1) handleRemove();
                else if (i == 2) client.setScreen(parent);
                return true;
            }
        }

        int listX = 20;
        int listY = 40;
        int listW = width - 240;
        int listH = height - 100;
        int entryH = 28;

        if (mouseX >= listX && mouseX <= listX + listW && mouseY >= listY && mouseY <= listY + listH) {
            int idx = (int) ((mouseY - listY - scrollY) / entryH);
            if (idx >= 0 && idx < accounts.size()) {
                selectedIndex = idx;
            }
            return true;
        }

        int barX = width / 2 - 130;
        int barY = height - 65;
        int addBtnX = barX + 164;
        int addBtnW = 100;
        int addBtnH = 22;

        if (mouseX >= addBtnX && mouseX <= addBtnX + addBtnW && mouseY >= barY && mouseY <= barY + addBtnH) {
            handleAdd();
            return true;
        }

        addField.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void handleLogin() {
        if (selectedIndex >= 0 && selectedIndex < accounts.size()) {
            String name = accounts.get(selectedIndex);
            AccountManager.setLastAccount(name);
            client.setScreen(parent);
        }
    }

    private void handleRemove() {
        if (selectedIndex >= 0 && selectedIndex < accounts.size()) {
            String name = accounts.get(selectedIndex);
            AccountManager.removeAccount(name);
            accounts = AccountManager.getAccounts();
            selectedIndex = -1;
        }
    }

    private void handleAdd() {
        String name = addField.getText().trim();
        if (!name.isEmpty()) {
            AccountManager.addAccount(name);
            accounts = AccountManager.getAccounts();
            addField.setText("");
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (addField.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (keyCode == 256) {
            client.setScreen(parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (addField.charTyped(chr, modifiers)) return true;
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int listX = 20;
        int listY = 40;
        int listW = width - 240;
        int listH = height - 100;

        if (mouseX >= listX && mouseX <= listX + listW && mouseY >= listY && mouseY <= listY + listH) {
            scrollY += (float) verticalAmount * 20;
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
