package com.cinema.ui;

import com.cinema.dao.StaffDao;
import com.cinema.model.Staff;
import com.cinema.ui.components.StyledButton;
import com.cinema.ui.dialog.FormDialog;
import com.cinema.util.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StaffPanel extends JPanel implements MainFrame.Refreshable {

    private final StaffDao dao = new StaffDao();
    private final DefaultTableModel model;
    private final JTable table;

    public StaffPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Constants.COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel north = new JPanel(new BorderLayout(0, Constants.PAGE_HEADER_GAP));
        north.setOpaque(false);

        JLabel header = new JLabel("Staff Management");
        header.setFont(Constants.FONT_HEADER);
        header.setForeground(Constants.COLOR_TEXT);
        north.add(header, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setOpaque(false);
        StyledButton addBtn = new StyledButton("+ Add Staff", StyledButton.Variant.SUCCESS);
        addBtn.addActionListener(e -> addStaff());

        StyledButton editBtn = new StyledButton("Edit", StyledButton.Variant.SECONDARY);
        editBtn.addActionListener(e -> editStaff());

        StyledButton delBtn = new StyledButton("Delete", StyledButton.Variant.DANGER);
        delBtn.addActionListener(e -> deleteStaff());

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(delBtn);
        north.add(btnPanel, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Full Name", "Role", "Hire Date"}, 0) {
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
            List<Staff> list = dao.getAll();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (Staff s : list) {
                model.addRow(new Object[]{s.getStaffId(), s.getFullName(), s.getRole(),
                        s.getHireDate() != null ? s.getHireDate().format(dtf) : ""});
            }
        });
    }

    private void addStaff() {
        Staff s = showForm(null);
        if (s != null) {
            try {
                dao.insert(s);
                refreshData();
                JOptionPane.showMessageDialog(this, "Staff added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editStaff() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) model.getValueAt(row, 0);
        Staff existing = dao.getById(id);
        Staff updated = showForm(existing);
        if (updated != null) {
            try {
                updated.setStaffId(id);
                dao.update(updated);
                refreshData();
                JOptionPane.showMessageDialog(this, "Staff updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteStaff() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this staff member?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dao.delete(id);
                refreshData();
                JOptionPane.showMessageDialog(this, "Staff deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Staff showForm(Staff s) {
        JTextField name = new JTextField(s != null ? s.getFullName() : "");
        JTextField role = new JTextField(s != null ? s.getRole() : "");
        JTextField hireDate = new JTextField(s != null && s.getHireDate() != null ? s.getHireDate().toString() : LocalDate.now().toString());

        FormDialog dlg = new FormDialog(SwingUtilities.getWindowAncestor(this), s == null ? "Add Staff" : "Edit Staff", 3);
        dlg.addField("Full Name:", name);
        dlg.addField("Role:", role);
        dlg.addField("Hire Date (YYYY-MM-DD):", hireDate);
        dlg.setVisible(true);

        if (dlg.isConfirmed()) {
            try {
                Staff staff = new Staff();
                staff.setFullName(name.getText().trim());
                staff.setRole(role.getText().trim());
                staff.setHireDate(LocalDate.parse(hireDate.getText().trim()));
                return staff;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }
}
