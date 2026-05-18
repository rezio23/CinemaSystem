package com.cinema.ui.components;

import com.cinema.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class IncomeBarChart extends RoundedPanel {

    private final String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private final double[] baseValues = {80, 65, 90, 55, 70, 85, 60};
    private final double[] fillValues = {55, 45, 70, 35, 50, 65, 40};

    public IncomeBarChart() {
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
        int topPad = 36;
        int bottomPad = 24;
        int leftPad = 8;
        int rightPad = 8;
        int barGap = 10;
        int barW = (w - leftPad - rightPad - (days.length - 1) * barGap) / days.length;
        int chartH = h - topPad - bottomPad;
        double max = 100;

        // Title
        g2.setFont(Constants.FONT_SUBHEADER);
        g2.setColor(Constants.COLOR_TEXT);
        g2.drawString("Total income $120.9k", leftPad, 24);

        for (int i = 0; i < days.length; i++) {
            int x = leftPad + i * (barW + barGap);

            int baseH = (int) (baseValues[i] / max * chartH);
            int fillH = (int) (fillValues[i] / max * chartH);
            int baseY = h - bottomPad - baseH;
            int fillY = h - bottomPad - fillH;

            // Base bar
            g2.setColor(Constants.COLOR_CHART_BAR_BASE);
            g2.fill(new RoundRectangle2D.Float(x, baseY, barW, baseH, barW / 2f, barW / 2f));

            // Fill bar
            g2.setColor(Constants.COLOR_CHART_BAR_FILL);
            g2.fill(new RoundRectangle2D.Float(x, fillY, barW, fillH, barW / 2f, barW / 2f));

            // Day label
            g2.setFont(Constants.FONT_TINY);
            g2.setColor(Constants.COLOR_TEXT_SECONDARY);
            FontMetrics fm = g2.getFontMetrics();
            int tx = x + (barW - fm.stringWidth(days[i])) / 2;
            g2.drawString(days[i], tx, h - 6);
        }

        g2.dispose();
    }
}
