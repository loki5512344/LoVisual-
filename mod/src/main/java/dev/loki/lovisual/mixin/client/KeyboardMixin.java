package dev.loki.lovisual.mixin.client;

import dev.loki.lovisual.core.event.EventBus;
import dev.loki.lovisual.core.event.impl.KeyPressEvent;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void onKey(long window, int key, int action, int modifiers, CallbackInfo ci) {
        EventBus.post(new KeyPressEvent(key, action));
    }
}
