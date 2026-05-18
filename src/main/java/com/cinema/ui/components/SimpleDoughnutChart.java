package com.cinema.ui.components;

import com.cinema.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

public class SimpleDoughnutChart extends JPanel {
    private double[] values = {1};
    private String[] labels = {"Default"};
    private Color[] colors = {Constants.COLOR_PRIMARY};
    private String centerValue = "0";
    private String centerLabel = "Total";
    private String chartTitle = "Booking Status";

    public SimpleDoughnutChart() {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(40, 20, 20, 20));
        setPreferredSize(new Dimension(280, 260));
        setMinimumSize(new Dimension(240, 220));
    }

    public void setData(double[] values, String[] labels, Color[] colors, String centerValue, String centerLabel, String title) {
        this.values = values != null ? values : this.values;
        this.labels = labels != null ? labels : this.labels;
        this.colors = colors != null ? colors : this.colors;
        this.centerValue = centerValue != null ? centerValue : this.centerValue;
        this.centerLabel = centerLabel != null ? centerLabel : this.centerLabel;
        this.chartTitle = title != null ? title : this.chartTitle;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        g2.setFont(Constants.FONT_SUBHEADER);
        g2.setColor(Constants.COLOR_TEXT);
        g2.drawString(chartTitle, 20, 22);

        int size = Math.min(w - 40, h - 80);
        int x = (w - size) / 2;
        int y = 35 + (h - 80 - size) / 2;
        int thickness = 28;

        double total = 0;
        for (double v : values) total += v;
        if (total == 0) total = 1;

        double start = 90;
        for (int i = 0; i < values.length; i++) {
            double sweep = 360.0 * (values[i] / total);
            g2.setColor(colors[i % colors.length]);
            g2.setStroke(new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            g2.draw(new Arc2D.Float(x, y, size, size, (float) start, (float) -sweep, Arc2D.OPEN));
            start -= sweep;
        }

        // Center text
        g2.setFont(new Font("Segoe UI", Font.BOLD, 24));
        g2.setColor(Constants.COLOR_TEXT);
        FontMetrics fm = g2.getFontMetrics();
        int cx = (w - fm.stringWidth(centerValue)) / 2;
        int cy = y + size / 2 + fm.getAscent() / 2 - 6;
        g2.drawString(centerValue, cx, cy);

        g2.setFont(Constants.FONT_SMALL);
        g2.setColor(Constants.COLOR_TEXT_MUTED);
        String sub = centerLabel;
        int sx = (w - g2.getFontMetrics().stringWidth(sub)) / 2;
        g2.drawString(sub, sx, cy + 18);

        // Legend
        if (labels.length > 0) {
            int ly = h - 36;
            int totalLegendWidth = labels.length * 60;
            int lx = Math.max(20, (w - totalLegendWidth) / 2);
            int gap = labels.length > 1 ? (w - lx * 2 - labels.length * 50) / (labels.length - 1) : 0;
            for (int i = 0; i < labels.length; i++) {
                g2.setColor(colors[i % colors.length]);
                g2.fill(new Ellipse2D.Float(lx, ly, 8, 8));
                g2.setFont(Constants.FONT_TINY);
                g2.setColor(Constants.COLOR_TEXT_MUTED);
                g2.drawString(labels[i], lx + 12, ly + 8);
                lx += 50 + gap;
            }
        }

        g2.dispose();
    }
}
