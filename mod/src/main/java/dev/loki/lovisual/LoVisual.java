package dev.loki.lovisual;

import dev.loki.lovisual.config.ConfigManager;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoVisual implements ModInitializer {
    public static final String MOD_ID = "lovisual";
    public static final String VERSION = "1.0.0";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static LoVisual instance;
    private ConfigManager configManager;

    @Override
    public void onInitialize() {
        instance = this;
        LOGGER.info("LoVisual initialized");
    }

    public static LoVisual getInstance() { return instance; }
    public ConfigManager getConfigManager() { return configManager; }
}
