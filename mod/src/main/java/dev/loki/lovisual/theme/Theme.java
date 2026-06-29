package dev.loki.lovisual.theme;

import java.awt.Color;

public class Theme {
    private Color background = new Color(0x1A1A2E);
    private Color accent = new Color(0xDC1C1C);
    private Color panelBg = new Color(0x0D0D1A);
    private Color panelBorder = new Color(0x1E1E3A);
    private Color panelHover = new Color(0x2A2A4A);
    private Color textPrimary = new Color(0xCCCCCC);
    private Color textSecondary = new Color(0x888888);
    private Color hudBg = new Color(0x333355);

    private Color gradientStart = new Color(0x1A1A2E);
    private Color gradientEnd = new Color(0x0D0D1A);
    private String gradientDir = "Vertical";

    public Color getBackground() { return background; }
    public void setBackground(Color c) { this.background = c; }
    public Color getAccent() { return accent; }
    public void setAccent(Color c) { this.accent = c; }
    public Color getPanelBg() { return panelBg; }
    public void setPanelBg(Color c) { this.panelBg = c; }
    public Color getPanelBorder() { return panelBorder; }
    public void setPanelBorder(Color c) { this.panelBorder = c; }
    public Color getPanelHover() { return panelHover; }
    public void setPanelHover(Color c) { this.panelHover = c; }
    public Color getTextPrimary() { return textPrimary; }
    public void setTextPrimary(Color c) { this.textPrimary = c; }
    public Color getTextSecondary() { return textSecondary; }
    public void setTextSecondary(Color c) { this.textSecondary = c; }
    public Color getHudBg() { return hudBg; }
    public void setHudBg(Color c) { this.hudBg = c; }

    public Color getGradientStart() { return gradientStart; }
    public void setGradientStart(Color c) { this.gradientStart = c; }
    public Color getGradientEnd() { return gradientEnd; }
    public void setGradientEnd(Color c) { this.gradientEnd = c; }
    public String getGradientDir() { return gradientDir; }
    public void setGradientDir(String d) { this.gradientDir = d; }
}
