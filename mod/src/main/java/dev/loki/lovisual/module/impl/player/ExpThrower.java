package dev.loki.lovisual.module.impl.player;

import dev.loki.lovisual.core.event.impl.KeyPressEvent;
import dev.loki.lovisual.core.event.impl.TickEvent;
import dev.loki.lovisual.module.Module;
import dev.loki.lovisual.module.ModuleCategory;
import dev.loki.lovisual.module.ModuleInfo;
import dev.loki.lovisual.setting.impl.BindSetting;
import dev.loki.lovisual.setting.impl.ModeSetting;
import dev.loki.lovisual.core.event.EventHandler;

@ModuleInfo(name = "ExpThrower", desc = "Automatically throws XP bottles", category = ModuleCategory.PLAYER, key = -1)
public class ExpThrower extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Key", "Key", "Auto");
    private final BindSetting bindKey = new BindSetting("Bind", -1);

    @EventHandler
    private void onTick(TickEvent event) {
        if (mc.player == null || !mode.get().equals("Auto")) return;
        if (mc.player.getHealth() < 10f) {
            mc.interactionManager.interactItem(mc.player, mc.player.getActiveHand());
        }
    }

    @EventHandler
    private void onKeyPress(KeyPressEvent event) {
        if (mc.player == null || !mode.get().equals("Key")) return;
        if (!event.isPressed()) return;
        if (event.getKey() != bindKey.get().intValue()) return;
        mc.interactionManager.interactItem(mc.player, mc.player.getActiveHand());
    }
}
