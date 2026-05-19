package com.cinema.ui;

import com.cinema.dao.*;
import com.cinema.model.MovieShow;
import com.cinema.ui.components.BarChartPanel;
import com.cinema.ui.components.StyledButton;
import com.cinema.util.Constants;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReportsPanel extends JPanel implements MainFrame.Refreshable {

    private final ReportDao reportDao = new ReportDao();
    private final BookingDao bookingDao = new BookingDao();
    private final BookingSeatDao bookingSeatDao = new BookingSeatDao();
    private final MovieShowDao showDao = new MovieShowDao();

    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    private JSpinner fromSpinner;
    private JSpinner toSpinner;

    private JPanel revenueMoviePanel;
    private JPanel dailyPanel;
    private JPanel weeklyPanel;
    private JPanel monthlyPanel;
    private JPanel yearlyPanel;
    private JPanel occupancyPanel;
    private JPanel customerHistoryPanel;
    private JPanel takenSeatsPanel;

    private DefaultTableModel revenueMovieModel;
    private DefaultTableModel dailyModel;
    private DefaultTableModel weeklyModel;
    private DefaultTableModel monthlyModel;
    private DefaultTableModel yearlyModel;
    private DefaultTableModel occupancyModel;
    private DefaultTableModel customerHistoryModel;

    private JPanel revenueMovieChartHolder;
    private JPanel occupancyChartHolder;

    private static final DateTimeFormatter SPINNER_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ReportsPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Constants.COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // ========== NORTH: Header + Filters ==========
        JPanel north = new JPanel(new BorderLayout(12, 12));
        north.setOpaque(false);

        JLabel header = new JLabel("Reports & Analytics");
        header.setFont(Constants.FONT_HEADER);
        header.setForeground(Constants.COLOR_TEXT);
        north.add(header, BorderLayout.NORTH);

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filterBar.setOpaque(false);

        String[] views = {"Revenue per Movie", "Daily Revenue", "Weekly Revenue", "Monthly Revenue", "Yearly Revenue", "Occupancy", "Customer History", "Taken Seats"};
        JComboBox<String> viewCombo = new JComboBox<>(views);
        viewCombo.setFont(Constants.FONT_BODY);
        viewCombo.setBackground(Constants.COLOR_CARD_ELEVATED);
        viewCombo.addActionListener(e -> switchView((String) viewCombo.getSelectedItem()));
        filterBar.add(new JLabel("Report:"));
        filterBar.add(viewCombo);

        filterBar.add(Box.createHorizontalStrut(12));

        JLabel fromLbl = new JLabel("From:");
        fromLbl.setFont(Constants.FONT_BODY);
        fromLbl.setForeground(Constants.COLOR_TEXT);
        filterBar.add(fromLbl);

        fromSpinner = createDateSpinner();
        filterBar.add(fromSpinner);

        JLabel toLbl = new JLabel("To:");
        toLbl.setFont(Constants.FONT_BODY);
        toLbl.setForeground(Constants.COLOR_TEXT);
        filterBar.add(toLbl);

        toSpinner = createDateSpinner();
        filterBar.add(toSpinner);

        StyledButton todayBtn = new StyledButton("Today", StyledButton.Variant.SECONDARY);
        todayBtn.addActionListener(e -> setPreset("day"));
        StyledButton weekBtn = new StyledButton("This Week", StyledButton.Variant.SECONDARY);
        weekBtn.addActionListener(e -> setPreset("week"));
        StyledButton monthBtn = new StyledButton("This Month", StyledButton.Variant.SECONDARY);
        monthBtn.addActionListener(e -> setPreset("month"));
        StyledButton yearBtn = new StyledButton("This Year", StyledButton.Variant.SECONDARY);
        yearBtn.addActionListener(e -> setPreset("year"));

        filterBar.add(todayBtn);
        filterBar.add(weekBtn);
        filterBar.add(monthBtn);
        filterBar.add(yearBtn);

        north.add(filterBar, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);

        // ========== CENTER: Report views ==========
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);

        revenueMoviePanel = createRevenueMoviePanel();
        dailyPanel = createDailyPanel();
        weeklyPanel = createWeeklyPanel();
        monthlyPanel = createMonthlyPanel();
        yearlyPanel = createYearlyPanel();
        occupancyPanel = createOccupancyPanel();
        customerHistoryPanel = createCustomerHistoryPanel();
        takenSeatsPanel = createTakenSeatsPanel();

        contentPanel.add(revenueMoviePanel, "Revenue per Movie");
        contentPanel.add(dailyPanel, "Daily Revenue");
        contentPanel.add(weeklyPanel, "Weekly Revenue");
        contentPanel.add(monthlyPanel, "Monthly Revenue");
        contentPanel.add(yearlyPanel, "Yearly Revenue");
        contentPanel.add(occupancyPanel, "Occupancy");
        contentPanel.add(customerHistoryPanel, "Customer History");
        contentPanel.add(takenSeatsPanel, "Taken Seats");

        add(contentPanel, BorderLayout.CENTER);
    }

    private JSpinner createDateSpinner() {
        SpinnerDateModel model = new SpinnerDateModel();
        JSpinner spinner = new JSpinner(model);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "yyyy-MM-dd"));
        spinner.setFont(Constants.FONT_BODY);
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) editor).getTextField().setBackground(Constants.COLOR_CARD_ELEVATED);
            ((JSpinner.DefaultEditor) editor).getTextField().setForeground(Constants.COLOR_TEXT);
            ((JSpinner.DefaultEditor) editor).getTextField().setCaretColor(Constants.COLOR_TEXT);
        }
        spinner.setPreferredSize(new Dimension(120, 28));
        return spinner;
    }

    private LocalDate getSpinnerDate(JSpinner spinner) {
        Date d = (Date) spinner.getValue();
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void setSpinnerDate(JSpinner spinner, LocalDate date) {
        spinner.setValue(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    private void setPreset(String preset) {
        LocalDate today = LocalDate.now();
        switch (preset) {
            case "day" -> {
                setSpinnerDate(fromSpinner, today);
                setSpinnerDate(toSpinner, today);
            }
            case "week" -> {
                setSpinnerDate(fromSpinner, today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)));
                setSpinnerDate(toSpinner, today.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY)));
            }
            case "month" -> {
                setSpinnerDate(fromSpinner, today.withDayOfMonth(1));
                setSpinnerDate(toSpinner, today.with(TemporalAdjusters.lastDayOfMonth()));
            }
            case "year" -> {
                setSpinnerDate(fromSpinner, today.withDayOfYear(1));
                setSpinnerDate(toSpinner, today.with(TemporalAdjusters.lastDayOfYear()));
            }
        }
    }

    private void switchView(String view) {
        cardLayout.show(contentPanel, view);
    }

    @Override
    public void refreshData() {
    }

    // ===================== PDF GENERATION =====================

    private void printPdf(String title, String[] headers, DefaultTableModel model, Component chart) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(title.replaceAll("\\s+", "_") + ".pdf"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = chooser.getSelectedFile();

        try {
            Document doc = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(doc, new FileOutputStream(file));
            doc.open();

            com.lowagie.text.Font titleFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 18, com.lowagie.text.Font.BOLD, new Color(0xFF6B00));
            Paragraph p = new Paragraph(title, titleFont);
            p.setAlignment(Element.ALIGN_CENTER);
            p.setSpacingAfter(12);
            doc.add(p);

            com.lowagie.text.Font filterFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.NORMAL, Color.DARK_GRAY);
            String filterText = "Period: " + SPINNER_FORMAT.format(getSpinnerDate(fromSpinner)) + " to " + SPINNER_FORMAT.format(getSpinnerDate(toSpinner));
            Paragraph filterP = new Paragraph(filterText, filterFont);
            filterP.setAlignment(Element.ALIGN_CENTER);
            filterP.setSpacingAfter(12);
            doc.add(filterP);

            if (chart != null) {
                BufferedImage img = new BufferedImage(chart.getWidth(), chart.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = img.createGraphics();
                chart.paint(g2d);
                g2d.dispose();
                com.lowagie.text.Image pdfImg = com.lowagie.text.Image.getInstance(img, null);
                pdfImg.scaleToFit(700, 300);
                pdfImg.setAlignment(Element.ALIGN_CENTER);
                pdfImg.setSpacingAfter(12);
                doc.add(pdfImg);
                doc.newPage();
            }

            PdfPTable table = new PdfPTable(headers.length);
            table.setWidthPercentage(100);
            com.lowagie.text.Font headerFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.BOLD, Color.WHITE);
            PdfPCell headerCell = new PdfPCell();
            headerCell.setBackgroundColor(new Color(0xFF6B00));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPadding(6);

            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(new Color(0xFF6B00));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(6);
                table.addCell(cell);
            }

            com.lowagie.text.Font cellFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 9, com.lowagie.text.Font.NORMAL, Color.BLACK);
            for (int r = 0; r < model.getRowCount(); r++) {
                for (int c = 0; c < model.getColumnCount(); c++) {
                    Object val = model.getValueAt(r, c);
                    PdfPCell cell = new PdfPCell(new Phrase(val != null ? val.toString() : "", cellFont));
                    cell.setPadding(4);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    if (r % 2 == 0) cell.setBackgroundColor(new Color(0xFFF5EB));
                    table.addCell(cell);
                }
            }
            doc.add(table);
            doc.close();
            JOptionPane.showMessageDialog(this, "PDF saved to:\n" + file.getAbsolutePath(), "PDF Exported", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to export PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printPdf(String title, String[] headers, DefaultTableModel model) {
        printPdf(title, headers, model, null);
    }

    // ===================== STYLED COMPONENTS =====================

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(Constants.FONT_BODY);
        table.setRowHeight(40);
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(Constants.COLOR_PRIMARY);
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
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

    private JPanel createButtonBar(JButton... buttons) {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bar.setOpaque(false);
        for (JButton b : buttons) bar.add(b);
        return bar;
    }

    // ===================== REPORT PANELS =====================

    private JPanel createRevenueMoviePanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setOpaque(false);

        revenueMovieModel = new DefaultTableModel(new String[]{"Movie", "Total Revenue", "Tickets Sold"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = createStyledTable(revenueMovieModel);
        JPanel tableCard = wrapInCard(createScroll(table), "Revenue per Movie");
        panel.add(tableCard, BorderLayout.NORTH);

        revenueMovieChartHolder = new JPanel(new BorderLayout());
        revenueMovieChartHolder.setOpaque(false);
        panel.add(revenueMovieChartHolder, BorderLayout.CENTER);

        StyledButton loadBtn = new StyledButton("Load Data", StyledButton.Variant.SECONDARY);
        StyledButton pdfBtn = new StyledButton("Print PDF", StyledButton.Variant.PRIMARY);
        loadBtn.addActionListener(e -> {
            List<Map<String, Object>> data = reportDao.revenuePerMovie(getSpinnerDate(fromSpinner), getSpinnerDate(toSpinner));
            revenueMovieModel.setRowCount(0);
            for (Map<String, Object> row : data) {
                revenueMovieModel.addRow(new Object[]{row.get("title"), "$" + row.get("totalRevenue"), row.get("ticketsSold")});
            }
            revenueMovieChartHolder.removeAll();
            revenueMovieChartHolder.add(new BarChartPanel(data, "title", "totalRevenue", "Revenue per Movie", Constants.COLOR_PRIMARY), BorderLayout.CENTER);
            revenueMovieChartHolder.revalidate();
            revenueMovieChartHolder.repaint();
        });
        pdfBtn.addActionListener(e -> printPdf("Revenue per Movie",
            new String[]{"Movie", "Total Revenue", "Tickets Sold"},
            revenueMovieModel, revenueMovieChartHolder.getComponentCount() > 0 ? revenueMovieChartHolder.getComponent(0) : null));
        panel.add(createButtonBar(loadBtn, pdfBtn), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createDailyPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setOpaque(false);
        dailyModel = new DefaultTableModel(new String[]{"Day", "Bookings", "Revenue"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = createStyledTable(dailyModel);
        panel.add(wrapInCard(createScroll(table), "Daily Revenue"), BorderLayout.CENTER);
        StyledButton loadBtn = new StyledButton("Load Data", StyledButton.Variant.SECONDARY);
        StyledButton pdfBtn = new StyledButton("Print PDF", StyledButton.Variant.PRIMARY);
        loadBtn.addActionListener(e -> {
            List<Map<String, Object>> data = reportDao.dailyRevenueAnalysis(getSpinnerDate(fromSpinner), getSpinnerDate(toSpinner));
            dailyModel.setRowCount(0);
            for (Map<String, Object> row : data) {
                dailyModel.addRow(new Object[]{row.get("day"), row.get("bookings"), "$" + row.get("revenue")});
            }
        });
        pdfBtn.addActionListener(e -> printPdf("Daily Revenue", new String[]{"Day", "Bookings", "Revenue"}, dailyModel));
        panel.add(createButtonBar(loadBtn, pdfBtn), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createWeeklyPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setOpaque(false);
        weeklyModel = new DefaultTableModel(new String[]{"Week Start", "Bookings", "Revenue"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = createStyledTable(weeklyModel);
        panel.add(wrapInCard(createScroll(table), "Weekly Revenue"), BorderLayout.CENTER);
        StyledButton loadBtn = new StyledButton("Load Data", StyledButton.Variant.SECONDARY);
        StyledButton pdfBtn = new StyledButton("Print PDF", StyledButton.Variant.PRIMARY);
        loadBtn.addActionListener(e -> {
            List<Map<String, Object>> data = reportDao.weeklyRevenueAnalysis(getSpinnerDate(fromSpinner), getSpinnerDate(toSpinner));
            weeklyModel.setRowCount(0);
            for (Map<String, Object> row : data) {
                weeklyModel.addRow(new Object[]{row.get("weekStart"), row.get("bookings"), "$" + row.get("revenue")});
            }
        });
        pdfBtn.addActionListener(e -> printPdf("Weekly Revenue", new String[]{"Week Start", "Bookings", "Revenue"}, weeklyModel));
        panel.add(createButtonBar(loadBtn, pdfBtn), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createMonthlyPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setOpaque(false);
        monthlyModel = new DefaultTableModel(new String[]{"Month", "Bookings", "Revenue"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = createStyledTable(monthlyModel);
        panel.add(wrapInCard(createScroll(table), "Monthly Revenue"), BorderLayout.CENTER);
        StyledButton loadBtn = new StyledButton("Load Data", StyledButton.Variant.SECONDARY);
        StyledButton pdfBtn = new StyledButton("Print PDF", StyledButton.Variant.PRIMARY);
        loadBtn.addActionListener(e -> {
            List<Map<String, Object>> data = reportDao.monthlyRevenueAnalysis(getSpinnerDate(fromSpinner), getSpinnerDate(toSpinner));
            monthlyModel.setRowCount(0);
            for (Map<String, Object> row : data) {
                monthlyModel.addRow(new Object[]{row.get("month"), row.get("bookings"), "$" + row.get("revenue")});
            }
        });
        pdfBtn.addActionListener(e -> printPdf("Monthly Revenue", new String[]{"Month", "Bookings", "Revenue"}, monthlyModel));
        panel.add(createButtonBar(loadBtn, pdfBtn), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createYearlyPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setOpaque(false);
        yearlyModel = new DefaultTableModel(new String[]{"Year", "Bookings", "Revenue"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = createStyledTable(yearlyModel);
        panel.add(wrapInCard(createScroll(table), "Yearly Revenue"), BorderLayout.CENTER);
        StyledButton loadBtn = new StyledButton("Load Data", StyledButton.Variant.SECONDARY);
        StyledButton pdfBtn = new StyledButton("Print PDF", StyledButton.Variant.PRIMARY);
        loadBtn.addActionListener(e -> {
            List<Map<String, Object>> data = reportDao.yearlyRevenueAnalysis(getSpinnerDate(fromSpinner), getSpinnerDate(toSpinner));
            yearlyModel.setRowCount(0);
            for (Map<String, Object> row : data) {
                yearlyModel.addRow(new Object[]{row.get("year"), row.get("bookings"), "$" + row.get("revenue")});
            }
        });
        pdfBtn.addActionListener(e -> printPdf("Yearly Revenue", new String[]{"Year", "Bookings", "Revenue"}, yearlyModel));
        panel.add(createButtonBar(loadBtn, pdfBtn), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createOccupancyPanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setOpaque(false);
        occupancyModel = new DefaultTableModel(new String[]{"Show", "Hall", "Seats Sold", "Capacity", "Occupancy %"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = createStyledTable(occupancyModel);
        panel.add(wrapInCard(createScroll(table), "Occupancy per Show"), BorderLayout.CENTER);

        occupancyChartHolder = new JPanel(new BorderLayout());
        occupancyChartHolder.setOpaque(false);
        panel.add(occupancyChartHolder, BorderLayout.EAST);

        StyledButton loadBtn = new StyledButton("Load Data", StyledButton.Variant.SECONDARY);
        StyledButton pdfBtn = new StyledButton("Print PDF", StyledButton.Variant.PRIMARY);
        loadBtn.addActionListener(e -> {
            List<Map<String, Object>> data = reportDao.seatsSoldPerShow(getSpinnerDate(fromSpinner), getSpinnerDate(toSpinner));
            occupancyModel.setRowCount(0);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (Map<String, Object> row : data) {
                Object dt = row.get("showDateTime");
                occupancyModel.addRow(new Object[]{
                    row.get("movieTitle") + " @ " + (dt != null ? ((java.time.LocalDateTime) dt).format(dtf) : ""),
                    row.get("hallName"), row.get("seatsSold"), row.get("capacity"),
                    row.get("occupancyRate") + "%"
                });
            }
            occupancyChartHolder.removeAll();
            occupancyChartHolder.add(new BarChartPanel(data, "movieTitle", "occupancyRate", "Occupancy Rate (%)", Constants.COLOR_PRIMARY), BorderLayout.CENTER);
            occupancyChartHolder.revalidate();
            occupancyChartHolder.repaint();
        });
        pdfBtn.addActionListener(e -> printPdf("Occupancy per Show",
            new String[]{"Show", "Hall", "Seats Sold", "Capacity", "Occupancy %"},
            occupancyModel, occupancyChartHolder.getComponentCount() > 0 ? occupancyChartHolder.getComponent(0) : null));
        panel.add(createButtonBar(loadBtn, pdfBtn), BorderLayout.SOUTH);
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

        customerHistoryModel = new DefaultTableModel(new String[]{"Booking ID", "Movie", "Hall", "Show Time", "Seats", "Total", "Payment", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = createStyledTable(customerHistoryModel);
        panel.add(wrapInCard(createScroll(table), "Customer History"), BorderLayout.CENTER);

        StyledButton loadBtn = new StyledButton("Load History", StyledButton.Variant.SECONDARY);
        StyledButton pdfBtn = new StyledButton("Print PDF", StyledButton.Variant.PRIMARY);
        loadBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(custIdField.getText().trim());
                List<Map<String, Object>> data = reportDao.customerBookingHistory(id);
                customerHistoryModel.setRowCount(0);
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                for (Map<String, Object> row : data) {
                    customerHistoryModel.addRow(new Object[]{
                        row.get("bookingId"), row.get("movieTitle"), row.get("hallName"),
                        row.get("showDateTime") != null ? ((java.time.LocalDateTime) row.get("showDateTime")).format(dtf) : "",
                        row.get("seats"), "$" + row.get("totalAmount"), row.get("paymentMethod"), row.get("status")
                    });
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Enter a valid Customer ID.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        pdfBtn.addActionListener(e -> printPdf("Customer History",
            new String[]{"Booking ID", "Movie", "Hall", "Show Time", "Seats", "Total", "Payment", "Status"},
            customerHistoryModel));
        top.add(createButtonBar(loadBtn, pdfBtn), BorderLayout.EAST);
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
        StyledButton loadBtn = new StyledButton("Load Seats", StyledButton.Variant.SECONDARY);
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
