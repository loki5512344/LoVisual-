package dev.loki.lovisual.module;

import dev.loki.lovisual.core.event.Event;
import dev.loki.lovisual.core.event.EventBus;
import dev.loki.lovisual.setting.Setting;
import dev.loki.lovisual.core.event.EventHandler;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    private final String name;
    private final String desc;
    private final ModuleCategory category;
    private int key;

    private boolean enabled;
    private final List<Setting<?>> settings = new ArrayList<>();

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected static MinecraftClient mc = MinecraftClient.getInstance();

    public Module() {
        ModuleInfo info = getClass().getAnnotation(ModuleInfo.class);
        if (info == null) throw new IllegalStateException("Module missing @ModuleInfo: " + getClass());

        this.name = info.name();
        this.desc = info.desc();
        this.category = info.category();
        this.key = info.key();

        collectSettings();
    }

    @SuppressWarnings("unchecked")
    private void collectSettings() {
        for (Field field : getClass().getDeclaredFields()) {
            if (Setting.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                try {
                    Setting<?> setting = (Setting<?>) field.get(this);
                    if (setting != null) {
                        settings.add(setting);
                    }
                } catch (IllegalAccessException ignored) {}
            }
        }
    }

    public final void toggle() {
        if (enabled) {
            disable();
        } else {
            enable();
        }
        saveConfig();
    }

    public final void enable() {
        if (enabled) return;
        enabled = true;
        EventBus.register(this);
        onEnable();
    }

    public final void disable() {
        if (!enabled) return;
        enabled = false;
        EventBus.unregister(this);
        onDisable();
    }

    private void saveConfig() {
        if (ModuleManager.INSTANCE != null && ModuleManager.INSTANCE.configManager != null) {
            ModuleManager.INSTANCE.configManager.save();
        }
    }

    protected void onEnable() {}
    protected void onDisable() {}

    public final <T extends Event> void post(T event) {
        EventBus.post(event);
    }

    public Setting<?> getSetting(String name) {
        for (Setting<?> setting : settings) {
            if (setting.getName().equalsIgnoreCase(name)) return setting;
        }
        return null;
    }

    public String getName() { return name; }
    public String getDesc() { return desc; }
    public ModuleCategory getCategory() { return category; }
    public int getKey() { return key; }
    public void setKey(int key) { this.key = key; saveConfig(); }
    public boolean isEnabled() { return enabled; }
    public List<Setting<?>> getSettings() { return settings; }
}
