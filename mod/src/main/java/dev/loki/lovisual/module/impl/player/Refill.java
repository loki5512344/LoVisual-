package dev.loki.lovisual.module.impl.player;

import dev.loki.lovisual.module.Module;
import dev.loki.lovisual.module.ModuleCategory;
import dev.loki.lovisual.module.ModuleInfo;
import dev.loki.lovisual.setting.impl.SliderSetting;
import net.minecraft.item.ItemStack;

@ModuleInfo(name = "Refill", desc = "Refills hotbar slots from inventory", category = ModuleCategory.PLAYER, key = -1)
public class Refill extends Module {
    private final SliderSetting fillLevel = new SliderSetting("FillLevel", 64, 1, 64, 1);

    @Override
    protected void onEnable() {
        refill();
    }

    private void refill() {
        if (mc.player == null) return;

        for (int i = 0; i < 9; i++) {
            ItemStack hotbarStack = mc.player.getInventory().getStack(i);
            if (hotbarStack.isEmpty()) continue;
            if (hotbarStack.getCount() >= fillLevel.get().intValue()) continue;

            for (int j = 9; j < 36; j++) {
                ItemStack invStack = mc.player.getInventory().getStack(j);
                if (invStack.isEmpty()) continue;
                if (!ItemStack.areItemsEqual(hotbarStack, invStack)) continue;
                if (!ItemStack.areEqual(hotbarStack, invStack)) continue;

                int needed = fillLevel.get().intValue() - hotbarStack.getCount();
                int taken = Math.min(needed, invStack.getCount());
                invStack.decrement(taken);
                hotbarStack.increment(taken);
                if (hotbarStack.getCount() >= fillLevel.get().intValue()) break;
            }
        }
    }
}
