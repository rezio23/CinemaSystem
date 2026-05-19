package com.cinema.ui;

import com.cinema.dao.CustomerDao;
import com.cinema.dao.ReportDao;
import com.cinema.model.Customer;
import com.cinema.ui.components.SearchField;
import com.cinema.ui.components.StyledButton;
import com.cinema.ui.dialog.AppDialog;
import com.cinema.ui.dialog.FormDialog;
import com.cinema.util.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class CustomerPanel extends JPanel implements MainFrame.Refreshable {

    private final CustomerDao dao = new CustomerDao();
    private final ReportDao reportDao = new ReportDao();
    private final DefaultTableModel model;
    private final JTable table;
    private final SearchField searchField;

    public CustomerPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Constants.COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel north = new JPanel(new BorderLayout(0, Constants.PAGE_HEADER_GAP));
        north.setOpaque(false);

        JLabel header = new JLabel("Customer Management");
        header.setFont(Constants.FONT_HEADER);
        header.setForeground(Constants.COLOR_TEXT);
        north.add(header, BorderLayout.NORTH);

        JPanel top = new JPanel(new BorderLayout(5, 0));
        top.setOpaque(false);
        searchField = new SearchField("Search by name, email, or phone...", this::search);
        top.add(searchField, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setOpaque(false);
        StyledButton addBtn = new StyledButton("+ Add Customer", StyledButton.Variant.SUCCESS);
        addBtn.addActionListener(e -> addCustomer());

        StyledButton editBtn = new StyledButton("Edit", StyledButton.Variant.SECONDARY);
        editBtn.addActionListener(e -> editCustomer());

        StyledButton delBtn = new StyledButton("Delete", StyledButton.Variant.DANGER);
        delBtn.addActionListener(e -> deleteCustomer());

        StyledButton historyBtn = new StyledButton("View History", StyledButton.Variant.SECONDARY);
        historyBtn.addActionListener(e -> viewHistory());

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(delBtn);
        btnPanel.add(historyBtn);
        top.add(btnPanel, BorderLayout.EAST);
        north.add(top, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Full Name", "Email", "Phone", "Created"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setFont(Constants.FONT_BODY);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);

        refreshData();
    }

    @Override
    public void refreshData() {
        SwingUtilities.invokeLater(() -> {
            model.setRowCount(0);
            List<Customer> list = dao.getAll();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (Customer c : list) {
                model.addRow(new Object[]{c.getCustomerId(), c.getFullName(), c.getEmail(), c.getPhone(),
                        c.getCreatedAt() != null ? c.getCreatedAt().format(dtf) : ""});
            }
        });
    }

    private void search() {
        String keyword = searchField.getText().trim();
        loadTable(keyword.isEmpty() ? dao.getAll() : dao.search(keyword));
    }

    private void loadTable(List<Customer> list) {
        SwingUtilities.invokeLater(() -> {
            model.setRowCount(0);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (Customer c : list) {
                model.addRow(new Object[]{c.getCustomerId(), c.getFullName(), c.getEmail(), c.getPhone(),
                        c.getCreatedAt() != null ? c.getCreatedAt().format(dtf) : ""});
            }
        });
    }

    private void addCustomer() {
        Customer c = showForm(null);
        if (c != null) {
            try {
                dao.insert(c);
                refreshData();
                AppDialog.showMessage(this, "Customer added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                AppDialog.showMessage(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editCustomer() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) model.getValueAt(row, 0);
        Customer existing = dao.getById(id);
        Customer updated = showForm(existing);
        if (updated != null) {
            try {
                updated.setCustomerId(id);
                dao.update(updated);
                refreshData();
                AppDialog.showMessage(this, "Customer updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                AppDialog.showMessage(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteCustomer() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) model.getValueAt(row, 0);
        int confirm = AppDialog.showConfirm(this, "Delete this customer?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dao.delete(id);
                refreshData();
                AppDialog.showMessage(this, "Customer deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                AppDialog.showMessage(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewHistory() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) model.getValueAt(row, 0);
        List<Map<String, Object>> history = reportDao.customerBookingHistory(id);

        String[] cols = {"Booking ID", "Movie", "Hall", "Show Time", "Seats", "Total", "Payment", "Status"};
        DefaultTableModel histModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Map<String, Object> h : history) {
            histModel.addRow(new Object[]{
                    h.get("bookingId"), h.get("movieTitle"), h.get("hallName"),
                    h.get("showDateTime") != null ? ((java.time.LocalDateTime) h.get("showDateTime")).format(dtf) : "",
                    h.get("seats"), "$" + h.get("totalAmount"), h.get("paymentMethod"), h.get("status")
            });
        }
        JTable histTable = new JTable(histModel);
        histTable.setFont(Constants.FONT_BODY);
        histTable.setRowHeight(40);
        histTable.setShowGrid(false);
        histTable.setIntercellSpacing(new Dimension(0, 0));
        histTable.setFillsViewportHeight(true);
        JScrollPane scroll = new JScrollPane(histTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setPreferredSize(new Dimension(800, 300));
        AppDialog.showContent(this, scroll, "Customer Booking History", JOptionPane.INFORMATION_MESSAGE);
    }

    private Customer showForm(Customer c) {
        JTextField name = new JTextField(c != null ? c.getFullName() : "");
        JTextField email = new JTextField(c != null ? c.getEmail() : "");
        JTextField phone = new JTextField(c != null ? c.getPhone() : "");

        FormDialog dlg = new FormDialog(SwingUtilities.getWindowAncestor(this), c == null ? "Add Customer" : "Edit Customer", 3);
        dlg.addField("Full Name:", name);
        dlg.addField("Email:", email);
        dlg.addField("Phone:", phone);
        dlg.setVisible(true);

        if (dlg.isConfirmed()) {
            Customer customer = new Customer();
            customer.setFullName(name.getText().trim());
            customer.setEmail(email.getText().trim());
            customer.setPhone(phone.getText().trim());
            return customer;
        }
        return null;
    }
}
