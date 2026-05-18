package com.cinema.ui.components;

import com.cinema.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;

public class MovieShowtimeCard extends RoundedPanel {

    public MovieShowtimeCard(String title, String time, int percentBooked, boolean soldOut) {
        setLayout(new BorderLayout(10, 0));
        setBackground(Constants.COLOR_CARD_ELEVATED);
        setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        setArc(10);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Constants.FONT_BODY);
        titleLabel.setForeground(Constants.COLOR_TEXT);

        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(Constants.FONT_SMALL);
        timeLabel.setForeground(Constants.COLOR_TEXT_SECONDARY);

        textPanel.add(titleLabel);
        textPanel.add(timeLabel);
        add(textPanel, BorderLayout.CENTER);

        if (soldOut) {
            JLabel badge = new JLabel("SOLD OUT", SwingConstants.CENTER);
            badge.setFont(Constants.FONT_TINY);
            badge.setForeground(Color.WHITE);
            badge.setOpaque(true);
            badge.setBackground(Constants.COLOR_PRIMARY);
            badge.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
            JPanel wrap = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            wrap.setOpaque(false);
            wrap.add(badge);
            add(wrap, BorderLayout.EAST);
        } else {
            JPanel ring = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    int size = Math.min(getWidth(), getHeight()) - 4;
                    int x = (getWidth() - size) / 2;
                    int y = (getHeight() - size) / 2;

                    g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.setColor(Constants.COLOR_CHART_TRACK);
                    g2.draw(new Arc2D.Float(x, y, size, size, 90, 360, Arc2D.OPEN));

                    int sweep = (int) (360 * percentBooked / 100.0);
                    g2.setColor(Constants.COLOR_PRIMARY);
                    g2.draw(new Arc2D.Float(x, y, size, size, 90, -sweep, Arc2D.OPEN));

                    g2.setFont(Constants.FONT_TINY);
                    g2.setColor(Constants.COLOR_TEXT_MUTED);
                    String txt = percentBooked + "%";
                    FontMetrics fm = g2.getFontMetrics();
                    int tx = (getWidth() - fm.stringWidth(txt)) / 2;
                    int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(txt, tx, ty);

                    g2.dispose();
                }
            };
            ring.setOpaque(false);
            ring.setPreferredSize(new Dimension(44, 44));
            add(ring, BorderLayout.EAST);
        }
    }
}
