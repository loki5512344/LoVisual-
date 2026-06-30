package dev.loki.lovisual.mixin.render;

import dev.loki.lovisual.gui.mainmenu.LoVisualMainMenu;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class MainMenuMixin {
    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private void onInit(CallbackInfo ci) {
        MinecraftClient.getInstance().setScreen(new LoVisualMainMenu());
        ci.cancel();
    }
}
