package dev.loki.lovisual.module.impl.combat;

import dev.loki.lovisual.core.event.EventHandler;
import dev.loki.lovisual.core.event.impl.Render2DEvent;
import dev.loki.lovisual.core.event.impl.Render3DEvent;
import dev.loki.lovisual.core.event.impl.TickEvent;
import dev.loki.lovisual.module.Module;
import dev.loki.lovisual.module.ModuleCategory;
import dev.loki.lovisual.module.ModuleInfo;
import dev.loki.lovisual.setting.impl.BooleanSetting;
import dev.loki.lovisual.setting.impl.ColorSetting;
import dev.loki.lovisual.setting.impl.ModeSetting;
import dev.loki.lovisual.setting.impl.SliderSetting;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "Trajectories", desc = "Show projectile trajectory & timer", category = ModuleCategory.COMBAT)
public class Trajectories extends Module {
    private final ColorSetting color = new ColorSetting("Color", new Color(255, 80, 80));
    private final SliderSetting opacity = new SliderSetting("Opacity", 0.8, 0.1, 1.0, 0.05);
    private final ModeSetting mode = new ModeSetting("Mode", "Pearl", "Pearl", "All");
    private final BooleanSetting showTimer = new BooleanSetting("Show Timer", true);
    private final BooleanSetting showLanding = new BooleanSetting("Show Landing", true);

    private EnderPearlEntity tracked;
    private final List<Vec3d> path = new ArrayList<>();
    private int ticksUntilLand = -1;

    @EventHandler
    private void onTick(TickEvent event) {
        if (mc.world == null || mc.player == null) {
            tracked = null; path.clear(); ticksUntilLand = -1;
            return;
        }

        List<EnderPearlEntity> pearls = mc.world.getEntitiesByClass(
            EnderPearlEntity.class,
            new Box(mc.player.getBlockPos()).expand(128),
            p -> p.getOwner() == mc.player
        );

        if (pearls.isEmpty()) {
            tracked = null; path.clear(); ticksUntilLand = -1;
            return;
        }

        EnderPearlEntity pearl = pearls.getFirst();
        if (pearl != tracked) {
            tracked = pearl;
            path.clear();
        }
        if (tracked == null) return;

        Vec3d vel = tracked.getVelocity();
        double vx = vel.x, vy = vel.y, vz = vel.z;
        double px = tracked.getX(), py = tracked.getY(), pz = tracked.getZ();

        path.clear();
        path.add(new Vec3d(px, py, pz));

        int ticks = 0;
        boolean willLand = false;
        for (int i = 0; i < 400; i++) {
            px += vx; py += vy; pz += vz;
            vx *= 0.99; vz *= 0.99; vy *= 0.99;
            vy -= 0.03;
            ticks++;

            if (py < mc.world.getBottomY()) {
                willLand = true;
                break;
            }
            if (i % 3 == 0) {
                path.add(new Vec3d(px, py, pz));
            }
        }

        ticksUntilLand = willLand ? ticks : -1;
    }

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (path.size() < 2) return;

        Color c = color.get();
        float r = c.getRed() / 255f;
        float g = c.getGreen() / 255f;
        float b = c.getBlue() / 255f;
        float a = (float) (double) opacity.get();

        MatrixStack matrices = event.getMatrices();
        matrices.push();
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINE_STRIP, VertexFormats.LINES);
        Vec3d cam = event.getCamera().getPos();

        for (Vec3d p : path) {
            float x = (float) (p.x - cam.x);
            float y = (float) (p.y - cam.y);
            float z = (float) (p.z - cam.z);
            buffer.vertex(matrix, x, y, z).color(r, g, b, a).normal(0f, 1f, 0f);
            buffer.vertex(matrix, x, y, z).color(r, g, b, a).normal(0f, 1f, 0f);
        }
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        if (showLanding.get() && !path.isEmpty()) {
            Vec3d last = path.getLast();
            float lx = (float) (last.x - cam.x);
            float ly = (float) (last.y - cam.y);
            float lz = (float) (last.z - cam.z);
            BufferBuilder box = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
            float hs = 0.3f;
            box.vertex(matrix, lx - hs, ly, lz - hs).color(r, g, b, a).normal(0f, 1f, 0f);
            box.vertex(matrix, lx + hs, ly, lz - hs).color(r, g, b, a).normal(0f, 1f, 0f);
            box.vertex(matrix, lx + hs, ly, lz - hs).color(r, g, b, a).normal(0f, 1f, 0f);
            box.vertex(matrix, lx + hs, ly, lz + hs).color(r, g, b, a).normal(0f, 1f, 0f);
            box.vertex(matrix, lx + hs, ly, lz + hs).color(r, g, b, a).normal(0f, 1f, 0f);
            box.vertex(matrix, lx - hs, ly, lz + hs).color(r, g, b, a).normal(0f, 1f, 0f);
            box.vertex(matrix, lx - hs, ly, lz + hs).color(r, g, b, a).normal(0f, 1f, 0f);
            box.vertex(matrix, lx - hs, ly, lz - hs).color(r, g, b, a).normal(0f, 1f, 0f);
            box.vertex(matrix, lx - hs, ly + 0.01f, lz - hs).color(r, g, b, a).normal(0f, 1f, 0f);
            box.vertex(matrix, lx + hs, ly + 0.01f, lz - hs).color(r, g, b, a).normal(0f, 1f, 0f);
            box.vertex(matrix, lx + hs, ly + 0.01f, lz - hs).color(r, g, b, a).normal(0f, 1f, 0f);
            box.vertex(matrix, lx + hs, ly + 0.01f, lz + hs).color(r, g, b, a).normal(0f, 1f, 0f);
            box.vertex(matrix, lx + hs, ly + 0.01f, lz + hs).color(r, g, b, a).normal(0f, 1f, 0f);
            box.vertex(matrix, lx - hs, ly + 0.01f, lz + hs).color(r, g, b, a).normal(0f, 1f, 0f);
            box.vertex(matrix, lx - hs, ly + 0.01f, lz + hs).color(r, g, b, a).normal(0f, 1f, 0f);
            box.vertex(matrix, lx - hs, ly + 0.01f, lz - hs).color(r, g, b, a).normal(0f, 1f, 0f);
            BufferRenderer.drawWithGlobalProgram(box.end());
        }

        matrices.pop();
    }

    @EventHandler
    private void onRender2D(Render2DEvent event) {
        if (tracked == null || ticksUntilLand <= 0 || !showTimer.get()) return;

        Vec3d pos = tracked.getPos();
        Vec3d screen = projectToScreen(event.getContext(), pos);

        if (screen == null || screen.z < 0) return;

        String timer = String.format("%.1fs", ticksUntilLand / 20f);
        int x = (int) screen.x;
        int y = (int) screen.y;

        event.getContext().getMatrices().push();
        event.getContext().getMatrices().translate(0, 0, 100);
        int tw = mc.textRenderer.getWidth(timer);
        event.getContext().fill(x - tw / 2 - 3, y - 12, x + tw / 2 + 3, y + 2, 0x80000000);
        event.getContext().drawText(mc.textRenderer, timer, x - tw / 2, y - 10, 0xFFFF4444, true);
        event.getContext().getMatrices().pop();
    }

    private Vec3d projectToScreen(net.minecraft.client.gui.DrawContext ctx, Vec3d pos) {
        if (mc.getCameraEntity() == null) return null;
        Vec3d cam = mc.getCameraEntity().getPos();
        double relX = pos.x - cam.x;
        double relY = pos.y - cam.y;
        double relZ = pos.z - cam.z;

        float fov = mc.options.getFov().getValue();
        int w = mc.getWindow().getScaledWidth();
        int h = mc.getWindow().getScaledHeight();

        float yaw = mc.getCameraEntity().getYaw();
        float pitch = mc.getCameraEntity().getPitch();
        float roll = 0;

        org.joml.Matrix4f proj = new org.joml.Matrix4f().perspective(
            (float) Math.toRadians(fov), (float) w / h, 0.05f, 256
        );
        org.joml.Matrix4f view = new org.joml.Matrix4f()
            .rotateY((float) Math.toRadians(-yaw))
            .rotateX((float) Math.toRadians(pitch))
            .rotateZ((float) Math.toRadians(roll));

        org.joml.Vector4f v = new org.joml.Vector4f((float) relX, (float) relY, (float) (-relZ), 1f)
            .mul(view).mul(proj);

        if (v.w <= 0) return null;
        float nx = (v.x / v.w + 1) * 0.5f * w;
        float ny = (1 - v.y / v.w) * 0.5f * h;
        return new Vec3d(nx, ny, v.w);
    }
}
