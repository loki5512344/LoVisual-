package dev.loki.lovisual.module.impl.player;

import dev.loki.lovisual.core.event.impl.TickEvent;
import dev.loki.lovisual.module.Module;
import dev.loki.lovisual.module.ModuleCategory;
import dev.loki.lovisual.module.ModuleInfo;
import dev.loki.lovisual.setting.impl.BooleanSetting;
import dev.loki.lovisual.core.event.EventHandler;
import net.minecraft.block.BlockState;
import net.minecraft.item.SwordItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

@ModuleInfo(name = "AutoTool", desc = "Automatically switches to the best tool", category = ModuleCategory.PLAYER, key = -1)
public class AutoTool extends Module {
    private final BooleanSetting sword = new BooleanSetting("Sword", true);

    @EventHandler
    private void onTick(TickEvent event) {
        if (mc.player == null || mc.interactionManager == null) return;
        if (mc.player.isSpectator()) return;
        if (sword.get() && mc.player.getMainHandStack().getItem() instanceof SwordItem) return;

        HitResult hit = mc.crosshairTarget;
        if (!(hit instanceof BlockHitResult blockHit)) return;

        BlockState state = mc.world.getBlockState(blockHit.getBlockPos());
        int bestSlot = -1;
        float bestSpeed = 0;

        for (int i = 0; i < 9; i++) {
            var stack = mc.player.getInventory().getStack(i);
            float speed = stack.getMiningSpeedMultiplier(state);
            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = i;
            }
        }

        if (bestSlot != -1 && bestSlot != mc.player.getInventory().selectedSlot) {
            mc.player.getInventory().selectedSlot = bestSlot;
        }
    }
}
