package com.cinema.ui;

import com.cinema.dao.HallDao;
import com.cinema.model.Hall;
import com.cinema.ui.dialog.FormDialog;
import com.cinema.util.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HallPanel extends JPanel implements MainFrame.Refreshable {

    private final HallDao dao = new HallDao();
    private final DefaultTableModel model;
    private final JTable table;

    public HallPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Constants.COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);

        JLabel header = new JLabel("Hall Management");
        header.setFont(Constants.FONT_HEADER);
        header.setForeground(Constants.COLOR_TEXT);
        north.add(header, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setOpaque(false);
        JButton addBtn = new JButton("+ Add Hall");
        addBtn.setFont(Constants.FONT_BODY);
        addBtn.setBackground(Constants.COLOR_SUCCESS);
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(e -> addHall());

        JButton editBtn = new JButton("Edit");
        editBtn.setFont(Constants.FONT_BODY);
        editBtn.addActionListener(e -> editHall());

        JButton delBtn = new JButton("Delete");
        delBtn.setFont(Constants.FONT_BODY);
        delBtn.setBackground(Constants.COLOR_DANGER);
        delBtn.setForeground(Color.WHITE);
        delBtn.setFocusPainted(false);
        delBtn.addActionListener(e -> deleteHall());

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(delBtn);
        north.add(btnPanel, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Name", "Capacity", "Type"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setFont(Constants.FONT_BODY);
        table.setRowHeight(28);
        table.getTableHeader().setFont(Constants.FONT_BODY);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refreshData();
    }

    @Override
    public void refreshData() {
        SwingUtilities.invokeLater(() -> {
            model.setRowCount(0);
            List<Hall> list = dao.getAll();
            for (Hall h : list) {
                model.addRow(new Object[]{h.getHallId(), h.getHallName(), h.getCapacity(), h.getHallType()});
            }
        });
    }

    private void addHall() {
        Hall h = showForm(null);
        if (h != null) {
            try {
                dao.insert(h);
                refreshData();
                JOptionPane.showMessageDialog(this, "Hall added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editHall() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) model.getValueAt(row, 0);
        Hall existing = dao.getById(id);
        Hall updated = showForm(existing);
        if (updated != null) {
            try {
                updated.setHallId(id);
                dao.update(updated);
                refreshData();
                JOptionPane.showMessageDialog(this, "Hall updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteHall() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this hall?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dao.delete(id);
                refreshData();
                JOptionPane.showMessageDialog(this, "Hall deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Hall showForm(Hall h) {
        JTextField name = new JTextField(h != null ? h.getHallName() : "");
        JTextField capacity = new JTextField(h != null ? String.valueOf(h.getCapacity()) : "");
        JTextField type = new JTextField(h != null ? h.getHallType() : "");

        FormDialog dlg = new FormDialog(SwingUtilities.getWindowAncestor(this), h == null ? "Add Hall" : "Edit Hall", 3);
        dlg.addField("Name:", name);
        dlg.addField("Capacity:", capacity);
        dlg.addField("Type:", type);
        dlg.setVisible(true);

        if (dlg.isConfirmed()) {
            try {
                Hall hall = new Hall();
                hall.setHallName(name.getText().trim());
                hall.setCapacity(Integer.parseInt(capacity.getText().trim()));
                hall.setHallType(type.getText().trim());
                return hall;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid capacity.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }
}
