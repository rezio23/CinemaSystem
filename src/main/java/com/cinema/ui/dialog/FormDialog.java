package com.cinema.ui.dialog;

import com.cinema.ui.components.StyledButton;
import com.cinema.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class FormDialog extends JDialog {
    private final JPanel formPanel;
    private boolean confirmed = false;

    public FormDialog(Window owner, String title, int fieldCount) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        setSize(450, 80 + fieldCount * 50);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        formPanel = new JPanel(new GridLayout(0, 1, 8, 8));
        formPanel.setOpaque(false);
        add(formPanel, BorderLayout.CENTER);

        StyledButton saveBtn = new StyledButton("Save", StyledButton.Variant.PRIMARY);
        saveBtn.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        StyledButton cancelBtn = new StyledButton("Cancel", StyledButton.Variant.SECONDARY);
        cancelBtn.addActionListener(e -> dispose());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        add(btnPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(saveBtn);
    }

    public void addField(String label, JComponent field) {
        if (field instanceof JTextField) {
            Constants.styleInput((JTextField) field);
        } else if (field instanceof JComboBox<?>) {
            Constants.styleInput((JComboBox<?>) field);
        } else if (field instanceof JSpinner) {
            Constants.styleInput((JSpinner) field);
        }
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(Constants.FONT_BODY);
        lbl.setPreferredSize(new Dimension(110, 28));
        row.add(lbl, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        formPanel.add(row);
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
