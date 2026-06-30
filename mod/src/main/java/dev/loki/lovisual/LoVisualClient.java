package dev.loki.lovisual;

import dev.loki.lovisual.core.Updater;
import dev.loki.lovisual.core.manager.Managers;
import dev.loki.lovisual.gui.hud.HudManager;
import dev.loki.lovisual.gui.hud.impl.*;
import dev.loki.lovisual.module.ModuleManager;
import dev.loki.lovisual.module.impl.combat.*;
import dev.loki.lovisual.module.impl.misc.*;
import dev.loki.lovisual.module.impl.movement.Sprint;
import dev.loki.lovisual.module.impl.player.*;
import dev.loki.lovisual.module.impl.render.*;
import dev.loki.lovisual.gui.clickgui.ClickGuiScreen;
import dev.loki.lovisual.gui.mainmenu.MainMenuHandler;
import dev.loki.lovisual.render.FontRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class LoVisualClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LoVisual.LOGGER.info("Initializing LoVisual client...");

        ModuleManager mm = Managers.MODULE;
        mm.configManager = Managers.CONFIG;

        registerModules(mm);
        registerHud();

        mm.init();
        Managers.CONFIG.init();
        Managers.COMMAND.init();
        HudManager.init();
        FontRegistry.init();
        Managers.CONFIG.load();
        MainMenuHandler.init();
        Updater.check();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
            dispatcher.register(literal("lovisual")
                .executes(ctx -> {
                    MinecraftClient.getInstance().setScreen(new ClickGuiScreen());
                    return 1;
                })
            )
        );
    }

    private void registerModules(ModuleManager mm) {
        mm.register(new Sprint());
        mm.register(new AutoClicker());
        mm.register(new Trajectories());
        mm.register(new ClickGUI());
        mm.register(new FullBright());
        mm.register(new NoHurtCam());
        mm.register(new NoWeather());
        mm.register(new NoBobbing());
        mm.register(new Crosshair());
        // TODO: implement Nametags, HealthTags, PVision — currently empty stubs
        // mm.register(new PVision());
        // mm.register(new Nametags());
        // mm.register(new HealthTags());
        mm.register(new AutoTool());
        mm.register(new InvManager());
        mm.register(new ExpThrower());
        mm.register(new MiddleClickFriend());
        mm.register(new Refill());
        mm.register(new NoInteract());
        mm.register(new AntiAFK());
        mm.register(new AutoRespawn());
        mm.register(new NameProtect());
        mm.register(new IRC());
    }

    private void registerHud() {
        HudManager.add(new Watermark());
        HudManager.add(new ModuleList());
        HudManager.add(new TargetHUD());
        HudManager.add(new ArmorHUD());
        HudManager.add(new PotionsHUD());
        HudManager.add(new Keystrokes());
        HudManager.add(new Scoreboard());
        HudManager.add(new Coords());
        HudManager.add(new ComboCounter());
        HudManager.add(new Notifications());
    }
}
