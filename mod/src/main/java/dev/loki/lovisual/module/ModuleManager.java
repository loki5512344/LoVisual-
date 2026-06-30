package dev.loki.lovisual.module;

import dev.loki.lovisual.config.ConfigManager;
import dev.loki.lovisual.core.event.EventBus;
import dev.loki.lovisual.core.event.impl.KeyPressEvent;
import dev.loki.lovisual.core.event.EventHandler;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleManager {
    public static ModuleManager INSTANCE;
    public ConfigManager configManager;
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private final List<Module> modules = new CopyOnWriteArrayList<>();

    public void init() {
        INSTANCE = this;
        EventBus.register(this);
    }

    public static List<Module> getAll() {
        return INSTANCE != null ? INSTANCE.getAllModules() : List.of();
    }

    private List<Module> getAllModules() {
        return new ArrayList<>(modules);
    }

    public void register(Module module) {
        modules.add(module);
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T get(Class<T> clazz) {
        for (Module module : modules) {
            if (module.getClass() == clazz) return (T) module;
        }
        return null;
    }

    public Module get(String name) {
        for (Module module : modules) {
            if (module.getName().equalsIgnoreCase(name)) return module;
        }
        return null;
    }

    public List<Module> getByCategory(ModuleCategory category) {
        List<Module> result = new ArrayList<>();
        for (Module module : modules) {
            if (module.getCategory() == category) result.add(module);
        }
        return result;
    }

    public List<Module> getEnabled() {
        List<Module> result = new ArrayList<>();
        for (Module module : modules) {
            if (module.isEnabled()) result.add(module);
        }
        return result;
    }

    @EventHandler
    private void onKey(KeyPressEvent event) {
        if (event.getKey() == -1) return;
        if (!event.isPressed()) return;
        if (mc.currentScreen != null) return;
        for (Module module : modules) {
            if (module.getKey() == event.getKey()) {
                module.toggle();
            }
        }
    }
}
