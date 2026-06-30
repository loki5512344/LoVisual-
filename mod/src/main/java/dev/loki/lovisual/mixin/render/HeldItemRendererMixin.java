package dev.loki.lovisual.mixin.render;

import dev.loki.lovisual.module.impl.render.Animations;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    @Inject(method = "renderItem", at = @At("HEAD"))
    private void onRenderItem(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate providers, ClientPlayerEntity player, int light, CallbackInfo ci) {
        if (Animations.INSTANCE != null && Animations.INSTANCE.isEnabled()) {
            double scale = Animations.INSTANCE.getItemScale();
            if (Math.abs(scale - 1.0) > 0.01) {
                matrices.push();
                matrices.scale((float) scale, (float) scale, (float) scale);
            }
        }
    }

    @Inject(method = "renderItem", at = @At("RETURN"))
    private void onRenderItemPost(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate providers, ClientPlayerEntity player, int light, CallbackInfo ci) {
        if (Animations.INSTANCE != null && Animations.INSTANCE.isEnabled()) {
            double scale = Animations.INSTANCE.getItemScale();
            if (Math.abs(scale - 1.0) > 0.01) {
                matrices.pop();
            }
        }
    }
}
