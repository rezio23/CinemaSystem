package com.cinema.ui.components;

import com.cinema.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class CalendarPanel extends RoundedPanel {
    private final String[] days = {"S", "M", "T", "W", "T", "F", "S"};
    private final int[] dates = {16, 17, 18, 19, 20, 21, 22};
    private final int activeIndex = 2;

    public CalendarPanel() {
        setLayout(new BorderLayout(8, 8));
        setBackground(Constants.COLOR_CARD);
        setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        setArc(12);

        JLabel monthLabel = new JLabel("Dec, 18 Today");
        monthLabel.setFont(Constants.FONT_SUBHEADER);
        monthLabel.setForeground(Constants.COLOR_TEXT);
        add(monthLabel, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 7, 6, 6));
        grid.setOpaque(false);

        for (String day : days) {
            JLabel d = new JLabel(day, SwingConstants.CENTER);
            d.setFont(Constants.FONT_SMALL);
            d.setForeground(Constants.COLOR_TEXT_SECONDARY);
            grid.add(d);
        }

        for (int i = 0; i < 7; i++) {
            boolean active = i == activeIndex;
            JLabel dateLabel = new JLabel(String.valueOf(dates[i]), SwingConstants.CENTER) {
                @Override
                protected void paintComponent(Graphics g) {
                    if (active) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(Constants.COLOR_PRIMARY);
                        int s = Math.min(getWidth(), getHeight()) - 4;
                        int x = (getWidth() - s) / 2;
                        int y = (getHeight() - s) / 2;
                        g2.fill(new Ellipse2D.Float(x, y, s, s));
                        g2.dispose();
                    }
                    super.paintComponent(g);
                }
            };
            dateLabel.setFont(Constants.FONT_BODY);
            dateLabel.setForeground(active ? Color.WHITE : Constants.COLOR_TEXT_MUTED);
            grid.add(dateLabel);
        }

        add(grid, BorderLayout.CENTER);
    }
}
