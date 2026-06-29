package dev.loki.lovisual.theme;

import java.awt.Color;

public class ThemeManager {
    private static final Theme current = new Theme();

    public static Theme getCurrent() {
        return current;
    }

    public static Color getBackground() { return current.getBackground(); }
    public static Color getAccent() { return current.getAccent(); }
    public static Color getPanelBg() { return current.getPanelBg(); }
    public static Color getPanelBorder() { return current.getPanelBorder(); }
    public static Color getPanelHover() { return current.getPanelHover(); }
    public static Color getTextPrimary() { return current.getTextPrimary(); }
    public static Color getTextSecondary() { return current.getTextSecondary(); }
    public static Color getHudBg() { return current.getHudBg(); }
    public static Color getGradientStart() { return current.getGradientStart(); }
    public static Color getGradientEnd() { return current.getGradientEnd(); }
    public static String getGradientDir() { return current.getGradientDir(); }

    public static int getBackgroundRGB() { return current.getBackground().getRGB(); }
    public static int getAccentRGB() { return current.getAccent().getRGB(); }
    public static int getPanelBgRGB() { return current.getPanelBg().getRGB(); }
    public static int getPanelBorderRGB() { return current.getPanelBorder().getRGB(); }
    public static int getPanelHoverRGB() { return current.getPanelHover().getRGB(); }
    public static int getTextPrimaryRGB() { return current.getTextPrimary().getRGB(); }
    public static int getTextSecondaryRGB() { return current.getTextSecondary().getRGB(); }
    public static int getHudBgRGB() { return current.getHudBg().getRGB(); }
}
