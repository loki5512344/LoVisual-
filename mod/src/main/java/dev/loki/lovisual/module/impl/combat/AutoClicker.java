package dev.loki.lovisual.module.impl.combat;

import dev.loki.lovisual.core.event.EventHandler;
import dev.loki.lovisual.core.event.impl.TickEvent;
import dev.loki.lovisual.module.Module;
import dev.loki.lovisual.module.ModuleCategory;
import dev.loki.lovisual.module.ModuleInfo;
import dev.loki.lovisual.setting.impl.BooleanSetting;
import dev.loki.lovisual.setting.impl.SliderSetting;

import net.minecraft.util.Hand;

@ModuleInfo(name = "AutoClicker", desc = "Automatic clicking", category = ModuleCategory.COMBAT)
public class AutoClicker extends Module {
    private final SliderSetting cps = new SliderSetting("CPS", 8, 1, 20, 1);
    private final BooleanSetting leftClick = new BooleanSetting("LeftClick", true);
    private final BooleanSetting rightClick = new BooleanSetting("RightClick", false);
    private final BooleanSetting onlyWeapon = new BooleanSetting("Only Weapon", false);

    private long lastLeftClick;
    private long lastRightClick;

    @EventHandler
    private void onTick(TickEvent event) {
        if (mc.player == null || mc.interactionManager == null) return;

        long now = System.currentTimeMillis();
        long interval = (long) (1000L / cps.get());

        if (leftClick.get() && mc.options.attackKey.isPressed()) {
            if (now - lastLeftClick >= interval) {
                mc.interactionManager.attackEntity(mc.player, mc.targetedEntity);
                mc.player.swingHand(Hand.MAIN_HAND);
                lastLeftClick = now;
            }
        }

        if (rightClick.get() && mc.options.useKey.isPressed()) {
            if (now - lastRightClick >= interval) {
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                mc.player.swingHand(Hand.MAIN_HAND);
                lastRightClick = now;
            }
        }
    }
}
