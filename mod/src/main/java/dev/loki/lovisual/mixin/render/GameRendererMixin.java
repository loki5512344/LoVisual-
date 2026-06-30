package dev.loki.lovisual.mixin.render;

import dev.loki.lovisual.core.event.EventBus;
import dev.loki.lovisual.core.event.impl.Render3DEvent;
import dev.loki.lovisual.module.ModuleManager;
import dev.loki.lovisual.module.impl.render.Zoom;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "renderWorld", at = @At("HEAD"))
    private void onRenderWorld(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci) {
        EventBus.post(new Render3DEvent(matrices, null, tickDelta));
    }

    @ModifyVariable(method = "getFov", at = @At("RETURN"), ordinal = 0)
    private double modifyFov(double original) {
        Zoom zoom = ModuleManager.INSTANCE != null ? ModuleManager.INSTANCE.get(Zoom.class) : null;
        if (zoom != null && zoom.isEnabled()) {
            return original * zoom.getCurrentFov();
        }
        return original;
    }
}
