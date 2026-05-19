package com.cinema.util;

import java.awt.Color;
import java.awt.Font;

public final class Constants {
    private Constants() {}

    // Typography
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_TINY = new Font("Segoe UI", Font.PLAIN, 11);

    // ============================================================
    // ORANGE DARK DASHBOARD PALETTE
    // ============================================================

    // Backgrounds
    public static final Color COLOR_BACKGROUND = new Color(0xF5F7FA);
    public static final Color COLOR_SIDEBAR = new Color(0xFFFFFF);
    public static final Color COLOR_CARD = new Color(0xFFFFFF);
    public static final Color COLOR_CARD_ELEVATED = new Color(0xE8EDF2);

    // Accent
    public static final Color COLOR_PRIMARY = new Color(0x1A237E);        // navy
    public static final Color COLOR_PRIMARY_LIGHT = new Color(0x3949AB);
    public static final Color COLOR_PRIMARY_DARK = new Color(0x0D1B2A);

    // Semantic
    public static final Color COLOR_SUCCESS = new Color(0x22C55E);
    public static final Color COLOR_DANGER = new Color(0xEF4444);
    public static final Color COLOR_WARNING = new Color(0xF59E0B);

    // Text
    public static final Color COLOR_TEXT = new Color(0x0D1B2A);
    public static final Color COLOR_TEXT_MUTED = new Color(0x546E7A);
    public static final Color COLOR_TEXT_SECONDARY = new Color(0x78909C);

    // Charts / Data visualization
    public static final Color COLOR_CHART_TRACK = new Color(0xE0E6ED);
    public static final Color COLOR_CHART_BAR_BASE = new Color(0xE0E6ED);
    public static final Color COLOR_CHART_BAR_FILL = new Color(0x1A237E);
    public static final Color COLOR_SEAT_AVAILABLE = new Color(0xE0E6ED);
    public static final Color COLOR_SEAT_BOOKED = new Color(0x0D1B2A);
    public static final Color COLOR_HEATMAP_LOW = new Color(0xE8EDF2);
    public static final Color COLOR_HEATMAP_HIGH = new Color(0x1A237E);

    // Shadows / borders
    public static final Color COLOR_SHADOW = new Color(0x000000);

    public static final String[] PAYMENT_METHODS = {"CASH", "CARD", "QR", "ONLINE"};
    public static final String[] SEAT_TYPES = {"STANDARD", "VIP", "IMAX"};
}
