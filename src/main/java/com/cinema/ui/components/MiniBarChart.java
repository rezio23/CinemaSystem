package com.cinema.ui.components;

import com.cinema.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class MiniBarChart extends RoundedPanel {

    private final int[] values = {40, 55, 35, 60, 45, 70, 80};
    private final int highlightIndex = 6;

    public MiniBarChart(String title, String subtitle) {
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
        int topPad = 52;
        int bottomPad = 8;
        int barGap = 4;
        int barW = (w - 32 - (values.length - 1) * barGap) / values.length;
        int chartH = h - topPad - bottomPad;
        int max = 100;

        // Title text
        g2.setFont(Constants.FONT_SUBHEADER);
        g2.setColor(Constants.COLOR_TEXT);
        g2.drawString("Viewers", 16, 24);

        g2.setFont(Constants.FONT_BODY);
        g2.setColor(Constants.COLOR_PRIMARY);
        g2.drawString("8k today", 16, 44);

        for (int i = 0; i < values.length; i++) {
            int x = 16 + i * (barW + barGap);
            int barH = (int) (values[i] / (double) max * chartH);
            int y = h - bottomPad - barH;

            g2.setColor(i == highlightIndex ? Constants.COLOR_PRIMARY : Constants.COLOR_CHART_TRACK);
            g2.fill(new RoundRectangle2D.Float(x, y, barW, barH, barW / 2f, barW / 2f));
        }

        g2.dispose();
    }
}
