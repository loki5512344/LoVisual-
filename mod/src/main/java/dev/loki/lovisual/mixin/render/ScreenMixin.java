package dev.loki.lovisual.mixin.render;

import dev.loki.lovisual.render.BackgroundRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {
    @Inject(method = "renderBackground", at = @At("HEAD"))
    private void onRenderBackground(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world != null) return;

        BackgroundRenderer.init(mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight());
        BackgroundRenderer.render(ctx, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight(), delta);
    }
}
