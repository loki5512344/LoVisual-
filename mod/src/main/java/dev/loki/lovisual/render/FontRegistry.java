package dev.loki.lovisual.render;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class FontRegistry {
    private static final Map<String, MSDFFont> fonts = new HashMap<>();

    public static MSDFFont register(String name, Identifier fontId) {
        MSDFFont font = new MSDFFont(fontId);
        fonts.put(name, font);
        return font;
    }

    public static MSDFFont get(String name) {
        return fonts.get(name);
    }

    public static MSDFFont getDefault() {
        return fonts.getOrDefault("default", null);
    }

    public static void init() {
        register("default", Identifier.of("minecraft", "default"));
        register("uniform", Identifier.of("minecraft", "uniform"));
        register("alt", Identifier.of("minecraft", "alt"));
    }
}
