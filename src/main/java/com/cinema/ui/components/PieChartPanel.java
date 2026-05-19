package com.cinema.ui.components;

import com.cinema.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

public class PieChartPanel extends JPanel {
    private final List<Map<String, Object>> data;
    private final String labelKey;
    private final String valueKey;
    private final String title;
    private final Color[] palette;

    private static final Color[] DEFAULT_PALETTE = {
        new Color(0x1A237E), new Color(0x3949AB), new Color(0x5C6BC0),
        new Color(0x0D1B2A), new Color(0x283593), new Color(0x7986CB),
        new Color(0x303F9F), new Color(0x1A237E), new Color(0x9FA8DA),
        new Color(0x3F51B5), new Color(0x536DFE), new Color(0xC5CAE9)
    };

    public PieChartPanel(List<Map<String, Object>> data, String labelKey, String valueKey, String title) {
        this(data, labelKey, valueKey, title, DEFAULT_PALETTE);
    }

    public PieChartPanel(List<Map<String, Object>> data, String labelKey, String valueKey, String title, Color[] palette) {
        this.data = data;
        this.labelKey = labelKey;
        this.valueKey = valueKey;
        this.title = title;
        this.palette = palette;
        setBackground(Constants.COLOR_CARD);
        setPreferredSize(new Dimension(700, 420));
        setMinimumSize(new Dimension(500, 350));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

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

        double total = 0;
        double[] values = new double[data.size()];
        for (int i = 0; i < data.size(); i++) {
            Object val = data.get(i).get(valueKey);
            double d = 0;
            if (val instanceof BigDecimal) d = ((BigDecimal) val).doubleValue();
            else if (val instanceof Number) d = ((Number) val).doubleValue();
            values[i] = d;
            total += d;
        }
        if (total == 0) total = 1;

        int diameter = Math.min(getWidth() - 220, getHeight() - 80);
        int cx = (getWidth() - 200) / 2 - diameter / 2 + 20;
        int cy = getHeight() / 2 - diameter / 2 + 10;

        double startAngle = 0;
        for (int i = 0; i < data.size(); i++) {
            double fraction = values[i] / total;
            double arcAngle = fraction * 360;
            Color color = palette[i % palette.length];

            // Shadow
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fill(new Arc2D.Double(cx + 3, cy + 3, diameter, diameter, startAngle, arcAngle, Arc2D.PIE));

            // Slice
            g2.setColor(color);
            g2.fill(new Arc2D.Double(cx, cy, diameter, diameter, startAngle, arcAngle, Arc2D.PIE));

            // Border
            g2.setColor(color.darker());
            g2.draw(new Arc2D.Double(cx, cy, diameter, diameter, startAngle, arcAngle, Arc2D.PIE));

            // Percentage label inside slice (only if slice is big enough)
            if (fraction > 0.05) {
                double midAngle = Math.toRadians(startAngle + arcAngle / 2);
                int labelRadius = diameter / 2 - 30;
                int lx = cx + diameter / 2 + (int) (Math.cos(midAngle) * labelRadius);
                int ly = cy + diameter / 2 - (int) (Math.sin(midAngle) * labelRadius);
                String pct = String.format("%.1f%%", fraction * 100);
                g2.setFont(Constants.FONT_SMALL);
                g2.setColor(Color.WHITE);
                FontMetrics mfm = g2.getFontMetrics();
                int mw = mfm.stringWidth(pct);
                g2.drawString(pct, lx - mw / 2, ly + mfm.getAscent() / 2 - 2);
            }

            startAngle += arcAngle;
        }

        // Center hole for donut effect
        g2.setColor(Constants.COLOR_CARD);
        int holeDiameter = diameter / 3;
        int holeX = cx + (diameter - holeDiameter) / 2;
        int holeY = cy + (diameter - holeDiameter) / 2;
        g2.fillOval(holeX, holeY, holeDiameter, holeDiameter);
        g2.setColor(Constants.COLOR_CARD.darker());
        g2.drawOval(holeX, holeY, holeDiameter, holeDiameter);

        // Total in center
        g2.setFont(Constants.FONT_BODY);
        g2.setColor(Constants.COLOR_TEXT);
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(0);
        String totalStr = nf.format(total);
        FontMetrics tfm = g2.getFontMetrics();
        int tw = tfm.stringWidth(totalStr);
        g2.drawString(totalStr, cx + diameter / 2 - tw / 2, cy + diameter / 2 + tfm.getAscent() / 2 - 2);

        // Legend on the right
        int legendX = getWidth() - 180;
        int legendY = 50;
        int legendItemHeight = 24;
        g2.setFont(Constants.FONT_SMALL);
        for (int i = 0; i < data.size(); i++) {
            String label = String.valueOf(data.get(i).get(labelKey));
            Color color = palette[i % palette.length];
            int y = legendY + i * legendItemHeight;

            g2.setColor(color);
            g2.fillRoundRect(legendX, y, 14, 14, 4, 4);
            g2.setColor(color.darker());
            g2.drawRoundRect(legendX, y, 14, 14, 4, 4);

            g2.setColor(Constants.COLOR_TEXT);
            String displayLabel = label;
            if (g2.getFontMetrics().stringWidth(displayLabel) > 150) {
                while (g2.getFontMetrics().stringWidth(displayLabel + "...") > 150 && displayLabel.length() > 0) {
                    displayLabel = displayLabel.substring(0, displayLabel.length() - 1);
                }
                displayLabel += "...";
            }
            g2.drawString(displayLabel, legendX + 22, y + 12);
        }
    }
}
