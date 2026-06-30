package dev.loki.lovisual.mixin.render;

import dev.loki.lovisual.module.impl.render.Animations;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    @Inject(method = "renderItem", at = @At("HEAD"))
    private void onRenderItem(LivingEntity entity, ItemStack stack, ItemDisplayContext displayContext, MatrixStack matrices, OrderedRenderCommandQueue commandQueue, int light, CallbackInfo ci) {
        if (Animations.INSTANCE != null && Animations.INSTANCE.isEnabled()) {
            double scale = Animations.INSTANCE.getItemScale();
            if (Math.abs(scale - 1.0) > 0.01) {
                matrices.push();
                matrices.scale((float) scale, (float) scale, (float) scale);
            }
        }
    }

    @Inject(method = "renderItem", at = @At("RETURN"))
    private void onRenderItemPost(LivingEntity entity, ItemStack stack, ItemDisplayContext displayContext, MatrixStack matrices, OrderedRenderCommandQueue commandQueue, int light, CallbackInfo ci) {
        if (Animations.INSTANCE != null && Animations.INSTANCE.isEnabled()) {
            double scale = Animations.INSTANCE.getItemScale();
            if (Math.abs(scale - 1.0) > 0.01) {
                matrices.pop();
            }
        }
    }
}
