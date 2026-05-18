package com.cinema.ui.components;

import com.cinema.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Random;

public class HeatmapPanel extends RoundedPanel {

    private final String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private final String[] times = {"10AM", "12PM", "2PM", "4PM", "6PM", "8PM"};
    private final float[][] density;

    public HeatmapPanel() {
        setBackground(Constants.COLOR_CARD);
        setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        setArc(14);

        density = new float[times.length][days.length];
        Random r = new Random(7);
        for (int t = 0; t < times.length; t++) {
            for (int d = 0; d < days.length; d++) {
                density[t][d] = r.nextFloat();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int topPad = 36;
        int leftPad = 40;
        int rightPad = 8;
        int bottomPad = 4;
        int cellGap = 3;

        int cellW = (w - leftPad - rightPad - (days.length - 1) * cellGap) / days.length;
        int cellH = (h - topPad - bottomPad - (times.length - 1) * cellGap) / times.length;
        int maxCell = Math.min(cellW, cellH);
        int offsetX = leftPad + (w - leftPad - rightPad - days.length * maxCell - (days.length - 1) * cellGap) / 2;
        int offsetY = topPad + (h - topPad - bottomPad - times.length * maxCell - (times.length - 1) * cellGap) / 2;

        // Title
        g2.setFont(Constants.FONT_SUBHEADER);
        g2.setColor(Constants.COLOR_TEXT);
        g2.drawString("Customers by time", 16, 24);

        // Day headers
        g2.setFont(Constants.FONT_TINY);
        g2.setColor(Constants.COLOR_TEXT_SECONDARY);
        for (int d = 0; d < days.length; d++) {
            int x = offsetX + d * (maxCell + cellGap);
            int tw = g2.getFontMetrics().stringWidth(days[d]);
            g2.drawString(days[d], x + (maxCell - tw) / 2, topPad - 6);
        }

        // Time headers
        for (int t = 0; t < times.length; t++) {
            int y = offsetY + t * (maxCell + cellGap) + maxCell / 2 + 4;
            int tw = g2.getFontMetrics().stringWidth(times[t]);
            g2.drawString(times[t], leftPad - tw - 6, y);
        }

        // Cells
        for (int t = 0; t < times.length; t++) {
            for (int d = 0; d < days.length; d++) {
                int x = offsetX + d * (maxCell + cellGap);
                int y = offsetY + t * (maxCell + cellGap);
                Color c = interpolateColor(Constants.COLOR_HEATMAP_LOW, Constants.COLOR_HEATMAP_HIGH, density[t][d]);
                g2.setColor(c);
                g2.fill(new RoundRectangle2D.Float(x, y, maxCell, maxCell, 3, 3));
            }
        }

        g2.dispose();
    }

    private Color interpolateColor(Color from, Color to, float ratio) {
        int r = (int) (from.getRed() + (to.getRed() - from.getRed()) * ratio);
        int g = (int) (from.getGreen() + (to.getGreen() - from.getGreen()) * ratio);
        int b = (int) (from.getBlue() + (to.getBlue() - from.getBlue()) * ratio);
        return new Color(r, g, b);
    }
}
