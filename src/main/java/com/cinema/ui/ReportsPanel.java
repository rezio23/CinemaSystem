package com.cinema.ui;

import com.cinema.dao.*;
import com.cinema.model.MovieShow;
import com.cinema.ui.components.BarChartPanel;
import com.cinema.util.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ReportsPanel extends JPanel implements MainFrame.Refreshable {

    private final ReportDao reportDao = new ReportDao();
    private final BookingDao bookingDao = new BookingDao();
    private final BookingSeatDao bookingSeatDao = new BookingSeatDao();
    private final MovieShowDao showDao = new MovieShowDao();

    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    public ReportsPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Constants.COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);

        JLabel header = new JLabel("Reports & Analytics");
        header.setFont(Constants.FONT_HEADER);
        header.setForeground(Constants.COLOR_TEXT);
        north.add(header, BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);
        String[] views = {"Revenue per Movie", "Daily Revenue", "Weekly Revenue", "Monthly Revenue", "Occupancy", "Customer History", "Taken Seats"};
        JComboBox<String> viewCombo = new JComboBox<>(views);
        viewCombo.setFont(Constants.FONT_BODY);
        viewCombo.setBackground(Constants.COLOR_CARD_ELEVATED);
        viewCombo.addActionListener(e -> switchView((String) viewCombo.getSelectedItem()));
        toolbar.add(new JLabel("Report:"));
        toolbar.add(viewCombo);
        north.add(toolbar, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);

        contentPanel.add(createRevenueMoviePanel(), "Revenue per Movie");
        contentPanel.add(createDailyPanel(), "Daily Revenue");
        contentPanel.add(createWeeklyPanel(), "Weekly Revenue");
        contentPanel.add(createMonthlyPanel(), "Monthly Revenue");
        contentPanel.add(createOccupancyPanel(), "Occupancy");
        contentPanel.add(createCustomerHistoryPanel(), "Customer History");
        contentPanel.add(createTakenSeatsPanel(), "Taken Seats");

        add(contentPanel, BorderLayout.CENTER);
    }

    private void switchView(String view) {
        cardLayout.show(contentPanel, view);
        refreshData();
    }

    @Override
    public void refreshData() {
        // panels refresh lazily on view switch; can be enhanced
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(Constants.FONT_BODY);
        table.setRowHeight(40);
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(Constants.COLOR_PRIMARY);
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        return table;
    }

    private JScrollPane createScroll(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        return scroll;
    }

    private JPanel wrapInCard(JComponent comp, String title) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Constants.COLOR_CARD);
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        if (title != null && !title.isEmpty()) {
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(Constants.FONT_SUBHEADER);
            titleLabel.setForeground(Constants.COLOR_TEXT);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
            card.add(titleLabel, BorderLayout.NORTH);
        }
        card.add(comp, BorderLayout.CENTER);
        return card;
    }

    private JPanel createRevenueMoviePanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setOpaque(false);

        DefaultTableModel model = new DefaultTableModel(new String[]{"Movie", "Total Revenue", "Tickets Sold"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = createStyledTable(model);
        JPanel tableCard = wrapInCard(createScroll(table), "Revenue per Movie");
        panel.add(tableCard, BorderLayout.NORTH);

        JPanel chartHolder = new JPanel(new BorderLayout());
        chartHolder.setOpaque(false);
        panel.add(chartHolder, BorderLayout.CENTER);

        JButton loadBtn = new JButton("Load Data");
        loadBtn.setFont(Constants.FONT_BODY);
        loadBtn.addActionListener(e -> {
            List<Map<String, Object>> data = reportDao.revenuePerMovie();
            model.setRowCount(0);
            for (Map<String, Object> row : data) {
                model.addRow(new Object[]{row.get("title"), "$" + row.get("totalRevenue"), row.get("ticketsSold")});
            }
            chartHolder.removeAll();
            chartHolder.add(new BarChartPanel(data, "title", "totalRevenue", "Revenue per Movie", Constants.COLOR_PRIMARY), BorderLayout.CENTER);
            chartHolder.revalidate();
            chartHolder.repaint();
        });
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnWrap.setOpaque(false);
        btnWrap.add(loadBtn);
        panel.add(btnWrap, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createDailyPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setOpaque(false);
        DefaultTableModel model = new DefaultTableModel(new String[]{"Day", "Bookings", "Revenue"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = createStyledTable(model);
        panel.add(wrapInCard(createScroll(table), "Daily Revenue"), BorderLayout.CENTER);
        JButton loadBtn = new JButton("Load Data");
        loadBtn.setFont(Constants.FONT_BODY);
        loadBtn.addActionListener(e -> {
            List<Map<String, Object>> data = reportDao.dailyRevenueAnalysis();
            model.setRowCount(0);
            for (Map<String, Object> row : data) {
                model.addRow(new Object[]{row.get("day"), row.get("bookings"), "$" + row.get("revenue")});
            }
        });
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnWrap.setOpaque(false);
        btnWrap.add(loadBtn);
        panel.add(btnWrap, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createWeeklyPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setOpaque(false);
        DefaultTableModel model = new DefaultTableModel(new String[]{"Week Start", "Bookings", "Revenue"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = createStyledTable(model);
        panel.add(wrapInCard(createScroll(table), "Weekly Revenue"), BorderLayout.CENTER);
        JButton loadBtn = new JButton("Load Data");
        loadBtn.setFont(Constants.FONT_BODY);
        loadBtn.addActionListener(e -> {
            List<Map<String, Object>> data = reportDao.weeklyRevenueAnalysis();
            model.setRowCount(0);
            for (Map<String, Object> row : data) {
                model.addRow(new Object[]{row.get("weekStart"), row.get("bookings"), "$" + row.get("revenue")});
            }
        });
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnWrap.setOpaque(false);
        btnWrap.add(loadBtn);
        panel.add(btnWrap, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createMonthlyPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setOpaque(false);
        DefaultTableModel model = new DefaultTableModel(new String[]{"Month", "Bookings", "Revenue"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = createStyledTable(model);
        panel.add(wrapInCard(createScroll(table), "Monthly Revenue"), BorderLayout.CENTER);
        JButton loadBtn = new JButton("Load Data");
        loadBtn.setFont(Constants.FONT_BODY);
        loadBtn.addActionListener(e -> {
            List<Map<String, Object>> data = reportDao.monthlyRevenueAnalysis();
            model.setRowCount(0);
            for (Map<String, Object> row : data) {
                model.addRow(new Object[]{row.get("month"), row.get("bookings"), "$" + row.get("revenue")});
            }
        });
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnWrap.setOpaque(false);
        btnWrap.add(loadBtn);
        panel.add(btnWrap, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createOccupancyPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setOpaque(false);
        DefaultTableModel model = new DefaultTableModel(new String[]{"Show", "Hall", "Seats Sold", "Capacity", "Occupancy %"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = createStyledTable(model);
        panel.add(wrapInCard(createScroll(table), "Occupancy per Show"), BorderLayout.CENTER);
        JButton loadBtn = new JButton("Load Data");
        loadBtn.setFont(Constants.FONT_BODY);
        loadBtn.addActionListener(e -> {
            List<Map<String, Object>> data = reportDao.seatsSoldPerShow();
            model.setRowCount(0);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (Map<String, Object> row : data) {
                Object dt = row.get("showDateTime");
                model.addRow(new Object[]{
                        row.get("movieTitle") + " @ " + (dt != null ? ((java.time.LocalDateTime) dt).format(dtf) : ""),
                        row.get("hallName"), row.get("seatsSold"), row.get("capacity"),
                        row.get("occupancyRate") + "%"
                });
            }
        });
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnWrap.setOpaque(false);
        btnWrap.add(loadBtn);
        panel.add(btnWrap, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createCustomerHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.setOpaque(false);
        JTextField custIdField = new JTextField(10);
        custIdField.setBackground(Constants.COLOR_CARD_ELEVATED);
        top.add(new JLabel("Customer ID:"), BorderLayout.WEST);
        top.add(custIdField, BorderLayout.CENTER);

        DefaultTableModel model = new DefaultTableModel(new String[]{"Booking ID", "Movie", "Hall", "Show Time", "Seats", "Total", "Payment", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = createStyledTable(model);
        panel.add(wrapInCard(createScroll(table), "Customer History"), BorderLayout.CENTER);

        JButton loadBtn = new JButton("Load History");
        loadBtn.setFont(Constants.FONT_BODY);
        loadBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(custIdField.getText().trim());
                List<Map<String, Object>> data = reportDao.customerBookingHistory(id);
                model.setRowCount(0);
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                for (Map<String, Object> row : data) {
                    model.addRow(new Object[]{
                            row.get("bookingId"), row.get("movieTitle"), row.get("hallName"),
                            row.get("showDateTime") != null ? ((java.time.LocalDateTime) row.get("showDateTime")).format(dtf) : "",
                            row.get("seats"), "$" + row.get("totalAmount"), row.get("paymentMethod"), row.get("status")
                    });
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Enter a valid Customer ID.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        top.add(loadBtn, BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createTakenSeatsPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.setOpaque(false);
        JTextField showIdField = new JTextField(10);
        showIdField.setBackground(Constants.COLOR_CARD_ELEVATED);
        top.add(new JLabel("Show ID:"), BorderLayout.WEST);
        top.add(showIdField, BorderLayout.CENTER);
        JButton loadBtn = new JButton("Load Seats");
        loadBtn.setFont(Constants.FONT_BODY);
        top.add(loadBtn, BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        JPanel seatArea = new JPanel(new BorderLayout());
        seatArea.setOpaque(false);
        panel.add(seatArea, BorderLayout.CENTER);

        loadBtn.addActionListener(e -> {
            try {
                int showId = Integer.parseInt(showIdField.getText().trim());
                MovieShow show = showDao.getById(showId);
                if (show == null) {
                    JOptionPane.showMessageDialog(panel, "Show not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                java.util.List<String> taken = bookingSeatDao.getTakenSeatNumbersByShow(showId);
                SeatSelectionPanel sp = new SeatSelectionPanel(show.getHallCapacity(), taken, s -> {});
                seatArea.removeAll();
                seatArea.add(sp, BorderLayout.CENTER);
                seatArea.revalidate();
                seatArea.repaint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Enter a valid Show ID.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        return panel;
    }
}
