package dev.loki.lovisual.mixin.entity;

import dev.loki.lovisual.module.impl.render.Animations;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
    }

    @Inject(method = "getHandSwingProgress", at = @At("RETURN"), cancellable = true)
    private void onGetHandSwingProgress(float tickDelta, CallbackInfoReturnable<Float> cir) {
        if (Animations.INSTANCE != null && Animations.INSTANCE.isEnabled()) {
            String mode = Animations.INSTANCE.getSwingMode();
            float speed = (float) Animations.INSTANCE.getSwingSpeed();

            if (mode.equals("None")) {
                cir.setReturnValue(0f);
            } else if (mode.equals("1.7") || mode.equals("Normal")) {
                float original = cir.getReturnValue();
                cir.setReturnValue(original * speed);
            }
        }
    }
}
