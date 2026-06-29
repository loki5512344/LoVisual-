package dev.loki.lovisual.module.impl.render;

import dev.loki.lovisual.module.Module;
import dev.loki.lovisual.module.ModuleCategory;
import dev.loki.lovisual.module.ModuleInfo;

@ModuleInfo(name = "FullBright", desc = "Sets gamma to maximum", category = ModuleCategory.RENDER)
public class FullBright extends Module {
    private double previousGamma;

    @Override
    protected void onEnable() {
        previousGamma = mc.options.getGamma().getValue();
        mc.options.getGamma().setValue(100.0);
    }

    @Override
    protected void onDisable() {
        mc.options.getGamma().setValue(previousGamma);
    }
}
