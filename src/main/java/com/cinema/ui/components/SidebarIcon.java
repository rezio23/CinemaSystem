package com.cinema.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

public class SidebarIcon implements Icon {
    private final Type type;
    private Color color = Color.WHITE;

    public enum Type {
        DASHBOARD, TICKETS, MOVIES, HALLS, STAFF, CUSTOMERS, SHOWS, REPORTS
    }

    public SidebarIcon(Type type) {
        this.type = type;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public int getIconWidth() {
        return 20;
    }

    @Override
    public int getIconHeight() {
        return 20;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(x, y);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);

        switch (type) {
            case DASHBOARD -> paintDashboard(g2);
            case TICKETS -> paintTickets(g2);
            case MOVIES -> paintMovies(g2);
            case HALLS -> paintHalls(g2);
            case STAFF -> paintStaff(g2);
            case CUSTOMERS -> paintCustomers(g2);
            case SHOWS -> paintShows(g2);
            case REPORTS -> paintReports(g2);
        }
        g2.dispose();
    }

    // 2x2 filled grid — apps/dashboard metaphor
    private void paintDashboard(Graphics2D g2) {
        int s = 8, gap = 3, arc = 2;
        g2.fillRoundRect(0, 0, s, s, arc, arc);
        g2.fillRoundRect(s + gap, 0, s, s, arc, arc);
        g2.fillRoundRect(0, s + gap, s, s, arc, arc);
        g2.fillRoundRect(s + gap, s + gap, s, s, arc, arc);
    }

    // Two ticket stubs with a perforation gap
    private void paintTickets(Graphics2D g2) {
        g2.fillRoundRect(0, 4, 8, 12, 2, 2);
        g2.fillRoundRect(11, 4, 8, 12, 2, 2);
        // stub detail lines
        g2.fillRect(2, 9, 4, 2);
        g2.fillRect(13, 9, 4, 2);
    }

    // Clapperboard: angled top bar + body
    private void paintMovies(Graphics2D g2) {
        // Body
        g2.fillRoundRect(0, 6, 20, 14, 2, 2);
        // Angled clapper
        Path2D p = new Path2D.Float();
        p.moveTo(0, 0);
        p.lineTo(20, 0);
        p.lineTo(16, 6);
        p.lineTo(0, 6);
        p.closePath();
        g2.fill(p);
    }

    // Theater arch with a stage line
    private void paintHalls(Graphics2D g2) {
        Path2D p = new Path2D.Float();
        p.moveTo(2, 18);
        p.lineTo(2, 10);
        p.quadTo(2, 2, 10, 2);
        p.quadTo(18, 2, 18, 10);
        p.lineTo(18, 18);
        p.closePath();
        g2.fill(p);
        // stage floor
        g2.drawLine(2, 14, 18, 14);
    }

    // Single person silhouette
    private void paintStaff(Graphics2D g2) {
        g2.fillOval(6, 2, 8, 8); // head
        Path2D p = new Path2D.Float();
        p.moveTo(2, 18);
        p.quadTo(10, 10, 18, 18);
        p.closePath();
        g2.fill(p); // shoulders
    }

    // Two overlapping person silhouettes
    private void paintCustomers(Graphics2D g2) {
        // Front person (larger, left)
        g2.fillOval(4, 5, 6, 6);
        Path2D p1 = new Path2D.Float();
        p1.moveTo(2, 18);
        p1.quadTo(7, 11, 12, 18);
        p1.closePath();
        g2.fill(p1);

        // Back person (smaller, right)
        g2.fillOval(11, 7, 5, 5);
        Path2D p2 = new Path2D.Float();
        p2.moveTo(9, 18);
        p2.quadTo(13.5f, 12, 18, 18);
        p2.closePath();
        g2.fill(p2);
    }

    // Calendar page with rings
    private void paintShows(Graphics2D g2) {
        // Page
        g2.fillRoundRect(2, 5, 16, 15, 2, 2);
        // Rings
        g2.fillRoundRect(6, 1, 3, 5, 1, 1);
        g2.fillRoundRect(11, 1, 3, 5, 1, 1);
        // Grid line
        g2.drawLine(2, 12, 18, 12);
    }

    // Three vertical bars of varying heights
    private void paintReports(Graphics2D g2) {
        g2.fillRoundRect(2, 11, 4, 9, 2, 2);
        g2.fillRoundRect(8, 6, 4, 14, 2, 2);
        g2.fillRoundRect(14, 9, 4, 11, 2, 2);
    }
}
