package com.cinema.ui.components;

import com.cinema.util.Constants;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class SidebarButton extends JButton {
    private boolean active = false;
    private final SidebarIcon sidebarIcon;

    public SidebarButton(String text, SidebarIcon icon) {
        super(text);
        this.sidebarIcon = icon;
        setIcon(icon);
        setIconTextGap(10);

        setFont(Constants.FONT_BODY);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setForeground(Constants.COLOR_TEXT_MUTED);
        setPreferredSize(new Dimension(228, 48));
        setMaximumSize(new Dimension(228, 48));
        setMinimumSize(new Dimension(228, 48));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setHorizontalAlignment(SwingConstants.LEFT);
        setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        updateIconColor();
    }

    public void setActive(boolean active) {
        this.active = active;
        updateColors();
        repaint();
    }

    private void updateColors() {
        if (active) {
            setForeground(Color.WHITE);
        } else if (getModel().isRollover()) {
            setForeground(Constants.COLOR_TEXT);
        } else {
            setForeground(Constants.COLOR_TEXT_MUTED);
        }
        updateIconColor();
    }

    private void updateIconColor() {
        Color target;
        if (active) {
            target = Color.WHITE;
        } else if (getModel().isRollover()) {
            target = Constants.COLOR_TEXT;
        } else {
            target = Constants.COLOR_TEXT_MUTED;
        }
        sidebarIcon.setColor(target);
    }

    @Override
    protected void paintComponent(Graphics g) {
        updateColors();

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        if (active) {
            g2.setColor(Constants.COLOR_CARD);
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 8, 8));
            g2.setColor(Constants.COLOR_PRIMARY);
            g2.fillRect(0, 8, 4, h - 16);
        } else if (getModel().isRollover()) {
            g2.setColor(new Color(0xFFFFFF));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.06f));
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 8, 8));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            setForeground(Constants.COLOR_TEXT);
        } else {
            setForeground(Constants.COLOR_TEXT_MUTED);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
