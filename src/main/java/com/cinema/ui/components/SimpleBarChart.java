package com.cinema.ui.components;

import com.cinema.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class SimpleBarChart extends JPanel {
    private String[] labels = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    private double[] values = {0, 0, 0, 0, 0, 0, 0};
    private double maxValue = 100;
    private String chartTitle = "Weekly Revenue";

    public SimpleBarChart() {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(40, 20, 30, 20));
        setPreferredSize(new Dimension(400, 220));
        setMinimumSize(new Dimension(300, 180));
    }

    public void setData(String[] labels, double[] values, String title) {
        this.labels = labels != null ? labels : this.labels;
        this.values = values != null ? values : this.values;
        this.chartTitle = title != null ? title : this.chartTitle;
        this.maxValue = 1;
        for (double v : this.values) {
            if (v > maxValue) maxValue = v;
        }
        maxValue = Math.ceil(maxValue * 1.1);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int padTop = 30;
        int padBottom = 24;
        int barGap = labels.length > 0 ? (w - 40) / labels.length / 4 : 14;
        int barW = labels.length > 0 ? (w - 40 - (labels.length - 1) * barGap) / labels.length : 30;
        int chartH = h - padTop - padBottom;
        double max = maxValue > 0 ? maxValue : 1;

        g2.setFont(Constants.FONT_SUBHEADER);
        g2.setColor(Constants.COLOR_TEXT);
        g2.drawString(chartTitle, 20, 22);

        for (int i = 0; i < labels.length; i++) {
            int x = 20 + i * (barW + barGap);
            int barH = (int) (values[i] / max * chartH);
            int y = h - padBottom - barH;

            g2.setColor(Constants.COLOR_CHART_BAR_BASE);
            g2.fill(new RoundRectangle2D.Float(x, h - padBottom - chartH, barW, chartH, barW / 2f, barW / 2f));

            g2.setColor(Constants.COLOR_CHART_BAR_FILL);
            g2.fill(new RoundRectangle2D.Float(x, y, barW, barH, barW / 2f, barW / 2f));

            g2.setFont(Constants.FONT_TINY);
            g2.setColor(Constants.COLOR_TEXT_SECONDARY);
            int tw = g2.getFontMetrics().stringWidth(labels[i]);
            g2.drawString(labels[i], x + (barW - tw) / 2, h - 6);
        }

        g2.dispose();
    }
}
