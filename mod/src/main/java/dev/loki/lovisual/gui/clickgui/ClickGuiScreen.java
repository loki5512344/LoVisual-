package dev.loki.lovisual.gui.clickgui;

import dev.loki.lovisual.module.Module;
import dev.loki.lovisual.module.ModuleCategory;
import dev.loki.lovisual.module.ModuleManager;
import dev.loki.lovisual.setting.Setting;
import dev.loki.lovisual.setting.impl.BooleanSetting;
import dev.loki.lovisual.setting.impl.ModeSetting;
import dev.loki.lovisual.setting.impl.SliderSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class ClickGuiScreen extends Screen {
    private static final int CHIP_HEIGHT = 24;
    private static final int CARD_WIDTH = 120;
    private static final int CARD_HEIGHT = 30;
    private static final int GAP = 6;
    private static final int SEARCH_HEIGHT = 22;
    private static final int SETTINGS_WIDTH = 200;

    private String search = "";
    private ModuleCategory selectedCategory;
    private Module selectedModule;
    private Module bindModule;
    private int columns = 2;
    private float scrollY;
    private boolean dragging;
    private int dragStartMouseY;
    private float dragStartScrollY;

    private Setting<?> draggingSlider;

    public ClickGuiScreen() {
        super(Text.literal("ClickGUI"));
    }

    private List<Module> getVisible() {
        List<Module> result = new ArrayList<>();
        for (Module m : ModuleManager.getAll()) {
            if (selectedCategory != null && m.getCategory() != selectedCategory) continue;
            if (!search.isEmpty() && !m.getName().toLowerCase().contains(search.toLowerCase())) continue;
            result.add(m);
        }
        return result;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);

        if (dragging) {
            scrollY = dragStartScrollY + (mouseY - dragStartMouseY) * 0.5f;
            scrollY = MathHelper.clamp(scrollY, -3000, 0);
        }

        if (draggingSlider instanceof SliderSetting slider) {
            updateSliderFromMouse(slider, mouseX);
        }

        if (bindModule != null) {
            drawBindOverlay(ctx);
            return;
        }

        int contentWidth = columns * (CARD_WIDTH + GAP) - GAP;
        int startX = (width - contentWidth) / 2;

        drawSearchBar(ctx, startX, contentWidth);
        drawChips(ctx, startX, mouseX, mouseY);

        if (selectedModule != null) {
            drawSettings(ctx, mouseX, mouseY);
        } else {
            drawModules(ctx, startX, mouseX, mouseY);
        }
    }

    private void drawSearchBar(DrawContext ctx, int startX, int contentWidth) {
        int sw = Math.min(280, contentWidth);
        int sx = startX + (contentWidth - sw) / 2;
        int sy = 10;

        ctx.fill(sx, sy, sx + sw, sy + SEARCH_HEIGHT, 0x80000000);
        ctx.drawText(textRenderer, "> " + search + ((System.currentTimeMillis() % 1000 < 500) ? "_" : ""),
            sx + 6, sy + 6, 0xFFCCCCCC, false);
    }

    private void drawChips(DrawContext ctx, int startX, int mx, int my) {
        int cy = 10 + SEARCH_HEIGHT + 6;
        int cx = startX;

        for (ModuleCategory cat : ModuleCategory.values()) {
            String name = cat.name();
            int cw = textRenderer.getWidth(name) + 20;
            boolean hover = mx >= cx && mx <= cx + cw && my >= cy && my <= cy + CHIP_HEIGHT;
            boolean sel = cat == selectedCategory;

            ctx.fill(cx, cy, cx + cw, cy + CHIP_HEIGHT, sel ? 0xFFAA0000 : hover ? 0x80000000 : 0x50000000);
            ctx.drawText(textRenderer, name, cx + 10, cy + 7, sel ? 0xFFFFFFFF : 0xFFAAAAAA, false);

            cx += cw + GAP;
        }
    }

    private void drawModules(DrawContext ctx, int startX, int mx, int my) {
        List<Module> visible = getVisible();
        int gridY = 10 + SEARCH_HEIGHT + 6 + CHIP_HEIGHT + GAP;

        int col = 0;
        int row = 0;

        for (Module m : visible) {
            int cx = startX + col * (CARD_WIDTH + GAP);
            int cy = (int) (gridY + row * (CARD_HEIGHT + GAP) + scrollY);

            if (cy + CARD_HEIGHT > gridY && cy < height) {
                boolean hover = mx >= cx && mx <= cx + CARD_WIDTH && my >= cy && my <= cy + CARD_HEIGHT;

                int bg = m.isEnabled() ? (hover ? 0xCC440000 : 0xCC220000) : (hover ? 0x80000000 : 0x40000000);
                int border = m.isEnabled() ? 0xFFAA0000 : (hover ? 0x60000000 : 0x20000000);

                ctx.fill(cx, cy, cx + CARD_WIDTH, cy + CARD_HEIGHT, bg);
                ctx.fill(cx, cy, cx + CARD_WIDTH, cy + 1, border);
                ctx.fill(cx, cy + CARD_HEIGHT - 1, cx + CARD_WIDTH, cy + CARD_HEIGHT, border);
                ctx.fill(cx, cy, cx + 1, cy + CARD_HEIGHT, border);
                ctx.fill(cx + CARD_WIDTH - 1, cy, cx + CARD_WIDTH, cy + CARD_HEIGHT, border);

                ctx.drawText(textRenderer, m.getName(), cx + 8, cy + 8,
                    m.isEnabled() ? 0xFFFFFFFF : 0xFF888888, false);

                String keyName = keyName(m.getKey());
                if (keyName != null) {
                    ctx.drawText(textRenderer, keyName, cx + CARD_WIDTH - 8 - textRenderer.getWidth(keyName),
                        cy + 8, 0xFF555555, false);
                }
            }

            col++;
            if (col >= columns) {
                col = 0;
                row++;
            }
        }
    }

    private String keyName(int key) {
        if (key <= 0) return null;
        return switch (key) {
            case 256 -> "ESC";
            case 257 -> "ENTER";
            case 258 -> "TAB";
            case 259 -> "BACK";
            case 260 -> "INS";
            case 261 -> "DEL";
            case 262 -> "RIGHT";
            case 263 -> "LEFT";
            case 264 -> "DOWN";
            case 265 -> "UP";
            case 266 -> "PGUP";
            case 267 -> "PGDN";
            case 268 -> "HOME";
            case 269 -> "END";
            case 340 -> "LSHIFT";
            case 341 -> "LCTRL";
            case 342 -> "LALT";
            case 344 -> "RCTRL";
            case 345 -> "RALT";
            case 346 -> "MENU";
            case 32 -> "SPACE";
            default -> {
                if (key >= 48 && key <= 57) yield String.valueOf((char) key);
                if (key >= 65 && key <= 90) yield String.valueOf((char) key);
                if (key >= 290 && key <= 313) yield "F" + (key - 289);
                yield "KEY_" + key;
            }
        };
    }

    private void drawSettings(DrawContext ctx, int mx, int my) {
        List<Setting<?>> settings = selectedModule.getSettings();
        int visible = 0;
        for (Setting<?> s : settings) {
            if (s.isVisible()) visible++;
        }
        int ph = Math.min(height - 40, visible * 26 + 40);
        int px = (width - SETTINGS_WIDTH) / 2;
        int py = (height - ph) / 2;
        int pex = px + SETTINGS_WIDTH;
        int pey = py + ph;

        ctx.fill(px, py, pex, pey, 0xE0000000);
        ctx.drawText(textRenderer, selectedModule.getName() + " Settings", px + 10, py + 8, 0xFFAA0000, false);

        int sy = py + 30;
        for (Setting<?> s : settings) {
            if (!s.isVisible()) continue;
            int ey = sy + 24;

            ctx.drawText(textRenderer, s.getName(), px + 10, sy + 2, 0xFFCCCCCC, false);

            if (s instanceof BooleanSetting bs) {
                String val = bs.get() ? "ON" : "OFF";
                int col = bs.get() ? 0xFF55FF55 : 0xFFFF5555;
                ctx.drawText(textRenderer, val, pex - 10 - textRenderer.getWidth(val), sy + 2, col, false);
            } else if (s instanceof ModeSetting ms) {
                ctx.drawText(textRenderer, ms.get(), pex - 10 - textRenderer.getWidth(ms.get()), sy + 2, 0xFFFFFFAA, false);
            } else if (s instanceof SliderSetting sl) {
                int trackX = px + 10;
                int trackW = SETTINGS_WIDTH - 20;
                int trackY = sy + 16;
                int trackH = 4;

                double pct = (sl.get() - sl.getMin()) / (sl.getMax() - sl.getMin());
                int fillW = (int) (trackW * pct);

                ctx.fill(trackX, trackY, trackX + trackW, trackY + trackH, 0x60000000);
                ctx.fill(trackX, trackY, trackX + fillW, trackY + trackH, 0xFFAA0000);

                String val = String.format("%.1f", sl.get());
                ctx.drawText(textRenderer, val, pex - 10 - textRenderer.getWidth(val), sy + 2, 0xFF888888, false);
            }

            sy = ey;
        }

        if (selectedModule == null) draggingSlider = null;
    }

    private void drawBindOverlay(DrawContext ctx) {
        ctx.fill(0, 0, width, height, 0xAA000000);
        String line1 = "Press a key to bind " + bindModule.getName();
        String line2 = "ESC to cancel";
        ctx.drawText(textRenderer, line1, (width - textRenderer.getWidth(line1)) / 2, height / 2 - 12, 0xFFFFFFFF, false);
        ctx.drawText(textRenderer, line2, (width - textRenderer.getWidth(line2)) / 2, height / 2 + 6, 0xFF888888, false);
    }

    private void updateSliderFromMouse(SliderSetting slider, double mouseX) {
        List<Setting<?>> settings = selectedModule.getSettings();
        int visible = 0;
        for (Setting<?> s : settings) {
            if (s.isVisible()) visible++;
        }
        int ph = Math.min(height - 40, visible * 26 + 40);
        int px = (width - SETTINGS_WIDTH) / 2;
        int trackX = px + 10;
        int trackW = SETTINGS_WIDTH - 20;

        double rel = (mouseX - trackX) / trackW;
        rel = MathHelper.clamp(rel, 0, 1);
        double value = slider.getMin() + rel * (slider.getMax() - slider.getMin());
        double step = slider.getStep();
        value = Math.round(value / step) * step;
        value = MathHelper.clamp(value, slider.getMin(), slider.getMax());
        slider.set(value);
    }



    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (bindModule != null) {
            return true;
        }

        if (selectedModule != null) {
            List<Setting<?>> settings = selectedModule.getSettings();
            int visible = 0;
            for (Setting<?> s : settings) {
                if (s.isVisible()) visible++;
            }
            int ph = Math.min(height - 40, visible * 26 + 40);
            int px = (width - SETTINGS_WIDTH) / 2;
            int py = (height - ph) / 2;
            int pex = px + SETTINGS_WIDTH;
            int pey = py + ph;

            if (mouseX >= px && mouseX <= pex && mouseY >= py && mouseY <= pey) {
                handleSettingsClick(mouseX, mouseY, button, px, py);
                return true;
            } else {
                selectedModule = null;
                return true;
            }
        }

        handleMainClick(mouseX, mouseY, button);

        if (button == 0) {
            dragging = true;
            dragStartMouseY = (int) mouseY;
            dragStartScrollY = scrollY;
        }

        return true;
    }

    private void handleMainClick(double mx, double my, int button) {
        int contentWidth = columns * (CARD_WIDTH + GAP) - GAP;
        int startX = (width - contentWidth) / 2;
        int chipY = 10 + SEARCH_HEIGHT + 6;

        int cx = startX;
        for (ModuleCategory cat : ModuleCategory.values()) {
            String name = cat.name();
            int cw = textRenderer.getWidth(name) + 20;
            if (mx >= cx && mx <= cx + cw && my >= chipY && my <= chipY + CHIP_HEIGHT) {
                selectedCategory = (selectedCategory == cat) ? null : cat;
                scrollY = 0;
                return;
            }
            cx += cw + GAP;
        }

        List<Module> visible = getVisible();
        int gridY = chipY + CHIP_HEIGHT + GAP;

        int col = 0;
        int row = 0;

        for (Module m : visible) {
            int mcx = startX + col * (CARD_WIDTH + GAP);
            int mcy = (int) (gridY + row * (CARD_HEIGHT + GAP) + scrollY);

            if (mcy + CARD_HEIGHT > gridY && mcy < height) {
                if (mx >= mcx && mx <= mcx + CARD_WIDTH && my >= mcy && my <= mcy + CARD_HEIGHT) {
                    if (button == 0) {
                        m.toggle();
                    } else if (button == 1) {
                        selectedModule = m;
                    } else if (button == 2) {
                        bindModule = m;
                    }
                    return;
                }
            }

            col++;
            if (col >= columns) {
                col = 0;
                row++;
            }
        }
    }

    private void handleSettingsClick(double mx, double my, int button, int px, int py) {
        List<Setting<?>> settings = selectedModule.getSettings();

        int sy = py + 30;
        for (Setting<?> s : settings) {
            if (!s.isVisible()) continue;
            int ey = sy + 24;

            if (my >= sy && my < ey) {
                if (s instanceof BooleanSetting bs) {
                    bs.toggle();
                } else if (s instanceof ModeSetting ms) {
                    if (button == 0) ms.cycle();
                    else ms.cycleBack();
                } else if (s instanceof SliderSetting sl && button == 0) {
                    draggingSlider = sl;
                    updateSliderFromMouse(sl, mx);
                }
                return;
            }

            sy = ey;
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        if (button == 0) {
            draggingSlider = null;
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollY += (float) verticalAmount * 24;
        scrollY = MathHelper.clamp(scrollY, -3000, 0);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (bindModule != null) {
            if (keyCode == 256) {
                bindModule = null;
            } else {
                bindModule.setKey(keyCode);
                bindModule = null;
            }
            return true;
        }

        if (keyCode == 256) {
            close();
            return true;
        }

        if (keyCode == 259 && !search.isEmpty()) {
            search = search.substring(0, search.length() - 1);
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (bindModule != null) return true;
        if (chr >= ' ' && chr <= '~') {
            search += chr;
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
