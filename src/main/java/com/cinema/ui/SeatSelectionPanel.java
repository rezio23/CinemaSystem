package com.cinema.ui;

import com.cinema.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class SeatSelectionPanel extends JPanel {

    private final int capacity;
    private final List<String> takenSeats;
    private final Set<String> selectedSeats = new HashSet<>();
    private final java.util.function.Consumer<Set<String>> onSelectionChanged;

    // Light-theme seat colors
    private static final Color COLOR_AVAILABLE = new Color(0xE0E6ED);
    private static final Color COLOR_SELECTED = new Color(0x1A237E);
    private static final Color COLOR_TAKEN = new Color(0x0D1B2A);

    public SeatSelectionPanel(int capacity, List<String> takenSeats,
                               java.util.function.Consumer<Set<String>> onSelectionChanged) {
        this.capacity = capacity;
        this.takenSeats = takenSeats != null ? takenSeats : Collections.emptyList();
        this.onSelectionChanged = onSelectionChanged;

        int cols = 10;
        int rows = (int) Math.ceil(capacity / (double) cols);

        setLayout(new GridLayout(rows, cols, 6, 6));
        setBackground(Constants.COLOR_CARD);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int i = 1; i <= capacity; i++) {
            String seatNum = seatNumberFromIndex(i);
            boolean taken = this.takenSeats.contains(seatNum);
            JButton btn = createSeatButton(seatNum, taken);
            add(btn);
        }
    }

    private String seatNumberFromIndex(int index) {
        int row = (index - 1) / 10;
        int col = (index - 1) % 10 + 1;
        return String.valueOf((char) ('A' + row)) + col;
    }

    private JButton createSeatButton(String seatNum, boolean taken) {
        JButton btn = new JButton(seatNum);
        btn.setFont(Constants.FONT_SMALL);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(52, 42));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder());

        if (taken) {
            btn.setEnabled(false);
            btn.setBackground(COLOR_TAKEN);
            btn.setForeground(new Color(0x78909C));
            btn.setToolTipText("Already booked");
        } else {
            btn.setBackground(COLOR_AVAILABLE);
            btn.setForeground(Constants.COLOR_TEXT);
            btn.addActionListener(e -> {
                if (selectedSeats.contains(seatNum)) {
                    selectedSeats.remove(seatNum);
                    btn.setBackground(COLOR_AVAILABLE);
                    btn.setForeground(Constants.COLOR_TEXT);
                } else {
                    selectedSeats.add(seatNum);
                    btn.setBackground(COLOR_SELECTED);
                    btn.setForeground(Color.WHITE);
                }
                onSelectionChanged.accept(selectedSeats);
            });
        }
        return btn;
    }

    public Set<String> getSelectedSeats() {
        return new HashSet<>(selectedSeats);
    }

    public void clearSelection() {
        selectedSeats.clear();
        for (Component c : getComponents()) {
            if (c instanceof JButton && c.isEnabled()) {
                c.setBackground(COLOR_AVAILABLE);
                ((JButton) c).setForeground(Constants.COLOR_TEXT);
            }
        }
        onSelectionChanged.accept(selectedSeats);
    }
}
