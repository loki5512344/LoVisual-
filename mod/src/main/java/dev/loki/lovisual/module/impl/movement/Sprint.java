package dev.loki.lovisual.module.impl.movement;

import dev.loki.lovisual.core.event.impl.TickEvent;
import dev.loki.lovisual.module.Module;
import dev.loki.lovisual.module.ModuleCategory;
import dev.loki.lovisual.module.ModuleInfo;
import dev.loki.lovisual.setting.impl.BooleanSetting;
import dev.loki.lovisual.core.event.EventHandler;

@ModuleInfo(name = "Sprint", desc = "Automatically sprints", category = ModuleCategory.MOVEMENT, key = -1)
public class Sprint extends Module {
    private final BooleanSetting keepSprint = new BooleanSetting("KeepSprint", true);

    @EventHandler
    private void onTick(TickEvent event) {
        if (mc.player == null) return;
        if (mc.player.input.playerInput.forward() && !mc.player.isSneaking()) {
            mc.player.setSprinting(true);
        }
    }
}
