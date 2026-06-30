package dev.loki.lovisual.module.impl.player;

import dev.loki.lovisual.core.event.EventHandler;
import dev.loki.lovisual.core.event.impl.TickEvent;
import dev.loki.lovisual.module.Module;
import dev.loki.lovisual.module.ModuleCategory;
import dev.loki.lovisual.module.ModuleInfo;
import dev.loki.lovisual.setting.impl.BooleanSetting;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

import java.util.Set;

@ModuleInfo(name = "InvManager", desc = "Auto manage inventory", category = ModuleCategory.PLAYER)
public class InvManager extends Module {
    private final BooleanSetting autoSort = new BooleanSetting("AutoSort", false);
    private final BooleanSetting autoDrop = new BooleanSetting("AutoDrop", false);
    private final BooleanSetting autoTool = new BooleanSetting("AutoTool", false);

    private static final Set<Item> JUNK_ITEMS = Set.of(
            Items.DIRT, Items.COBBLESTONE, Items.GRAVEL, Items.SAND,
            Items.ROTTEN_FLESH, Items.STRING, Items.BONE, Items.SPIDER_EYE,
            Items.POISONOUS_POTATO, Items.POTATO, Items.CARROT, Items.WHEAT,
            Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.MELON_SEEDS,
            Items.PUMPKIN_SEEDS, Items.STONE, Items.ANDESITE, Items.DIORITE,
            Items.GRANITE, Items.OAK_LOG, Items.SPRUCE_LOG, Items.BIRCH_LOG,
            Items.JUNGLE_LOG, Items.ACACIA_LOG, Items.DARK_OAK_LOG
    );

    private int tickCounter;

    @EventHandler
    private void onTick(TickEvent event) {
        if (mc.player == null || mc.interactionManager == null) return;
        if (!(mc.currentScreen instanceof InventoryScreen)) return;

        tickCounter++;

        if (autoDrop.get() && tickCounter % 4 == 0) {
            dropJunk();
        }

        if (autoTool.get() && tickCounter % 10 == 0) {
            switchBestTool();
        }

        if (autoSort.get() && tickCounter % 10 == 0) {
            sortInventory();
        }
    }

    private void dropJunk() {
        for (int i = 0; i < 36; i++) {
            Item item = mc.player.getInventory().getStack(i).getItem();
            if (JUNK_ITEMS.contains(item)) {
                int slot = i < 9 ? i + 36 : i;
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 0, SlotActionType.THROW, mc.player);
            }
        }
    }

    private void switchBestTool() {
        int bestSlot = -1;
        float bestSpeed = 0;

        for (int i = 0; i < 9; i++) {
            var stack = mc.player.getInventory().getStack(i);
            float speed = stack.getMiningSpeedMultiplier(mc.world.getBlockState(mc.player.getBlockPos()));
            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = i;
            }
        }

        if (bestSlot != -1 && bestSlot != mc.player.getInventory().selectedSlot) {
            mc.player.getInventory().selectedSlot = bestSlot;
        }
    }

    private void sortInventory() {
        for (int i = 9; i < 36; i++) {
            var stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty()) continue;

            for (int j = 9; j < 36; j++) {
                if (i == j) continue;
                var other = mc.player.getInventory().getStack(j);
                if (other.isEmpty()) continue;
                if (stack.isOf(other.getItem()) && other.getCount() < other.getMaxCount()) {
                    int transfer = Math.min(stack.getCount(), other.getMaxCount() - other.getCount());
                    stack.decrement(transfer);
                    other.increment(transfer);
                    return;
                }
            }
        }
    }
}
