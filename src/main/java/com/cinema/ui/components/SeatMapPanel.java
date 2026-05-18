package com.cinema.ui.components;

import com.cinema.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Random;

public class SeatMapPanel extends RoundedPanel {

    private static final int ROWS = 8;
    private static final int COLS = 12;
    private final boolean[][] booked;

    public SeatMapPanel() {
        setBackground(Constants.COLOR_PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setArc(16);

        booked = new boolean[ROWS][COLS];
        Random r = new Random(42);
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                booked[i][j] = r.nextDouble() < 0.35;
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
        int seatSize = 10;
        int gap = 5;

        int gridW = COLS * (seatSize + gap) - gap;
        int gridH = ROWS * (seatSize + gap) - gap;
        int startX = (w - gridW) / 2;
        int startY = (h - gridH) / 2 + 8;

        // Curved screen line
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(255, 255, 255, 200));
        Path2D screen = new Path2D.Float();
        int cx = w / 2;
        int cy = startY - 18;
        int rw = gridW + 16;
        screen.moveTo(cx - rw / 2f, cy);
        screen.quadTo(cx, cy - 24, cx + rw / 2f, cy);
        g2.draw(screen);

        g2.setFont(Constants.FONT_SMALL);
        g2.setColor(new Color(255, 255, 255, 160));
        String screenText = "SCREEN";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(screenText, cx - fm.stringWidth(screenText) / 2, cy - 6);

        // Seats
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                int x = startX + c * (seatSize + gap);
                int y = startY + r * (seatSize + gap);
                boolean isBooked = booked[r][c];
                g2.setColor(isBooked ? Constants.COLOR_SEAT_BOOKED : Constants.COLOR_SEAT_AVAILABLE);
                g2.fill(new RoundRectangle2D.Float(x, y, seatSize, seatSize, 2, 2));
            }
        }

        g2.dispose();
    }
}
