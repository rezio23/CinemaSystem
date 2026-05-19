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
    public static final Color COLOR_PRIMARY_LIGHTER = new Color(0x5C6BC0);
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

    public static final int INPUT_HEIGHT = 36;
    public static final int PAGE_HEADER_GAP = 12;
    public static final int BORDER_RADIUS = 8;
    public static final java.awt.Insets INPUT_PADDING = new java.awt.Insets(8, 14, 8, 14);
    public static final java.awt.Insets BUTTON_PADDING = new java.awt.Insets(8, 16, 8, 16);
    public static final Color INPUT_BORDER_COLOR = new Color(0xD0D9E2);

    public static javax.swing.border.Border createRoundedBorder(Color color, int thickness, int arc) {
        return new javax.swing.border.AbstractBorder() {
            @Override
            public void paintBorder(java.awt.Component c, java.awt.Graphics g, int x, int y, int width, int height) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new java.awt.BasicStroke(thickness));
                int off = thickness / 2;
                g2.drawRoundRect(x + off, y + off, width - thickness, height - thickness, arc, arc);
                g2.dispose();
            }
            @Override
            public java.awt.Insets getBorderInsets(java.awt.Component c) {
                return new java.awt.Insets(thickness, thickness, thickness, thickness);
            }
        };
    }

    public static javax.swing.border.Border inputBorder() {
        return javax.swing.BorderFactory.createCompoundBorder(
            createRoundedBorder(INPUT_BORDER_COLOR, 1, BORDER_RADIUS),
            javax.swing.BorderFactory.createEmptyBorder(INPUT_PADDING.top, INPUT_PADDING.left, INPUT_PADDING.bottom, INPUT_PADDING.right)
        );
    }

    public static javax.swing.border.Border inputBorderFocused() {
        return javax.swing.BorderFactory.createCompoundBorder(
            createRoundedBorder(COLOR_PRIMARY, 1, BORDER_RADIUS),
            javax.swing.BorderFactory.createEmptyBorder(INPUT_PADDING.top, INPUT_PADDING.left, INPUT_PADDING.bottom, INPUT_PADDING.right)
        );
    }

    public static void styleInput(javax.swing.JTextField field) {
        field.setBackground(COLOR_CARD_ELEVATED);
        field.setFont(FONT_BODY);
        field.setForeground(COLOR_TEXT);
        field.setCaretColor(COLOR_TEXT);
        field.putClientProperty("JComponent.roundRect", true);
        field.setBorder(inputBorder());
        field.setPreferredSize(new java.awt.Dimension(field.getPreferredSize().width, INPUT_HEIGHT));
    }

    public static void styleInput(javax.swing.JComboBox<?> combo) {
        combo.setBackground(COLOR_CARD_ELEVATED);
        combo.setFont(FONT_BODY);
        combo.setForeground(COLOR_TEXT);
        combo.putClientProperty("JComponent.roundRect", true);
        combo.setPreferredSize(new java.awt.Dimension(combo.getPreferredSize().width, INPUT_HEIGHT));
    }

    public static javax.swing.JPanel wrapInput(javax.swing.JComponent component) {
        javax.swing.JPanel wrapper = new javax.swing.JPanel(new java.awt.BorderLayout());
        wrapper.setBackground(COLOR_CARD_ELEVATED);
        wrapper.setBorder(inputBorder());
        if (component instanceof javax.swing.JTextField) {
            ((javax.swing.JTextField) component).setBorder(null);
            ((javax.swing.JTextField) component).setOpaque(false);
        } else {
            component.setBorder(javax.swing.BorderFactory.createEmptyBorder());
            component.setOpaque(false);
        }
        component.setBackground(COLOR_CARD_ELEVATED);
        component.setFont(FONT_BODY);
        component.setForeground(COLOR_TEXT);
        wrapper.add(component, java.awt.BorderLayout.CENTER);
        return wrapper;
    }

    public static void styleInput(javax.swing.JSpinner spinner) {
        spinner.setFont(FONT_BODY);
        spinner.putClientProperty("JComponent.roundRect", true);
        spinner.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        spinner.setPreferredSize(new java.awt.Dimension(spinner.getPreferredSize().width, INPUT_HEIGHT));
        javax.swing.JComponent editor = spinner.getEditor();
        if (editor instanceof javax.swing.JSpinner.DefaultEditor) {
            javax.swing.JTextField tf = ((javax.swing.JSpinner.DefaultEditor) editor).getTextField();
            tf.setBackground(COLOR_CARD_ELEVATED);
            tf.setForeground(COLOR_TEXT);
            tf.setCaretColor(COLOR_TEXT);
            tf.setFont(FONT_BODY);
            tf.putClientProperty("JComponent.roundRect", true);
            tf.setBorder(inputBorder());
            tf.setPreferredSize(new java.awt.Dimension(tf.getPreferredSize().width, INPUT_HEIGHT));
        }
    }
}
