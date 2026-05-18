package com.cinema.ui;

import com.cinema.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SeatSelectionPanel extends JPanel {

    private final int capacity;
    private final List<String> takenSeats;
    private final Set<String> selectedSeats = new HashSet<>();
    private final java.util.function.Consumer<Set<String>> onSelectionChanged;

    public SeatSelectionPanel(int capacity, List<String> takenSeats,
                               java.util.function.Consumer<Set<String>> onSelectionChanged) {
        this.capacity = capacity;
        this.takenSeats = takenSeats != null ? takenSeats : Collections.emptyList();
        this.onSelectionChanged = onSelectionChanged;

        int cols = 10;
        int rows = (int) Math.ceil(capacity / (double) cols);

        setLayout(new GridLayout(rows, cols, 6, 6));
        setBackground(Constants.COLOR_BACKGROUND);
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
        btn.setPreferredSize(new Dimension(55, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (taken) {
            btn.setEnabled(false);
            btn.setBackground(new Color(149, 165, 166));
            btn.setForeground(Color.WHITE);
            btn.setToolTipText("Already booked");
        } else {
            btn.setBackground(Constants.COLOR_SUCCESS);
            btn.setForeground(Color.WHITE);
            btn.addActionListener(e -> {
                if (selectedSeats.contains(seatNum)) {
                    selectedSeats.remove(seatNum);
                    btn.setBackground(Constants.COLOR_SUCCESS);
                } else {
                    selectedSeats.add(seatNum);
                    btn.setBackground(Constants.COLOR_WARNING);
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
                c.setBackground(Constants.COLOR_SUCCESS);
            }
        }
        onSelectionChanged.accept(selectedSeats);
    }
}
