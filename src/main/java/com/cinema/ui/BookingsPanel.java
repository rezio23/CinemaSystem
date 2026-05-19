package com.cinema.ui;

import com.cinema.dao.BookingDao;
import com.cinema.model.Booking;
import com.cinema.ui.components.StyledButton;
import com.cinema.util.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BookingsPanel extends JPanel implements MainFrame.Refreshable {

    private final BookingDao dao = new BookingDao();
    private final DefaultTableModel model;
    private final JTable table;
    private List<Booking> allBookings;
    private String currentFilter = "ALL";

    private static final String[] STATUSES = {"CONFIRMED", "PENDING", "CANCELLED"};

    public BookingsPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Constants.COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);

        JLabel header = new JLabel("Booking Management");
        header.setFont(Constants.FONT_HEADER);
        header.setForeground(Constants.COLOR_TEXT);
        north.add(header, BorderLayout.NORTH);

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        // Filter buttons
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterPanel.setOpaque(false);

        StyledButton allBtn = new StyledButton("All", StyledButton.Variant.PRIMARY);
        StyledButton pendingBtn = new StyledButton("Pending", StyledButton.Variant.SECONDARY);
        StyledButton confirmedBtn = new StyledButton("Confirmed", StyledButton.Variant.SECONDARY);
        StyledButton cancelledBtn = new StyledButton("Cancelled", StyledButton.Variant.SECONDARY);

        allBtn.addActionListener(e -> applyFilter("ALL", allBtn, pendingBtn, confirmedBtn, cancelledBtn));
        pendingBtn.addActionListener(e -> applyFilter("PENDING", pendingBtn, allBtn, confirmedBtn, cancelledBtn));
        confirmedBtn.addActionListener(e -> applyFilter("CONFIRMED", confirmedBtn, allBtn, pendingBtn, cancelledBtn));
        cancelledBtn.addActionListener(e -> applyFilter("CANCELLED", cancelledBtn, allBtn, pendingBtn, confirmedBtn));

        filterPanel.add(allBtn);
        filterPanel.add(pendingBtn);
        filterPanel.add(confirmedBtn);
        filterPanel.add(cancelledBtn);
        topRow.add(filterPanel, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionPanel.setOpaque(false);

        StyledButton refreshBtn = new StyledButton("Refresh", StyledButton.Variant.SECONDARY);
        refreshBtn.addActionListener(e -> refreshData());

        StyledButton statusBtn = new StyledButton("Change Status", StyledButton.Variant.PRIMARY);
        statusBtn.addActionListener(e -> changeStatus());

        actionPanel.add(refreshBtn);
        actionPanel.add(statusBtn);
        topRow.add(actionPanel, BorderLayout.EAST);

        north.add(topRow, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{
                "ID", "Customer", "Movie", "Hall", "Show Time", "Total", "Payment", "Status"
        }, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setFont(Constants.FONT_BODY);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(Constants.COLOR_PRIMARY);
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);

        refreshData();
    }

    private void applyFilter(String status, StyledButton active, StyledButton... others) {
        currentFilter = status;
        // Visual feedback: active = PRIMARY, others = SECONDARY
        // Re-create buttons is easier than managing state, but let's just reload
        refreshData();
    }

    @Override
    public void refreshData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override protected Void doInBackground() {
                allBookings = dao.getAll();
                loadTable();
                return null;
            }
        };
        worker.execute();
    }

    private void loadTable() {
        SwingUtilities.invokeLater(() -> {
            model.setRowCount(0);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (Booking b : allBookings) {
                if (!currentFilter.equals("ALL") && !currentFilter.equals(b.getStatus())) {
                    continue;
                }
                model.addRow(new Object[]{
                        b.getBookingId(),
                        b.getCustomerName(),
                        b.getMovieTitle(),
                        b.getHallName(),
                        b.getShowDateTime() != null ? b.getShowDateTime().format(dtf) : "",
                        "$" + b.getTotalAmount(),
                        b.getPaymentMethod(),
                        b.getStatus()
                });
            }
        });
    }

    private void changeStatus() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int bookingId = (int) model.getValueAt(row, 0);
        String currentStatus = (String) model.getValueAt(row, 7);

        JComboBox<String> combo = new JComboBox<>(STATUSES);
        combo.setSelectedItem(currentStatus);
        combo.setFont(Constants.FONT_BODY);

        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);
        panel.add(new JLabel("New status:"), BorderLayout.WEST);
        panel.add(combo, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Change Booking #" + bookingId + " Status",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String newStatus = (String) combo.getSelectedItem();
            try {
                dao.updateStatus(bookingId, newStatus);
                refreshData();
                JOptionPane.showMessageDialog(this,
                        "Booking #" + bookingId + " updated to " + newStatus + ".",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
