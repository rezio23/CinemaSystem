package com.cinema.ui.components;

import com.cinema.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

public class BarChartPanel extends JPanel {
    private final List<Map<String, Object>> data;
    private final String labelKey;
    private final String valueKey;
    private final String title;
    private final Color barColor;

    public BarChartPanel(List<Map<String, Object>> data, String labelKey, String valueKey, String title, Color barColor) {
        this.data = data;
        this.labelKey = labelKey;
        this.valueKey = valueKey;
        this.title = title;
        this.barColor = barColor;
        setBackground(Constants.COLOR_CARD);
        setPreferredSize(new Dimension(500, 300));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        int padding = 40;
        int top = 40;
        int bottom = 60;
        int left = 80;
        int right = 20;
        int chartWidth = getWidth() - left - right;
        int chartHeight = getHeight() - top - bottom;

        // Title
        g2.setFont(Constants.FONT_SUBHEADER);
        g2.setColor(Constants.COLOR_TEXT);
        FontMetrics fm = g2.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        g2.drawString(title, (getWidth() - titleWidth) / 2, 25);

        if (data == null || data.isEmpty()) {
            g2.setFont(Constants.FONT_BODY);
            g2.setColor(Constants.COLOR_TEXT_MUTED);
            g2.drawString("No data available", getWidth() / 2 - 50, getHeight() / 2);
            return;
        }

        double maxValue = 0;
        for (Map<String, Object> row : data) {
            Object val = row.get(valueKey);
            double d = 0;
            if (val instanceof BigDecimal) d = ((BigDecimal) val).doubleValue();
            else if (val instanceof Number) d = ((Number) val).doubleValue();
            maxValue = Math.max(maxValue, d);
        }
        if (maxValue == 0) maxValue = 1;

        int barWidth = Math.max(10, chartWidth / data.size() - 10);
        int gap = 10;

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(0);

        for (int i = 0; i < data.size(); i++) {
            Map<String, Object> row = data.get(i);
            String label = String.valueOf(row.get(labelKey));
            Object val = row.get(valueKey);
            double value = 0;
            if (val instanceof BigDecimal) value = ((BigDecimal) val).doubleValue();
            else if (val instanceof Number) value = ((Number) val).doubleValue();

            int barHeight = (int) ((value / maxValue) * chartHeight);
            int x = left + i * (barWidth + gap) + gap / 2;
            int y = getHeight() - bottom - barHeight;

            // Bar shadow for depth
            g2.setColor(new Color(0, 0, 0, 20));
            g2.fillRoundRect(x + 2, y + 2, barWidth, barHeight, 4, 4);

            // Bar
            g2.setColor(barColor);
            g2.fillRoundRect(x, y, barWidth, barHeight, 4, 4);
            g2.setColor(barColor.darker());
            g2.drawRoundRect(x, y, barWidth, barHeight, 4, 4);

            // Value label on top
            String valueStr = nf.format(value);
            g2.setFont(Constants.FONT_SMALL);
            FontMetrics vfm = g2.getFontMetrics();
            int vw = vfm.stringWidth(valueStr);
            int vh = vfm.getHeight();
            int vx = x + (barWidth - vw) / 2;
            int vy = y - 8;

            // Ensure label stays within chart bounds
            if (vy < top + vh) {
                vy = y + barHeight + vh;
            }

            // Draw contrasting background pill behind value
            int pillPadX = 4;
            int pillPadY = 2;
            int pillX = vx - pillPadX;
            int pillY = vy - vfm.getAscent() - pillPadY;
            int pillW = vw + pillPadX * 2;
            int pillH = vh + pillPadY * 2;
            g2.setColor(new Color(255, 255, 255, 220));
            g2.fillRoundRect(pillX, pillY, pillW, pillH, 6, 6);
            g2.setColor(Constants.COLOR_TEXT);
            g2.drawString(valueStr, vx, vy);

            // X label
            g2.setColor(Constants.COLOR_TEXT);
            int lw = g2.getFontMetrics().stringWidth(label);
            int lx = x + (barWidth - lw) / 2;
            int ly = getHeight() - bottom + 20;

            // Truncate long labels and add ellipsis
            String displayLabel = label;
            if (lw > barWidth + gap) {
                while (g2.getFontMetrics().stringWidth(displayLabel + "...") > barWidth + gap && displayLabel.length() > 0) {
                    displayLabel = displayLabel.substring(0, displayLabel.length() - 1);
                }
                displayLabel += "...";
                lw = g2.getFontMetrics().stringWidth(displayLabel);
                lx = x + (barWidth - lw) / 2;
            }
            g2.drawString(displayLabel, lx, ly);
        }

        // Y axis line
        g2.setColor(Constants.COLOR_TEXT_MUTED);
        g2.drawLine(left, top, left, getHeight() - bottom);
        g2.drawLine(left, getHeight() - bottom, getWidth() - right, getHeight() - bottom);

        // Y ticks with proper number formatting
        g2.setFont(Constants.FONT_SMALL);
        for (int i = 0; i <= 5; i++) {
            double val = maxValue * i / 5.0;
            int yTick = getHeight() - bottom - (int) ((val / maxValue) * chartHeight);
            String tick = nf.format(val);
            int tw = g2.getFontMetrics().stringWidth(tick);
            g2.drawString(tick, left - tw - 8, yTick + 4);

            // Light grid line
            if (i > 0) {
                g2.setColor(new Color(0xE0E6ED));
                g2.drawLine(left + 1, yTick, getWidth() - right, yTick);
                g2.setColor(Constants.COLOR_TEXT_MUTED);
            }
        }
    }
}
