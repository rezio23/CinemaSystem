package com.cinema.ui;

import com.cinema.dao.*;
import com.cinema.model.Booking;
import com.cinema.ui.components.*;
import com.cinema.util.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class DashboardPanel extends JPanel implements MainFrame.Refreshable {

    private final CustomerDao customerDao = new CustomerDao();
    private final MovieDao movieDao = new MovieDao();
    private final HallDao hallDao = new HallDao();
    private final StaffDao staffDao = new StaffDao();
    private final MovieShowDao showDao = new MovieShowDao();
    private final BookingDao bookingDao = new BookingDao();
    private final ReportDao reportDao = new ReportDao();

    private JLabel statMovies, statBookings, statRevenue, statOccupancy;
    private SimpleBarChart barChart;
    private SimpleLineChart lineChart;
    private SimpleDoughnutChart doughnutChart;
    private DefaultTableModel recentTableModel;

    public DashboardPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(Constants.COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Dashboard Overview");
        header.setFont(Constants.FONT_HEADER);
        header.setForeground(Constants.COLOR_TEXT);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(16, 16));
        content.setOpaque(false);

        // Stat cards row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 12, 0));
        statsRow.setOpaque(false);
        statsRow.setPreferredSize(new Dimension(0, 100));

        statMovies = createStatLabel("0");
        statsRow.add(wrapStatCard("Total Movies", statMovies, Constants.COLOR_PRIMARY, ""));

        statBookings = createStatLabel("0");
        statsRow.add(wrapStatCard("Bookings", statBookings, Constants.COLOR_SUCCESS, ""));

        statRevenue = createStatLabel("$0");
        statsRow.add(wrapStatCard("Revenue Today", statRevenue, Constants.COLOR_WARNING, ""));

        statOccupancy = createStatLabel("0%");
        statsRow.add(wrapStatCard("Occupancy", statOccupancy, new Color(0x8B5CF6), ""));

        content.add(statsRow, BorderLayout.NORTH);

        // Charts area
        JPanel chartsArea = new JPanel(new BorderLayout(12, 12));
        chartsArea.setOpaque(false);

        // Left: stacked charts
        JPanel leftCharts = new JPanel(new GridLayout(2, 1, 12, 12));
        leftCharts.setOpaque(false);
        leftCharts.setPreferredSize(new Dimension(420, 0));

        barChart = new SimpleBarChart();
        barChart.setBackground(Constants.COLOR_CARD);
        leftCharts.add(wrapChart(barChart));

        lineChart = new SimpleLineChart();
        lineChart.setBackground(Constants.COLOR_CARD);
        leftCharts.add(wrapChart(lineChart));

        chartsArea.add(leftCharts, BorderLayout.WEST);

        // Center: recent bookings table
        JPanel tablePanel = createRecentTable();
        tablePanel.setPreferredSize(new Dimension(360, 0));
        chartsArea.add(tablePanel, BorderLayout.CENTER);

        // Right: doughnut chart
        JPanel rightChart = new JPanel(new BorderLayout());
        rightChart.setOpaque(false);
        rightChart.setPreferredSize(new Dimension(300, 0));

        doughnutChart = new SimpleDoughnutChart();
        rightChart.add(wrapChart(doughnutChart), BorderLayout.CENTER);

        chartsArea.add(rightChart, BorderLayout.EAST);

        content.add(chartsArea, BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);

        refreshData();
    }

    private JLabel createStatLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lbl.setForeground(Constants.COLOR_TEXT);
        return lbl;
    }

    private JPanel wrapStatCard(String label, JLabel valueLabel, Color accent, String trend) {
        JPanel card = new JPanel(new BorderLayout(4, 4));
        card.setBackground(Constants.COLOR_CARD);
        card.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(valueLabel, BorderLayout.NORTH);

        JLabel lbl = new JLabel(label);
        lbl.setFont(Constants.FONT_SMALL);
        lbl.setForeground(Constants.COLOR_TEXT_MUTED);
        top.add(lbl, BorderLayout.SOUTH);
        card.add(top, BorderLayout.CENTER);

        if (trend != null && !trend.isEmpty()) {
            JLabel t = new JLabel(trend);
            t.setFont(Constants.FONT_TINY);
            t.setForeground(accent);
            card.add(t, BorderLayout.SOUTH);
        }

        JPanel bar = new JPanel();
        bar.setBackground(accent);
        bar.setPreferredSize(new Dimension(0, 3));
        card.add(bar, BorderLayout.NORTH);

        return card;
    }

    private JPanel wrapChart(JPanel chart) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Constants.COLOR_CARD);
        wrapper.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        wrapper.add(chart, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createRecentTable() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Constants.COLOR_CARD);
        card.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JLabel title = new JLabel("Recent Bookings");
        title.setFont(Constants.FONT_SUBHEADER);
        title.setForeground(Constants.COLOR_TEXT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        card.add(title, BorderLayout.NORTH);

        recentTableModel = new DefaultTableModel(
            new String[]{"ID", "Customer", "Movie", "Total", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(recentTableModel);
        table.setFont(Constants.FONT_BODY);
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(Constants.COLOR_PRIMARY);
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(Constants.COLOR_CARD);
        table.getTableHeader().setForeground(Constants.COLOR_TEXT_MUTED);
        table.getTableHeader().setPreferredSize(new Dimension(0, 32));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    @Override
    public void refreshData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    // Stats
                    int movies = movieDao.countAll();
                    int bookings = bookingDao.countConfirmed();
                    double revenue = bookingDao.getRevenueToday();
                    double occupancy = reportDao.getOverallOccupancyRate();

                    // Weekly revenue bar chart
                    Map<String, Object> weekData = buildWeeklyRevenue();
                    String[] weekLabels = (String[]) weekData.get("labels");
                    double[] weekValues = (double[]) weekData.get("values");

                    // Monthly sales line chart
                    Map<String, Object> monthData = buildMonthlySales();
                    String[] monthLabels = (String[]) monthData.get("labels");
                    double[] monthValues = (double[]) monthData.get("values");

                    // Booking status doughnut
                    Map<String, Object> statusData = buildStatusData();

                    // Recent bookings
                    List<Booking> recent = bookingDao.getAll();

                    SwingUtilities.invokeLater(() -> {
                        statMovies.setText(String.valueOf(movies));
                        statBookings.setText(String.valueOf(bookings));
                        statRevenue.setText(String.format("$%.2f", revenue));
                        statOccupancy.setText(String.format("%.1f%%", occupancy));

                        barChart.setData(weekLabels, weekValues, "Weekly Revenue");
                        lineChart.setData(monthLabels, monthValues, "Ticket Sales Trend");

                        doughnutChart.setData(
                            (double[]) statusData.get("values"),
                            (String[]) statusData.get("labels"),
                            (Color[]) statusData.get("colors"),
                            (String) statusData.get("total"),
                            "Total",
                            "Booking Status"
                        );

                        recentTableModel.setRowCount(0);
                        int limit = Math.min(recent.size(), 5);
                        for (int i = 0; i < limit; i++) {
                            Booking b = recent.get(i);
                            recentTableModel.addRow(new Object[]{
                                "#" + b.getBookingId(),
                                b.getCustomerName(),
                                b.getMovieTitle(),
                                "$" + b.getTotalAmount(),
                                b.getStatus()
                            });
                        }
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(
                            DashboardPanel.this,
                            "Database error: " + ex.getMessage() + "\n\nPlease verify Oracle is running and tables exist.",
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                    });
                    ex.printStackTrace();
                }
                return null;
            }
        };
        worker.execute();
    }

    private Map<String, Object> buildWeeklyRevenue() {
        List<Map<String, Object>> daily = reportDao.dailyRevenueAnalysis();
        String[] labels = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        double[] values = new double[7];
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1); // Monday

        for (Map<String, Object> row : daily) {
            Date day = (Date) row.get("day");
            if (day == null) continue;
            LocalDate d = day.toLocalDate();
            int dayOfWeek = d.getDayOfWeek().getValue(); // 1=Mon ... 7=Sun
            int idx = dayOfWeek - 1; // 0=Mon ... 6=Sun
            if (idx >= 0 && idx < 7) {
                Object rev = row.get("revenue");
                double val = rev instanceof BigDecimal ? ((BigDecimal) rev).doubleValue() : 0;
                values[idx] += val;
            }
        }

        return Map.of("labels", labels, "values", values);
    }

    private Map<String, Object> buildMonthlySales() {
        List<Map<String, Object>> monthly = reportDao.monthlyRevenueAnalysis();
        String[] labels = new String[6];
        double[] values = new double[6];
        LocalDate now = LocalDate.now();

        for (int i = 0; i < 6; i++) {
            LocalDate m = now.minusMonths(5 - i);
            labels[i] = m.format(DateTimeFormatter.ofPattern("MMM"));
            String key = m.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            for (Map<String, Object> row : monthly) {
                if (key.equals(row.get("month"))) {
                    Object rev = row.get("revenue");
                    values[i] = rev instanceof BigDecimal ? ((BigDecimal) rev).doubleValue() : 0;
                    break;
                }
            }
        }
        return Map.of("labels", labels, "values", values);
    }

    private Map<String, Object> buildStatusData() {
        Map<String, Integer> counts = bookingDao.countByStatus();
        int confirmed = counts.getOrDefault("CONFIRMED", 0);
        int pending = counts.getOrDefault("PENDING", 0);
        int cancelled = counts.getOrDefault("CANCELLED", 0);
        int total = confirmed + pending + cancelled;

        double[] values = {confirmed, pending, cancelled};
        String[] labels = {"Confirmed", "Pending", "Cancelled"};
        Color[] colors = {Constants.COLOR_PRIMARY, Constants.COLOR_WARNING, Constants.COLOR_DANGER};

        return Map.of(
            "values", values,
            "labels", labels,
            "colors", colors,
            "total", String.valueOf(total)
        );
    }
}
