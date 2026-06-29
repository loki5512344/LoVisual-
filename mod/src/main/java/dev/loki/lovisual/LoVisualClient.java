package dev.loki.lovisual;

import dev.loki.lovisual.core.manager.Managers;
import dev.loki.lovisual.gui.hud.HudManager;
import dev.loki.lovisual.gui.hud.impl.*;
import dev.loki.lovisual.module.ModuleManager;
import dev.loki.lovisual.module.impl.misc.*;
import dev.loki.lovisual.module.impl.movement.Sprint;
import dev.loki.lovisual.module.impl.player.*;
import dev.loki.lovisual.module.impl.render.*;
import net.fabricmc.api.ClientModInitializer;

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
        Managers.CONFIG.load();
    }

    private void registerModules(ModuleManager mm) {
        mm.register(new Sprint());
        mm.register(new ClickGUI());
        mm.register(new FullBright());
        mm.register(new NoHurtCam());
        mm.register(new NoWeather());
        mm.register(new NoBobbing());
        mm.register(new Crosshair());
        mm.register(new PVision());
        mm.register(new Nametags());
        mm.register(new HealthTags());
        mm.register(new AutoTool());
        mm.register(new ExpThrower());
        mm.register(new MiddleClickFriend());
        mm.register(new Refill());
        mm.register(new NoInteract());
        mm.register(new AntiAFK());
        mm.register(new AutoRespawn());
        mm.register(new NameProtect());
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
