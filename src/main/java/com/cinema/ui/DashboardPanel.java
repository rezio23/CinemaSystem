package com.cinema.ui;

import com.cinema.dao.*;
import com.cinema.ui.components.BarChartPanel;
import com.cinema.ui.components.StatCard;
import com.cinema.util.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
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

    private final StatCard customersCard;
    private final StatCard moviesCard;
    private final StatCard hallsCard;
    private final StatCard staffCard;
    private final StatCard upcomingCard;
    private final StatCard bookingsCard;
    private final StatCard ticketsCard;
    private final StatCard revenueCard;
    private final StatCard occupancyCard;

    private final DefaultTableModel revenueModel;
    private final DefaultTableModel occupancyModel;

    public DashboardPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Constants.COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Dashboard");
        header.setFont(Constants.FONT_HEADER);
        header.setForeground(Constants.COLOR_TEXT);
        add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(15, 15));
        center.setOpaque(false);

        // Stats row 1
        JPanel statsRow1 = new JPanel(new GridLayout(1, 5, 15, 0));
        statsRow1.setOpaque(false);
        customersCard = new StatCard("Customers", "0", Constants.COLOR_PRIMARY);
        moviesCard = new StatCard("Movies", "0", new Color(155, 89, 182));
        hallsCard = new StatCard("Halls", "0", new Color(46, 204, 113));
        staffCard = new StatCard("Staff", "0", new Color(241, 196, 15));
        upcomingCard = new StatCard("Upcoming Shows", "0", new Color(230, 126, 34));
        statsRow1.add(customersCard);
        statsRow1.add(moviesCard);
        statsRow1.add(hallsCard);
        statsRow1.add(staffCard);
        statsRow1.add(upcomingCard);

        // Stats row 2
        JPanel statsRow2 = new JPanel(new GridLayout(1, 4, 15, 0));
        statsRow2.setOpaque(false);
        bookingsCard = new StatCard("Confirmed Bookings", "0", Constants.COLOR_PRIMARY);
        ticketsCard = new StatCard("Tickets Sold", "0", new Color(155, 89, 182));
        revenueCard = new StatCard("Revenue Today", "$0", new Color(46, 204, 113));
        occupancyCard = new StatCard("Occupancy Rate", "0%", new Color(230, 126, 34));
        statsRow2.add(bookingsCard);
        statsRow2.add(ticketsCard);
        statsRow2.add(revenueCard);
        statsRow2.add(occupancyCard);

        JPanel statsPanel = new JPanel(new GridLayout(2, 1, 0, 15));
        statsPanel.setOpaque(false);
        statsPanel.add(statsRow1);
        statsPanel.add(statsRow2);

        center.add(statsPanel, BorderLayout.NORTH);

        // Tables
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        tablesPanel.setOpaque(false);

        revenueModel = new DefaultTableModel(new String[]{"Movie", "Revenue", "Tickets"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable revenueTable = new JTable(revenueModel);
        revenueTable.setFont(Constants.FONT_BODY);
        revenueTable.setRowHeight(28);
        revenueTable.getTableHeader().setFont(Constants.FONT_BODY);
        JScrollPane revenueScroll = new JScrollPane(revenueTable);
        revenueScroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Revenue per Movie", 0, 0, Constants.FONT_SUBHEADER, Constants.COLOR_TEXT));
        tablesPanel.add(revenueScroll);

        occupancyModel = new DefaultTableModel(new String[]{"Show", "Seats Sold", "Capacity", "Occupancy %"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable occupancyTable = new JTable(occupancyModel);
        occupancyTable.setFont(Constants.FONT_BODY);
        occupancyTable.setRowHeight(28);
        occupancyTable.getTableHeader().setFont(Constants.FONT_BODY);
        JScrollPane occupancyScroll = new JScrollPane(occupancyTable);
        occupancyScroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Seats Sold per Show", 0, 0, Constants.FONT_SUBHEADER, Constants.COLOR_TEXT));
        tablesPanel.add(occupancyScroll);

        center.add(tablesPanel, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        refreshData();
    }

    @Override
    public void refreshData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    customersCard.setValue(String.valueOf(customerDao.countAll()));
                    moviesCard.setValue(String.valueOf(movieDao.countAll()));
                    hallsCard.setValue(String.valueOf(hallDao.countAll()));
                    staffCard.setValue(String.valueOf(staffDao.countAll()));
                    upcomingCard.setValue(String.valueOf(showDao.countUpcoming()));
                    bookingsCard.setValue(String.valueOf(bookingDao.countConfirmed()));
                    ticketsCard.setValue(String.valueOf(bookingDao.countTicketsSold()));
                    revenueCard.setValue(String.format("$%.2f", bookingDao.getRevenueToday()));
                    occupancyCard.setValue(String.format("%.1f%%", reportDao.getOverallOccupancyRate()));

                    List<Map<String, Object>> rev = reportDao.revenuePerMovie();
                    SwingUtilities.invokeLater(() -> {
                        revenueModel.setRowCount(0);
                        for (Map<String, Object> row : rev) {
                            revenueModel.addRow(new Object[]{
                                    row.get("title"),
                                    "$" + row.get("totalRevenue"),
                                    row.get("ticketsSold")
                            });
                        }
                    });

                    List<Map<String, Object>> occ = reportDao.seatsSoldPerShow();
                    SwingUtilities.invokeLater(() -> {
                        occupancyModel.setRowCount(0);
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd HH:mm");
                        for (Map<String, Object> row : occ) {
                            Object dt = row.get("showDateTime");
                            String showLabel = row.get("movieTitle") + " @ " + row.get("hallName") + "\n" +
                                    (dt != null ? dtf.format((java.time.LocalDateTime) dt) : "");
                            occupancyModel.addRow(new Object[]{
                                    showLabel,
                                    row.get("seatsSold"),
                                    row.get("capacity"),
                                    row.get("occupancyRate") + "%"
                            });
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }
        };
        worker.execute();
    }
}
