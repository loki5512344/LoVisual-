package dev.loki.lovisual.module.impl.misc;

import dev.loki.lovisual.core.event.EventHandler;
import dev.loki.lovisual.core.event.impl.TickEvent;
import dev.loki.lovisual.module.Module;
import dev.loki.lovisual.module.ModuleCategory;
import dev.loki.lovisual.module.ModuleInfo;

@ModuleInfo(name = "AntiAFK", desc = "Prevents AFK kick", category = ModuleCategory.MISC)
public class AntiAFK extends Module {
    private int tick;

    @EventHandler
    private void onTick(TickEvent event) {
        if (mc.player == null) return;
        tick++;
        if (tick < 80) return;
        tick = 0;
        if (mc.player.input != null) {
            mc.player.input.movementForward = 1;
        }
    }
}
