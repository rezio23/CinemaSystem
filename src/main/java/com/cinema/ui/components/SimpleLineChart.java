package com.cinema.ui.components;

import com.cinema.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

public class SimpleLineChart extends JPanel {
    private String[] labels = {"Jan", "Feb", "Mar", "Apr", "May", "Jun"};
    private double[] values = {0, 0, 0, 0, 0, 0};
    private double maxValue = 100;
    private String chartTitle = "Ticket Sales Trend";

    public SimpleLineChart() {
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
        int padLeft = 10;
        int padRight = 10;
        int chartH = h - padTop - padBottom;
        int chartW = w - padLeft - padRight;
        double max = maxValue > 0 ? maxValue : 1;

        g2.setFont(Constants.FONT_SUBHEADER);
        g2.setColor(Constants.COLOR_TEXT);
        g2.drawString(chartTitle, 20, 22);

        // Grid lines
        g2.setColor(Constants.COLOR_CHART_TRACK);
        g2.setStroke(new BasicStroke(1));
        for (int i = 0; i <= 4; i++) {
            int y = padTop + (int) (chartH * i / 4.0);
            g2.drawLine(padLeft, y, w - padRight, y);
        }

        if (values.length == 0) {
            g2.dispose();
            return;
        }

        // Line path
        Path2D path = new Path2D.Float();
        int step = values.length > 1 ? chartW / (values.length - 1) : 0;
        for (int i = 0; i < values.length; i++) {
            int px = padLeft + i * step;
            int py = padTop + chartH - (int) (values[i] / max * chartH);
            if (i == 0) path.moveTo(px, py);
            else path.lineTo(px, py);
        }

        // Fill area
        Path2D fill = new Path2D.Float(path);
        fill.lineTo(padLeft + (values.length - 1) * step, padTop + chartH);
        fill.lineTo(padLeft, padTop + chartH);
        fill.closePath();
        g2.setColor(new Color(0xFF6B00));
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
        g2.fill(fill);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        // Line
        g2.setColor(Constants.COLOR_PRIMARY);
        g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(path);

        // Points
        for (int i = 0; i < values.length; i++) {
            int px = padLeft + i * step;
            int py = padTop + chartH - (int) (values[i] / max * chartH);
            g2.setColor(Constants.COLOR_PRIMARY);
            g2.fillOval(px - 4, py - 4, 8, 8);
            g2.setColor(Constants.COLOR_CARD);
            g2.fillOval(px - 2, py - 2, 4, 4);
        }

        // Labels
        g2.setFont(Constants.FONT_TINY);
        g2.setColor(Constants.COLOR_TEXT_SECONDARY);
        for (int i = 0; i < labels.length; i++) {
            int px = padLeft + i * step;
            int tw = g2.getFontMetrics().stringWidth(labels[i]);
            g2.drawString(labels[i], px - tw / 2, h - 6);
        }

        g2.dispose();
    }
}
