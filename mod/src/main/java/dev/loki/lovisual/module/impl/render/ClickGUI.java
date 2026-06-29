package dev.loki.lovisual.module.impl.render;

import dev.loki.lovisual.gui.clickgui.ClickGuiScreen;
import dev.loki.lovisual.module.Module;
import dev.loki.lovisual.module.ModuleCategory;
import dev.loki.lovisual.module.ModuleInfo;

@ModuleInfo(name = "ClickGUI", desc = "Opens the click GUI", category = ModuleCategory.RENDER, key = 344)
public class ClickGUI extends Module {

    @Override
    protected void onEnable() {
        if (mc != null) {
            mc.setScreen(new ClickGuiScreen());
        }
        disable();
    }
}
