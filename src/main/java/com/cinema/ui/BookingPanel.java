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
        setLayout(new BorderLayout(15, 15));
        setBackground(Constants.COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Sell Tickets");
        header.setFont(Constants.FONT_HEADER);
        header.setForeground(Constants.COLOR_TEXT);
        add(header, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(500);
        split.setOpaque(false);

        // Left: Show selection + seats
        JPanel left = new JPanel(new BorderLayout(10, 10));
        left.setOpaque(false);

        JPanel showTop = new JPanel(new BorderLayout(5, 5));
        showTop.setOpaque(false);
        showTop.add(new JLabel("Select a Show"), BorderLayout.NORTH);
        showSearch = new SearchField("Search shows by movie or hall...", this::filterShows);
        showTop.add(showSearch, BorderLayout.CENTER);
        left.add(showTop, BorderLayout.NORTH);

        showModel = new DefaultTableModel(new String[]{"ID", "Movie", "Hall", "Date/Time", "Price"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        showTable = new JTable(showModel);
        showTable.setFont(Constants.FONT_BODY);
        showTable.setRowHeight(28);
        showTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        showTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        showTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        showTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        showTable.getColumnModel().getColumn(3).setPreferredWidth(130);
        showTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        JScrollPane showScroll = new JScrollPane(showTable);
        showScroll.setPreferredSize(new Dimension(0, 180));
        left.add(showScroll, BorderLayout.CENTER);

        showTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && showTable.getSelectedRow() >= 0) {
                int showId = (int) showModel.getValueAt(showTable.getSelectedRow(), 0);
                selectShow(showId);
            }
        });

        JPanel seatArea = new JPanel(new BorderLayout(5, 5));
        seatArea.setOpaque(false);
        selectedShowLabel = new JLabel("No show selected");
        selectedShowLabel.setFont(Constants.FONT_SUBHEADER);
        selectedShowLabel.setForeground(Constants.COLOR_TEXT);
        seatArea.add(selectedShowLabel, BorderLayout.NORTH);

        seatPanel = new SeatSelectionPanel(0, Collections.emptyList(), seats -> {
            selectedSeats = seats;
            updateTotal();
        });
        seatArea.add(seatPanel, BorderLayout.CENTER);

        JPanel seatBottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        seatBottom.setOpaque(false);
        clearBtn = new JButton("Clear Seats");
        clearBtn.setFont(Constants.FONT_BODY);
        clearBtn.addActionListener(e -> {
            if (seatPanel != null) seatPanel.clearSelection();
        });
        seatBottom.add(clearBtn);
        seatArea.add(seatBottom, BorderLayout.SOUTH);

        left.add(seatArea, BorderLayout.SOUTH);
        split.setLeftComponent(left);

        // Right: Customer + payment + confirm
        JPanel right = new JPanel(new BorderLayout(10, 10));
        right.setOpaque(false);
        right.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JPanel customerPanel = new JPanel(new BorderLayout(5, 5));
        customerPanel.setOpaque(false);
        customerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Customer", 0, 0, Constants.FONT_SUBHEADER, Constants.COLOR_TEXT));

        JPanel customerSearchRow = new JPanel(new BorderLayout(5, 0));
        customerSearchRow.setOpaque(false);
        customerSearchField = new JTextField(20);
        customerSearchField.setFont(Constants.FONT_BODY);
        JButton searchCustomerBtn = new JButton("Search");
        searchCustomerBtn.setFont(Constants.FONT_BODY);
        searchCustomerBtn.addActionListener(e -> searchCustomers());
        customerSearchRow.add(customerSearchField, BorderLayout.CENTER);
        customerSearchRow.add(searchCustomerBtn, BorderLayout.EAST);
        customerPanel.add(customerSearchRow, BorderLayout.NORTH);

        customerListModel = new DefaultListModel<>();
        customerList = new JList<>(customerListModel);
        customerList.setFont(Constants.FONT_BODY);
        customerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerList.setFixedCellHeight(28);
        JScrollPane customerScroll = new JScrollPane(customerList);
        customerScroll.setPreferredSize(new Dimension(0, 120));
        customerPanel.add(customerScroll, BorderLayout.CENTER);

        customerList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedCustomer = customerList.getSelectedValue();
            }
        });

        newCustomerBtn = new JButton("+ New Customer");
        newCustomerBtn.setFont(Constants.FONT_BODY);
        newCustomerBtn.addActionListener(e -> openNewCustomerDialog());
        JPanel customerBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        customerBtnPanel.setOpaque(false);
        customerBtnPanel.add(newCustomerBtn);
        customerPanel.add(customerBtnPanel, BorderLayout.SOUTH);

        right.add(customerPanel, BorderLayout.NORTH);

        JPanel detailsPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Booking Details", 0, 0, Constants.FONT_SUBHEADER, Constants.COLOR_TEXT));

        JPanel staffRow = new JPanel(new BorderLayout(5, 0));
        staffRow.setOpaque(false);
        staffRow.add(new JLabel("Staff:"), BorderLayout.WEST);
        staffCombo = new JComboBox<>();
        staffCombo.setFont(Constants.FONT_BODY);
        staffRow.add(staffCombo, BorderLayout.CENTER);
        detailsPanel.add(staffRow);

        JPanel paymentRow = new JPanel(new BorderLayout(5, 0));
        paymentRow.setOpaque(false);
        paymentRow.add(new JLabel("Payment:"), BorderLayout.WEST);
        paymentCombo = new JComboBox<>(Constants.PAYMENT_METHODS);
        paymentCombo.setFont(Constants.FONT_BODY);
        paymentRow.add(paymentCombo, BorderLayout.CENTER);
        detailsPanel.add(paymentRow);

        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(Constants.FONT_SUBHEADER);
        totalLabel.setForeground(Constants.COLOR_PRIMARY);
        detailsPanel.add(totalLabel);

        right.add(detailsPanel, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);
        confirmBtn = new JButton("Confirm Booking");
        confirmBtn.setFont(Constants.FONT_SUBHEADER);
        confirmBtn.setBackground(Constants.COLOR_SUCCESS);
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFocusPainted(false);
        confirmBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmBtn.addActionListener(e -> confirmBooking());
        actionPanel.add(confirmBtn);
        right.add(actionPanel, BorderLayout.SOUTH);

        split.setRightComponent(right);
        add(split, BorderLayout.CENTER);

        refreshData();
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

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
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
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        area.setEditable(false);
        area.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);
    }
}
