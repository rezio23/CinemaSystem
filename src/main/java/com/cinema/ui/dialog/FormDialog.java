package com.cinema.ui.dialog;

import com.cinema.ui.components.StyledButton;
import com.cinema.util.Constants;

import javax.swing.*;
import java.awt.*;

public class FormDialog extends JDialog {
    private final JPanel formPanel;
    private boolean confirmed = false;

    public FormDialog(Window owner, String title, int fieldCount) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        StyledButton saveBtn = new StyledButton("Save", StyledButton.Variant.PRIMARY);
        saveBtn.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        StyledButton cancelBtn = new StyledButton("Cancel", StyledButton.Variant.SECONDARY);
        cancelBtn.addActionListener(e -> dispose());

        AppDialog.installShell(this, title, formPanel, AppDialog.createActionBar(cancelBtn, saveBtn), saveBtn);
    }

    public void addField(String label, JComponent field) {
        if (field instanceof JTextField) {
            Constants.styleInput((JTextField) field);
        } else if (field instanceof JComboBox<?>) {
            Constants.styleInput((JComboBox<?>) field);
        } else if (field instanceof JSpinner) {
            Constants.styleInput((JSpinner) field);
        }
        JPanel row = new JPanel(new BorderLayout(0, 6));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JLabel lbl = new JLabel(label);
        lbl.setFont(Constants.FONT_BODY);
        lbl.setForeground(Constants.COLOR_TEXT);
        row.add(lbl, BorderLayout.NORTH);
        row.add(field, BorderLayout.CENTER);
        formPanel.add(row);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            AppDialog.prepareAndCenter(this, getOwner(), 460);
        }
        super.setVisible(visible);
    }
}
