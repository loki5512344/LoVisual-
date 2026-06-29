package dev.loki.lovisual.module.impl.player;

import dev.loki.lovisual.core.event.impl.PacketEvent;
import dev.loki.lovisual.module.Module;
import dev.loki.lovisual.module.ModuleCategory;
import dev.loki.lovisual.module.ModuleInfo;
import dev.loki.lovisual.setting.impl.ModeSetting;
import dev.loki.lovisual.core.event.EventHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;

@ModuleInfo(name = "NoInteract", desc = "Prevents interaction with entities or items", category = ModuleCategory.PLAYER, key = -1)
public class NoInteract extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "All", "Entities", "Items", "All");

    @EventHandler
    private void onPacket(PacketEvent event) {
        if (event.getState() != PacketEvent.PacketState.SEND) return;

        Packet<?> packet = event.getPacket();
        boolean cancel = switch (mode.get()) {
            case "Entities" -> packet instanceof PlayerInteractEntityC2SPacket;
            case "Items" -> packet instanceof PlayerInteractItemC2SPacket;
            case "All" -> packet instanceof PlayerInteractEntityC2SPacket || packet instanceof PlayerInteractItemC2SPacket;
            default -> false;
        };

        if (cancel) event.cancel();
    }
}
