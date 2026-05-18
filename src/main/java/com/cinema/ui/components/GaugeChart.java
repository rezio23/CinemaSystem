package com.cinema.ui.components;

import com.cinema.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;

public class GaugeChart extends RoundedPanel {

    private final double percent;

    public GaugeChart(double percent) {
        this.percent = Math.max(0, Math.min(100, percent));
        setBackground(Constants.COLOR_CARD);
        setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        setArc(14);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int pad = 16;
        int size = Math.min(w - pad * 2, (h - pad) * 2);
        int x = (w - size) / 2;
        int y = h - pad - size / 2;

        // Title
        g2.setFont(Constants.FONT_SUBHEADER);
        g2.setColor(Constants.COLOR_TEXT);
        g2.drawString("Customer satisfaction", 16, 24);

        float stroke = size / 10f;
        g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));

        // Track
        g2.setColor(Constants.COLOR_CHART_TRACK);
        g2.draw(new Arc2D.Float(x, y, size, size, 180, 180, Arc2D.OPEN));

        // Fill
        double sweep = 180 * (percent / 100.0);
        g2.setColor(Constants.COLOR_PRIMARY);
        g2.draw(new Arc2D.Float(x, y, size, size, 180, (float) sweep, Arc2D.OPEN));

        // Percent text
        g2.setFont(new Font("Segoe UI", Font.BOLD, 32));
        g2.setColor(Constants.COLOR_TEXT);
        String text = (int) percent + "%";
        FontMetrics fm = g2.getFontMetrics();
        int tx = (w - fm.stringWidth(text)) / 2;
        int ty = y + size / 2 + fm.getAscent() / 2 - 4;
        g2.drawString(text, tx, ty);

        // Subtitle
        g2.setFont(Constants.FONT_SMALL);
        g2.setColor(Constants.COLOR_TEXT_MUTED);
        String sub = "Based on 1.2k reviews";
        int sx = (w - g2.getFontMetrics().stringWidth(sub)) / 2;
        g2.drawString(sub, sx, ty + 20);

        g2.dispose();
    }
}
