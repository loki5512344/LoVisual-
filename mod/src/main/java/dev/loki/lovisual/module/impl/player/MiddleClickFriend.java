package dev.loki.lovisual.module.impl.player;

import dev.loki.lovisual.core.event.impl.MouseClickEvent;
import dev.loki.lovisual.module.Module;
import dev.loki.lovisual.module.ModuleCategory;
import dev.loki.lovisual.module.ModuleInfo;
import dev.loki.lovisual.core.event.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;

import java.util.HashSet;
import java.util.Set;

@ModuleInfo(name = "MiddleClickFriend", desc = "Middle click on players to add or remove friends", category = ModuleCategory.PLAYER, key = -1)
public class MiddleClickFriend extends Module {
    private final Set<String> friends = new HashSet<>();

    @EventHandler
    private void onMouseClick(MouseClickEvent event) {
        if (event.getButton() != 2 || event.getAction() != 1) return;
        if (mc.player == null || mc.crosshairTarget == null) return;
        if (!(mc.crosshairTarget instanceof EntityHitResult hit)) return;

        Entity target = hit.getEntity();
        if (!(target instanceof PlayerEntity)) return;

        String name = target.getName().getString();
        if (friends.contains(name)) {
            friends.remove(name);
            log.info("Unfriended {}", name);
        } else {
            friends.add(name);
            log.info("Friended {}", name);
        }
    }
}
