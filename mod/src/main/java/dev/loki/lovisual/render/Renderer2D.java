package dev.loki.lovisual.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public class Renderer2D {
    private final DrawContext ctx;
    private final MinecraftClient mc;

    public Renderer2D(DrawContext ctx) {
        this.ctx = ctx;
        this.mc = MinecraftClient.getInstance();
    }

    public DrawContext getContext() {
        return ctx;
    }

    public void fill(float x, float y, float w, float h, int color) {
        ctx.fill((int) x, (int) y, (int) (x + w), (int) (y + h), color);
    }

    public void fillGradient(float x, float y, float w, float h, int color1, int color2, boolean vertical) {
        if (vertical) {
            ctx.fillGradient((int) x, (int) y, (int) (x + w), (int) (y + h), color1, color2);
        } else {
            ctx.fillGradient((int) x, (int) y, (int) (x + w), (int) (y + h), color1, color2);
        }
    }

    public void drawRoundedRect(float x, float y, float w, float h, float r, int color) {
        int ix = (int) x, iy = (int) y;
        int iw = (int) (x + w), ih = (int) (y + h);
        int ir = (int) r;
        if (ir <= 0) { ctx.fill(ix, iy, iw, ih, color); return; }

        ctx.fill(ix + ir, iy, iw - ir, ih, color);
        ctx.fill(ix, iy + ir, ix + ir, ih - ir, color);
        ctx.fill(iw - ir, iy + ir, iw, ih - ir, color);

        for (int row = 0; row < ir; row++) {
            int dx = (int) Math.sqrt(ir * ir - (ir - row) * (ir - row));
            if (dx <= 0) continue;
            ctx.fill(ix + ir - dx, iy + row, ix + ir, iy + row + 1, color);
            ctx.fill(iw - ir, iy + row, iw - ir + dx, iy + row + 1, color);
            ctx.fill(ix + ir - dx, ih - row - 1, ix + ir, ih - row, color);
            ctx.fill(iw - ir, ih - row - 1, iw - ir + dx, ih - row, color);
        }
    }

    public void drawRoundedBorder(float x, float y, float w, float h, float r, float thickness, int color) {
        int ix = (int) x, iy = (int) y;
        int iw = (int) (x + w), ih = (int) (y + h);
        int ir = (int) r, t = Math.max(1, (int) thickness);
        if (ir <= 0) { drawBorder(x, y, w, h, thickness, color); return; }

        ctx.fill(ix + ir, iy, iw - ir, iy + t, color);
        ctx.fill(ix + ir, ih - t, iw - ir, ih, color);
        ctx.fill(ix, iy + ir, ix + t, ih - ir, color);
        ctx.fill(iw - t, iy + ir, iw, ih - ir, color);

        int r2 = ir * ir;
        int innerR2 = (ir - t) * (ir - t);

        for (int py = 0; py < ir; py++) {
            for (int px = 0; px < ir; px++) {
                int dx = px - ir, dy = py - ir;
                int d2 = dx * dx + dy * dy;
                if (d2 >= innerR2 && d2 <= r2) {
                    ctx.fill(ix + px, iy + py, ix + px + 1, iy + py + 1, color);
                    ctx.fill(iw - px - 1, iy + py, iw - px, iy + py + 1, color);
                    ctx.fill(ix + px, ih - py - 1, ix + px + 1, ih - py, color);
                    ctx.fill(iw - px - 1, ih - py - 1, iw - px, ih - py, color);
                }
            }
        }
    }

    public void drawBorder(float x, float y, float w, float h, float thickness, int color) {
        int t = Math.max(1, (int) thickness);
        ctx.fill((int) x, (int) y, (int) (x + w), (int) y + t, color);
        ctx.fill((int) x, (int) (y + h - t), (int) (x + w), (int) (y + h), color);
        ctx.fill((int) x, (int) y, (int) x + t, (int) (y + h), color);
        ctx.fill((int) (x + w - t), (int) y, (int) (x + w), (int) (y + h), color);
    }

    public void drawText(String text, float x, float y, int color) {
        ctx.drawText(mc.textRenderer, text, (int) x, (int) y, color, false);
    }

    public void drawText(Text text, float x, float y, int color) {
        ctx.drawText(mc.textRenderer, text, (int) x, (int) y, color, false);
    }

    public void drawText(OrderedText text, float x, float y, int color) {
        ctx.drawText(mc.textRenderer, text, (int) x, (int) y, color, false);
    }

    public void drawTextWithShadow(String text, float x, float y, int color) {
        ctx.drawTextWithShadow(mc.textRenderer, text, (int) x, (int) y, color);
    }

    public void drawCenteredText(String text, float x, float y, int color) {
        int tw = mc.textRenderer.getWidth(text);
        ctx.drawText(mc.textRenderer, text, (int) (x - tw / 2f), (int) y, color, false);
    }

    public void drawCenteredTextWithShadow(String text, float x, float y, int color) {
        ctx.drawCenteredTextWithShadow(mc.textRenderer, text, (int) x, (int) y, color);
    }

    public TextRenderer getTextRenderer() {
        return mc.textRenderer;
    }

    public int getTextWidth(String text) {
        return mc.textRenderer.getWidth(text);
    }
}
