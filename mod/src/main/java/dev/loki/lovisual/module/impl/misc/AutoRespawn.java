package dev.loki.lovisual.module.impl.misc;

import dev.loki.lovisual.core.event.impl.TickEvent;
import dev.loki.lovisual.module.Module;
import dev.loki.lovisual.module.ModuleCategory;
import dev.loki.lovisual.module.ModuleInfo;
import dev.loki.lovisual.core.event.EventHandler;

@ModuleInfo(name = "AutoRespawn", desc = "Automatically respawns when dead", category = ModuleCategory.MISC, key = -1)
public class AutoRespawn extends Module {
    @EventHandler
    private void onTick(TickEvent event) {
        if (mc.player == null) return;
        if (!mc.player.isDead()) return;
        mc.player.requestRespawn();
    }
}
