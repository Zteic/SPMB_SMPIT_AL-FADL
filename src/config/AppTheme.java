package config;

import java.awt.Color;
import java.awt.Font;

public class AppTheme {
    public static final String APP_NAME = "SPMB SMPIT AL FADL";

    // BRAND COLORS (Flat Modern UI)
    public static final Color PRIMARY = Color.decode("#2C3E50");
    public static final Color ACCENT = Color.decode("#3498DB");
    public static final Color SUCCESS = Color.decode("#2ECC71");
    public static final Color WARNING = Color.decode("#F1C40F");
    public static final Color DANGER = Color.decode("#E74C3C");
    
    // BACKGROUND & SURFACE
    public static final Color BACKGROUND = Color.decode("#F8F9FA");
    public static final Color SURFACE = Color.WHITE;
    public static final Color SIDEBAR_BG = Color.decode("#1A252F");
    public static final Color SIDEBAR_HOVER = Color.decode("#2C3E50");

    // TEXT & BORDER
    public static final Color TEXT_DARK = Color.decode("#2C3E50");
    public static final Color TEXT_SECONDARY = Color.decode("#7F8C8D");
    public static final Color TEXT_LIGHT = Color.WHITE;
    public static final Color BORDER = Color.decode("#E0E6ED");

    // TYPOGRAPHY (Modern Sans-Serif Look)
    public static final Font TITLE_LARGE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font SUBTITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font CAPTION = new Font("Segoe UI", Font.ITALIC, 11);
}