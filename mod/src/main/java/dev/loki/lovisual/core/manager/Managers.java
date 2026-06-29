package dev.loki.lovisual.core.manager;

import dev.loki.lovisual.command.CommandManager;
import dev.loki.lovisual.config.ConfigManager;
import dev.loki.lovisual.module.ModuleManager;

public class Managers {
    public static final ModuleManager MODULE = new ModuleManager();
    public static final ConfigManager CONFIG = new ConfigManager();
    public static final CommandManager COMMAND = new CommandManager();

    public static void init() {
        // init is now called manually in LoVisualClient
    }
}
