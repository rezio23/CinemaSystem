package com.cinema.ui.components;

import com.cinema.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class StyledButton extends JButton {
    public enum Variant { PRIMARY, SUCCESS, DANGER, SECONDARY }

    private final Variant variant;
    private static final int ARC = 8;

    public StyledButton(String text, Variant variant) {
        super(text);
        this.variant = variant;
        setFont(Constants.FONT_BODY);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        setForeground(foregroundColor());
    }

    private Color baseColor() {
        return switch (variant) {
            case PRIMARY -> Constants.COLOR_PRIMARY;
            case SUCCESS -> Constants.COLOR_SUCCESS;
            case DANGER -> Constants.COLOR_DANGER;
            case SECONDARY -> Constants.COLOR_CARD_ELEVATED;
        };
    }

    private Color foregroundColor() {
        return switch (variant) {
            case PRIMARY, SUCCESS, DANGER -> Color.WHITE;
            case SECONDARY -> Constants.COLOR_TEXT;
        };
    }

    private Color hoverColor() {
        return switch (variant) {
            case PRIMARY -> Constants.COLOR_PRIMARY_LIGHT;
            case SUCCESS -> new Color(0x34D399);
            case DANGER -> new Color(0xF87171);
            case SECONDARY -> new Color(0x3A3A3A);
        };
    }

    private Color pressColor() {
        return switch (variant) {
            case PRIMARY -> Constants.COLOR_PRIMARY_DARK;
            case SUCCESS -> new Color(0x16A34A);
            case DANGER -> new Color(0xDC2626);
            case SECONDARY -> Constants.COLOR_CARD;
        };
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        Color bg;
        if (getModel().isPressed()) {
            bg = pressColor();
        } else if (getModel().isRollover()) {
            bg = hoverColor();
        } else {
            bg = baseColor();
        }

        g2.setColor(bg);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, ARC, ARC));

        g2.dispose();
        super.paintComponent(g);
    }
}
