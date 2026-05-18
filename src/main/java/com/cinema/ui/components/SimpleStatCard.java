package com.cinema.ui.components;

import com.cinema.util.Constants;

import javax.swing.*;
import java.awt.*;

public class SimpleStatCard extends JPanel {
    public SimpleStatCard(String label, String value, Color accent, String trend) {
        setLayout(new BorderLayout(4, 4));
        setBackground(Constants.COLOR_CARD);
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(Constants.COLOR_TEXT);

        JLabel labelLbl = new JLabel(label);
        labelLbl.setFont(Constants.FONT_SMALL);
        labelLbl.setForeground(Constants.COLOR_TEXT_MUTED);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(valueLabel, BorderLayout.NORTH);
        top.add(labelLbl, BorderLayout.SOUTH);

        add(top, BorderLayout.CENTER);

        if (trend != null) {
            JLabel trendLbl = new JLabel(trend);
            trendLbl.setFont(Constants.FONT_TINY);
            trendLbl.setForeground(accent);
            add(trendLbl, BorderLayout.SOUTH);
        }

        // Accent bar at top
        JPanel bar = new JPanel();
        bar.setBackground(accent);
        bar.setPreferredSize(new Dimension(0, 3));
        add(bar, BorderLayout.NORTH);
    }
}
