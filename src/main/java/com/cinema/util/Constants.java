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
    public static final Color COLOR_BACKGROUND = new Color(0x1A1A1A);
    public static final Color COLOR_SIDEBAR = new Color(0x151515);
    public static final Color COLOR_CARD = new Color(0x282828);
    public static final Color COLOR_CARD_ELEVATED = new Color(0x323232);

    // Accent
    public static final Color COLOR_PRIMARY = new Color(0xFF6B00);        // vibrant orange
    public static final Color COLOR_PRIMARY_LIGHT = new Color(0xFF8533);
    public static final Color COLOR_PRIMARY_DARK = new Color(0xCC5500);

    // Semantic
    public static final Color COLOR_SUCCESS = new Color(0x22C55E);
    public static final Color COLOR_DANGER = new Color(0xEF4444);
    public static final Color COLOR_WARNING = new Color(0xF59E0B);

    // Text
    public static final Color COLOR_TEXT = new Color(0xFFFFFF);
    public static final Color COLOR_TEXT_MUTED = new Color(0xA0A0A0);
    public static final Color COLOR_TEXT_SECONDARY = new Color(0x666666);

    // Charts / Data visualization
    public static final Color COLOR_CHART_TRACK = new Color(0x3A3A3A);
    public static final Color COLOR_CHART_BAR_BASE = new Color(0x3A3A3A);
    public static final Color COLOR_CHART_BAR_FILL = new Color(0xFF6B00);
    public static final Color COLOR_SEAT_AVAILABLE = new Color(0x4A4A4A);
    public static final Color COLOR_SEAT_BOOKED = new Color(0x1A1A1A);
    public static final Color COLOR_HEATMAP_LOW = new Color(0x3A2515);
    public static final Color COLOR_HEATMAP_HIGH = new Color(0xFF6B00);

    // Shadows / borders
    public static final Color COLOR_SHADOW = new Color(0x000000);

    public static final String[] PAYMENT_METHODS = {"CASH", "CARD", "QR", "ONLINE"};
    public static final String[] SEAT_TYPES = {"STANDARD", "VIP", "IMAX"};
}
