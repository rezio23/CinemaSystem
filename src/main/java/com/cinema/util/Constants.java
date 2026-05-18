package com.cinema.util;

import java.awt.Color;
import java.awt.Font;

public final class Constants {
    private Constants() {}

    // Typography: clean hierarchy using Segoe UI (system modern sans-serif)
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);

    // Dark theme palette
    public static final Color COLOR_BACKGROUND = new Color(0x1E1E2D);      // deepest background
    public static final Color COLOR_SIDEBAR = new Color(0x161622);           // sidebar depth
    public static final Color COLOR_CARD = new Color(0x27273A);            // card / panel surface
    public static final Color COLOR_CARD_ELEVATED = new Color(0x2B2B40);   // inputs, table bg
    public static final Color COLOR_BORDER = new Color(0x3F3F5F);          // subtle dividers

    // Cinematic accent
    public static final Color COLOR_PRIMARY = new Color(0x3B82F6);         // neon blue
    public static final Color COLOR_PRIMARY_HOVER = new Color(0x2563EB);
    public static final Color COLOR_PRIMARY_PRESS = new Color(0x1D4ED8);

    // Semantic
    public static final Color COLOR_SUCCESS = new Color(0x10B981);
    public static final Color COLOR_DANGER = new Color(0xEF4444);
    public static final Color COLOR_WARNING = new Color(0xF59E0B);

    // Text
    public static final Color COLOR_TEXT = new Color(0xE2E8F0);            // primary text (slate-200)
    public static final Color COLOR_TEXT_MUTED = new Color(0x94A3B8);      // secondary labels (slate-400)

    public static final String[] PAYMENT_METHODS = {"CASH", "CARD", "QR", "ONLINE"};
    public static final String[] SEAT_TYPES = {"STANDARD", "VIP", "IMAX"};
}
