package com.cinema.ui;

import com.cinema.dao.*;
import com.cinema.model.*;
import com.cinema.service.BookingService;
import com.cinema.service.CinemaException;
import com.cinema.ui.components.SearchField;
import com.cinema.util.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;

public class BookingPanel extends JPanel implements MainFrame.Refreshable {

    private final MovieShowDao showDao = new MovieShowDao();
    private final CustomerDao customerDao = new CustomerDao();
    private final StaffDao staffDao = new StaffDao();
    private final BookingSeatDao bookingSeatDao = new BookingSeatDao();
    private final BookingService bookingService = new BookingService();

    private final DefaultTableModel showModel;
    private final JTable showTable;
    private final SearchField showSearch;
    private SeatSelectionPanel seatPanel;
    private final JLabel selectedShowLabel;
    private final JLabel totalLabel;
    private final JComboBox<Staff> staffCombo;
    private final JComboBox<String> paymentCombo;
    private final JTextField customerSearchField;
    private final JList<Customer> customerList;
    private final DefaultListModel<Customer> customerListModel;
    private final JButton newCustomerBtn;
    private final JButton confirmBtn;
    private final JButton clearBtn;

    private MovieShow selectedShow;
    private Customer selectedCustomer;
    private Set<String> selectedSeats = new HashSet<>();

    public BookingPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Constants.COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel header = new JLabel("Sell Tickets");
        header.setFont(Constants.FONT_HEADER);
        header.setForeground(Constants.COLOR_TEXT);
        add(header, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(520);
        split.setOpaque(false);
        split.setBorder(BorderFactory.createEmptyBorder());

        // ========== LEFT: Show selection + seats ==========
        JPanel left = new JPanel(new BorderLayout(16, 16));
        left.setOpaque(false);

        // Show search + table card
        JPanel showCard = new JPanel(new BorderLayout(12, 12));
        showCard.setBackground(Constants.COLOR_CARD);
        showCard.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel showTitle = new JLabel("Select a Show");
        showTitle.setFont(Constants.FONT_SUBHEADER);
        showTitle.setForeground(Constants.COLOR_TEXT);
        showCard.add(showTitle, BorderLayout.NORTH);

        showSearch = new SearchField("Search shows by movie or hall...", this::filterShows);
        showCard.add(showSearch, BorderLayout.CENTER);

        showModel = new DefaultTableModel(new String[]{"ID", "Movie", "Hall", "Date/Time", "Price"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        showTable = createStyledTable(showModel);
        showTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        showTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        showTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        showTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        showTable.getColumnModel().getColumn(3).setPreferredWidth(130);
        showTable.getColumnModel().getColumn(4).setPreferredWidth(80);

        JScrollPane showScroll = new JScrollPane(showTable);
        showScroll.setBorder(BorderFactory.createEmptyBorder());
        showScroll.setPreferredSize(new Dimension(0, 220));
        showCard.add(showScroll, BorderLayout.SOUTH);
        left.add(showCard, BorderLayout.NORTH);

        showTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && showTable.getSelectedRow() >= 0) {
                int showId = (int) showModel.getValueAt(showTable.getSelectedRow(), 0);
                selectShow(showId);
            }
        });

        // Seat area
        JPanel seatCard = new JPanel(new BorderLayout(12, 12));
        seatCard.setBackground(Constants.COLOR_CARD);
        seatCard.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        selectedShowLabel = new JLabel("No show selected");
        selectedShowLabel.setFont(Constants.FONT_SUBHEADER);
        selectedShowLabel.setForeground(Constants.COLOR_TEXT_MUTED);
        seatCard.add(selectedShowLabel, BorderLayout.NORTH);

        seatPanel = new SeatSelectionPanel(0, Collections.emptyList(), seats -> {
            selectedSeats = seats;
            updateTotal();
        });
        seatCard.add(seatPanel, BorderLayout.CENTER);

        JPanel seatBottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        seatBottom.setOpaque(false);
        clearBtn = new JButton("Clear Seats");
        clearBtn.setFont(Constants.FONT_BODY);
        clearBtn.setFocusPainted(false);
        clearBtn.setBackground(Constants.COLOR_CARD_ELEVATED);
        clearBtn.setForeground(Constants.COLOR_TEXT);
        clearBtn.addActionListener(e -> {
            if (seatPanel != null) seatPanel.clearSelection();
        });
        seatBottom.add(clearBtn);
        seatCard.add(seatBottom, BorderLayout.SOUTH);

        left.add(seatCard, BorderLayout.CENTER);
        split.setLeftComponent(left);

        // ========== RIGHT: Customer + payment + confirm ==========
        JPanel right = new JPanel(new BorderLayout(16, 16));
        right.setOpaque(false);

        // Customer card
        JPanel customerCard = new JPanel(new BorderLayout(12, 12));
        customerCard.setBackground(Constants.COLOR_CARD);
        customerCard.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel customerTitle = new JLabel("Customer");
        customerTitle.setFont(Constants.FONT_SUBHEADER);
        customerTitle.setForeground(Constants.COLOR_TEXT);
        customerCard.add(customerTitle, BorderLayout.NORTH);

        JPanel customerSearchRow = new JPanel(new BorderLayout(8, 0));
        customerSearchRow.setOpaque(false);
        customerSearchField = new JTextField(20);
        customerSearchField.setFont(Constants.FONT_BODY);
        JButton searchCustomerBtn = new JButton("Search");
        searchCustomerBtn.setFont(Constants.FONT_BODY);
        searchCustomerBtn.setFocusPainted(false);
        searchCustomerBtn.addActionListener(e -> searchCustomers());
        customerSearchRow.add(customerSearchField, BorderLayout.CENTER);
        customerSearchRow.add(searchCustomerBtn, BorderLayout.EAST);
        customerCard.add(customerSearchRow, BorderLayout.CENTER);

        customerListModel = new DefaultListModel<>();
        customerList = new JList<>(customerListModel);
        customerList.setFont(Constants.FONT_BODY);
        customerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerList.setFixedCellHeight(36);
        customerList.setBackground(Constants.COLOR_CARD_ELEVATED);
        JScrollPane customerScroll = new JScrollPane(customerList);
        customerScroll.setBorder(BorderFactory.createEmptyBorder());
        customerScroll.setPreferredSize(new Dimension(0, 140));
        customerCard.add(customerScroll, BorderLayout.SOUTH);

        customerList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedCustomer = customerList.getSelectedValue();
            }
        });

        right.add(customerCard, BorderLayout.NORTH);

        // Booking details card
        JPanel detailsCard = new JPanel(new GridLayout(0, 1, 12, 12));
        detailsCard.setBackground(Constants.COLOR_CARD);
        detailsCard.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel detailsTitle = new JLabel("Booking Details");
        detailsTitle.setFont(Constants.FONT_SUBHEADER);
        detailsTitle.setForeground(Constants.COLOR_TEXT);
        detailsCard.add(detailsTitle);

        JPanel staffRow = new JPanel(new BorderLayout(8, 0));
        staffRow.setOpaque(false);
        JLabel staffLbl = new JLabel("Staff:");
        staffLbl.setFont(Constants.FONT_BODY);
        staffLbl.setForeground(Constants.COLOR_TEXT);
        staffRow.add(staffLbl, BorderLayout.WEST);
        staffCombo = new JComboBox<>();
        staffCombo.setFont(Constants.FONT_BODY);
        staffCombo.setBackground(Constants.COLOR_CARD_ELEVATED);
        staffRow.add(staffCombo, BorderLayout.CENTER);
        detailsCard.add(staffRow);

        JPanel paymentRow = new JPanel(new BorderLayout(8, 0));
        paymentRow.setOpaque(false);
        JLabel payLbl = new JLabel("Payment:");
        payLbl.setFont(Constants.FONT_BODY);
        payLbl.setForeground(Constants.COLOR_TEXT);
        paymentRow.add(payLbl, BorderLayout.WEST);
        paymentCombo = new JComboBox<>(Constants.PAYMENT_METHODS);
        paymentCombo.setFont(Constants.FONT_BODY);
        paymentCombo.setBackground(Constants.COLOR_CARD_ELEVATED);
        paymentRow.add(paymentCombo, BorderLayout.CENTER);
        detailsCard.add(paymentRow);

        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        totalLabel.setForeground(Constants.COLOR_PRIMARY);
        detailsCard.add(totalLabel);

        right.add(detailsCard, BorderLayout.CENTER);

        // Action area
        JPanel actionCard = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionCard.setBackground(Constants.COLOR_CARD);
        actionCard.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        newCustomerBtn = new JButton("+ New Customer");
        newCustomerBtn.setFont(Constants.FONT_BODY);
        newCustomerBtn.setFocusPainted(false);
        newCustomerBtn.setBackground(Constants.COLOR_CARD_ELEVATED);
        newCustomerBtn.setForeground(Constants.COLOR_TEXT);
        newCustomerBtn.addActionListener(e -> openNewCustomerDialog());
        actionCard.add(newCustomerBtn);

        confirmBtn = new JButton("Confirm Booking");
        confirmBtn.setFont(Constants.FONT_SUBHEADER);
        confirmBtn.setBackground(Constants.COLOR_PRIMARY);
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFocusPainted(false);
        confirmBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmBtn.addActionListener(e -> confirmBooking());
        actionCard.add(confirmBtn);

        right.add(actionCard, BorderLayout.SOUTH);

        split.setRightComponent(right);
        add(split, BorderLayout.CENTER);

        refreshData();
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

    @Override
    public void refreshData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                loadShows();
                loadStaff();
                return null;
            }
        };
        worker.execute();
    }

    private void loadShows() {
        List<MovieShow> shows = showDao.getUpcoming();
        SwingUtilities.invokeLater(() -> {
            showModel.setRowCount(0);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd HH:mm");
            for (MovieShow s : shows) {
                showModel.addRow(new Object[]{
                        s.getShowId(),
                        s.getMovieTitle(),
                        s.getHallName(),
                        s.getShowDateTime() != null ? s.getShowDateTime().format(dtf) : "",
                        "$" + s.getBasePrice()
                });
            }
        });
    }

    private void filterShows() {
        String keyword = showSearch.getText().trim().toLowerCase();
        javax.swing.table.TableRowSorter<?> sorter = (javax.swing.table.TableRowSorter<?>) showTable.getRowSorter();
        if (sorter == null) {
            sorter = new javax.swing.table.TableRowSorter<>(showModel);
            showTable.setRowSorter(sorter);
        }
        if (keyword.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + keyword, 1, 2));
        }
    }

    private void loadStaff() {
        List<Staff> staff = staffDao.getAll();
        SwingUtilities.invokeLater(() -> {
            staffCombo.removeAllItems();
            for (Staff s : staff) {
                staffCombo.addItem(s);
            }
        });
    }

    private void selectShow(int showId) {
        selectedShow = showDao.getById(showId);
        if (selectedShow == null) return;

        SwingUtilities.invokeLater(() -> {
            selectedShowLabel.setText(selectedShow.getMovieTitle() + " @ " + selectedShow.getHallName() +
                    " - " + selectedShow.getShowDateTime().format(DateTimeFormatter.ofPattern("MM/dd HH:mm")));
            selectedShowLabel.setForeground(Constants.COLOR_TEXT);
            seatPanel.clearSelection();

            List<String> taken = bookingSeatDao.getTakenSeatNumbersByShow(showId);
            // Rebuild seat panel
            Container parent = seatPanel.getParent();
            parent.remove(seatPanel);
            seatPanel = new SeatSelectionPanel(selectedShow.getHallCapacity(), taken, seats -> {
                selectedSeats = seats;
                updateTotal();
            });
            parent.add(seatPanel, BorderLayout.CENTER);
            parent.revalidate();
            parent.repaint();
        });
    }

    private void updateTotal() {
        if (selectedShow == null || selectedSeats.isEmpty()) {
            totalLabel.setText("Total: $0.00");
            return;
        }
        BigDecimal total = selectedShow.getBasePrice().multiply(BigDecimal.valueOf(selectedSeats.size()));
        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    private void searchCustomers() {
        String keyword = customerSearchField.getText().trim();
        if (keyword.isEmpty()) return;
        List<Customer> results = customerDao.search(keyword);
        SwingUtilities.invokeLater(() -> {
            customerListModel.clear();
            for (Customer c : results) {
                customerListModel.addElement(c);
            }
        });
    }

    private void openNewCustomerDialog() {
        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField phoneField = new JTextField(20);

        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.setBackground(Constants.COLOR_CARD);
        panel.add(new JLabel("Full Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);

        int result = JOptionPane.showConfirmDialog(this, panel, "New Customer", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Customer c = new Customer();
                c.setFullName(nameField.getText().trim());
                c.setEmail(emailField.getText().trim());
                c.setPhone(phoneField.getText().trim());
                int id = customerDao.insert(c);
                c.setCustomerId(id);
                selectedCustomer = c;
                customerListModel.clear();
                customerListModel.addElement(c);
                customerList.setSelectedIndex(0);
                JOptionPane.showMessageDialog(this, "Customer created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void confirmBooking() {
        if (selectedShow == null) {
            JOptionPane.showMessageDialog(this, "Please select a show.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (selectedSeats.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one seat.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this, "Please select or create a customer.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Staff staff = (Staff) staffCombo.getSelectedItem();
        if (staff == null) {
            JOptionPane.showMessageDialog(this, "Please select staff.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Booking booking = new Booking();
        booking.setCustomerId(selectedCustomer.getCustomerId());
        booking.setShowId(selectedShow.getShowId());
        booking.setStaffId(staff.getStaffId());
        booking.setPaymentMethod((String) paymentCombo.getSelectedItem());
        BigDecimal total = selectedShow.getBasePrice().multiply(BigDecimal.valueOf(selectedSeats.size()));
        booking.setTotalAmount(total);

        List<BookingSeat> seats = new ArrayList<>();
        for (String seatNum : selectedSeats) {
            BookingSeat bs = new BookingSeat();
            bs.setShowId(selectedShow.getShowId());
            bs.setSeatNumber(seatNum);
            bs.setSeatPrice(selectedShow.getBasePrice());
            bs.setSeatType("STANDARD");
            seats.add(bs);
        }

        try {
            int bookingId = bookingService.createBooking(booking, seats);
            showReceipt(bookingId);
            refreshData();
            selectedShow = null;
            selectedSeats.clear();
            selectedCustomer = null;
            customerListModel.clear();
            totalLabel.setText("Total: $0.00");
            selectedShowLabel.setText("No show selected");
            selectedShowLabel.setForeground(Constants.COLOR_TEXT_MUTED);
            Container parent = seatPanel.getParent();
            parent.remove(seatPanel);
            seatPanel = new SeatSelectionPanel(0, Collections.emptyList(), s -> {});
            parent.add(seatPanel, BorderLayout.CENTER);
            parent.revalidate();
            parent.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Booking Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showReceipt(int bookingId) {
        BookingDao dao = new BookingDao();
        Booking b = dao.getById(bookingId);
        List<BookingSeat> seats = bookingSeatDao.getByBookingId(bookingId);

        StringBuilder sb = new StringBuilder();
        sb.append("Booking Receipt\n");
        sb.append("=================\n\n");
        sb.append("Booking ID: ").append(b.getBookingId()).append("\n");
        sb.append("Customer: ").append(b.getCustomerName()).append("\n");
        sb.append("Movie: ").append(b.getMovieTitle()).append("\n");
        sb.append("Hall: ").append(b.getHallName()).append("\n");
        sb.append("Show Time: ").append(b.getShowDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("\n");
        sb.append("Staff: ").append(b.getStaffName()).append("\n");
        sb.append("Payment: ").append(b.getPaymentMethod()).append("\n");
        sb.append("Status: ").append(b.getStatus()).append("\n\n");
        sb.append("Seats: ");
        for (int i = 0; i < seats.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(seats.get(i).getSeatNumber());
        }
        sb.append("\n\nTotal: $").append(b.getTotalAmount());
        sb.append("\nDate: ").append(b.getBookingDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        JTextArea area = new JTextArea(sb.toString());
        area.setFont(new Font("Consolas", Font.PLAIN, 14));
        area.setEditable(false);
        area.setBackground(Constants.COLOR_CARD);
        area.setForeground(Constants.COLOR_TEXT);
        area.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JScrollPane sp = new JScrollPane(area);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setPreferredSize(new Dimension(420, 360));
        JOptionPane.showMessageDialog(this, sp, "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);
    }
}
