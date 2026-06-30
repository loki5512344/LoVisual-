package dev.loki.lovisual.mixin.client;

import dev.loki.lovisual.core.event.EventBus;
import dev.loki.lovisual.core.event.impl.KeyPressEvent;
import net.minecraft.client.Keyboard;
import net.minecraft.client.input.KeyInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(method = "onKey", at = @At("HEAD"))
    private void onKey(long window, int action, KeyInput keyInput, CallbackInfo ci) {
        EventBus.post(new KeyPressEvent(keyInput.key(), action));
    }
}
