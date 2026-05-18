package com.cinema.ui.components;

import com.cinema.util.Constants;

import javax.swing.*;
import java.awt.*;

public class SidebarButton extends JButton {
    private boolean active = false;

    public SidebarButton(String text, String iconUnicode) {
        super(iconUnicode + "  " + text);
        setFont(Constants.FONT_BODY);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(true);
        setBackground(Constants.COLOR_PRIMARY);
        setForeground(Color.WHITE);
        setPreferredSize(new Dimension(220, 45));
        setMaximumSize(new Dimension(220, 45));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setHorizontalAlignment(SwingConstants.LEFT);
        setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
    }

    public void setActive(boolean active) {
        this.active = active;
        if (active) {
            setBackground(Constants.COLOR_PRIMARY.darker());
            setForeground(Color.WHITE);
        } else {
            setBackground(Constants.COLOR_PRIMARY);
            setForeground(Color.WHITE);
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (active) {
            setBackground(Constants.COLOR_PRIMARY.darker());
        } else if (getModel().isRollover()) {
            setBackground(Constants.COLOR_PRIMARY.brighter());
        } else {
            setBackground(Constants.COLOR_PRIMARY);
        }
        super.paintComponent(g);
        if (active) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, 4, getHeight());
            g2.dispose();
        }
    }
}
