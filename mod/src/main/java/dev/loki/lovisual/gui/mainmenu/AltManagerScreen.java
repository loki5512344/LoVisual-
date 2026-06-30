package dev.loki.lovisual.gui.mainmenu;

import dev.loki.lovisual.render.BackgroundRenderer;
import dev.loki.lovisual.render.Renderer2D;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;

import java.util.List;

public class AltManagerScreen extends Screen {
    private static final int ACCENT = 0xFFDC1C1C;
    private static final int BG = 0x90000000;
    private static final int BG_HOVER = 0x80AA0000;

    private final Screen parent;
    private float fadeAlpha;

    private List<String> accounts;
    private int selectedIndex = -1;
    private float scrollY;
    private TextFieldWidget addField;

    public AltManagerScreen(Screen parent) {
        super(Text.literal("Alt Manager"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        accounts = AccountManager.getAccounts();
        BackgroundRenderer.init(width, height);

        int fw = Math.min(160, Math.max(80, width / 3));
        addField = new TextFieldWidget(textRenderer, width / 2 - fw / 2, height - 60, fw, 20, Text.empty());
        addField.setMaxLength(32);
        addField.setDrawsBackground(false);
        addField.setSuggestion("Username");
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        fadeAlpha = Math.min(1f, fadeAlpha + delta * 0.02f);

        BackgroundRenderer.render(ctx, width, height, delta);
        drawTitle(ctx);
        drawAccountList(ctx, mouseX, mouseY);
        drawActions(ctx, mouseX, mouseY);
        drawAddBar(ctx, mouseX, mouseY, delta);
    }

    private void drawTitle(DrawContext ctx) {
        int alpha = (int) (fadeAlpha * 255);
        int color = (alpha << 24) | 0xDC1C1C;
        ctx.drawText(textRenderer, "Alt Manager", 20, 16, color, false);
    }

    private void drawAccountList(DrawContext ctx, int mouseX, int mouseY) {
        int btnArea = Math.min(160, Math.max(80, width / 3));
        int listX = 20;
        int listY = 40;
        int listW = width - btnArea - 40;
        int listH = height - 100;
        int entryH = 28;

        ctx.fill(listX, listY, listX + listW, listY + listH, 0x60000000);

        int contentH = accounts.size() * entryH;
        int maxScroll = Math.max(0, contentH - listH + 10);
        scrollY = Math.max(-maxScroll, Math.min(0, scrollY));

        ctx.getMatrices().pushMatrix();
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
        ctx.getMatrices().popMatrix();
    }

    private void drawActions(DrawContext ctx, int mouseX, int mouseY) {
        int btnW = Math.min(140, Math.max(60, width / 5));
        int btnH = Math.max(16, btnW / 5);
        int gap = Math.max(4, btnH / 3);
        int radius = Math.max(3, btnH / 3);
        int ax = width - btnW - 20;
        int ay = 40;

        Renderer2D r2d = new Renderer2D(ctx);
        String[] actions = {"Login", "Remove", "Back"};

        for (int i = 0; i < actions.length; i++) {
            int by = ay + i * (btnH + gap);
            boolean hover = mouseX >= ax && mouseX <= ax + btnW && mouseY >= by && mouseY <= by + btnH;

            int bg = hover ? BG_HOVER : BG;
            int alpha = (int) (fadeAlpha * ((bg >> 24) & 0xFF));
            int bgFade = (alpha << 24) | (bg & 0xFFFFFF);

            r2d.drawRoundedRect(ax, by, btnW, btnH, radius, bgFade);
            r2d.drawRoundedBorder(ax, by, btnW, btnH, radius, 1, hover ? ACCENT : 0x40000000);

            int textColor = hover ? 0xFFFFFFFF : 0xFFCCCCCC;
            ctx.drawText(textRenderer, actions[i],
                ax + btnW / 2 - textRenderer.getWidth(actions[i]) / 2,
                by + btnH / 2 - 4,
                (int) (fadeAlpha * 255) << 24 | (textColor & 0xFFFFFF), false);
        }
    }

    private void drawAddBar(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int fw = Math.min(160, Math.max(80, width / 3));
        int barX = width / 2 - fw / 2 - 55;
        int barY = height - 65;

        int btnW = Math.min(100, Math.max(50, fw / 2));
        int btnH = Math.max(16, btnW / 5);
        int radius = Math.max(3, btnH / 3);

        ctx.fill(barX - 2, barY - 2, barX + fw + btnW + 12, barY + 24, 0x60000000);
        addField.setX(barX + 2);
        addField.setY(barY + 2);
        addField.render(ctx, mouseX, mouseY, delta);

        int btnX = barX + fw + 8;
        int addBtnW = btnW;
        int addBtnH = btnH;
        boolean hover = mouseX >= btnX && mouseX <= btnX + addBtnW && mouseY >= barY + 2 && mouseY <= barY + 2 + addBtnH;

        Renderer2D r2d = new Renderer2D(ctx);
        int bg = hover ? BG_HOVER : BG;
        r2d.drawRoundedRect(btnX, barY + 2, addBtnW, addBtnH, radius, bg);
        r2d.drawRoundedBorder(btnX, barY + 2, addBtnW, addBtnH, radius, 1, hover ? ACCENT : 0x40000000);

        ctx.drawText(textRenderer, "Add",
            btnX + addBtnW / 2 - textRenderer.getWidth("Add") / 2,
            barY + 2 + addBtnH / 2 - 4, 0xFFCCCCCC, false);
    }

    @Override
    public boolean mouseClicked(Click click, boolean handled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();

        int btnW = Math.min(140, Math.max(60, width / 5));
        int btnH = Math.max(16, btnW / 5);
        int ax = width - btnW - 20;
        int ay = 40;

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
        int listW = width - btnW - 60;
        int listH = height - 100;
        int entryH = 28;

        if (mouseX >= listX && mouseX <= listX + listW && mouseY >= listY && mouseY <= listY + listH) {
            int idx = (int) ((mouseY - listY - scrollY) / entryH);
            if (idx >= 0 && idx < accounts.size()) {
                selectedIndex = idx;
            }
            return true;
        }

        int fw = Math.min(160, Math.max(80, width / 3));
        int barX = width / 2 - fw / 2 - 55;
        int barY = height - 65;
        int addBtnX = barX + fw + 8;
        int addBtnW = Math.min(100, Math.max(50, fw / 2));
        int addBtnH = Math.max(16, addBtnW / 5);

        if (mouseX >= addBtnX && mouseX <= addBtnX + addBtnW && mouseY >= barY + 2 && mouseY <= barY + 2 + addBtnH) {
            handleAdd();
            return true;
        }

        addField.mouseClicked(click, true);
        return super.mouseClicked(click, handled);
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
    public boolean keyPressed(KeyInput keyInput) {
        if (addField.keyPressed(keyInput)) return true;
        if (keyInput.key() == 256) {
            client.setScreen(parent);
            return true;
        }
        return super.keyPressed(keyInput);
    }

    @Override
    public boolean charTyped(CharInput charInput) {
        if (addField.charTyped(charInput)) return true;
        return super.charTyped(charInput);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int listX = 20;
        int listY = 40;
        int btnW = Math.min(140, Math.max(60, width / 5));
        int listW = width - btnW - 60;
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
