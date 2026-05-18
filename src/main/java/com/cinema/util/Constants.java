package com.cinema.util;

import java.awt.Color;
import java.awt.Font;

public final class Constants {
    private Constants() {}

    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);

    public static final Color COLOR_PRIMARY = new Color(41, 128, 185);
    public static final Color COLOR_SUCCESS = new Color(46, 204, 113);
    public static final Color COLOR_DANGER = new Color(231, 76, 60);
    public static final Color COLOR_WARNING = new Color(241, 196, 15);
    public static final Color COLOR_BACKGROUND = new Color(245, 246, 250);
    public static final Color COLOR_CARD = new Color(255, 255, 255);
    public static final Color COLOR_TEXT = new Color(44, 62, 80);
    public static final Color COLOR_TEXT_MUTED = new Color(127, 140, 141);

    public static final String[] PAYMENT_METHODS = {"CASH", "CARD", "QR", "ONLINE"};
    public static final String[] SEAT_TYPES = {"STANDARD", "VIP", "IMAX"};
}
