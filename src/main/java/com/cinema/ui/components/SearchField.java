package com.cinema.ui.components;

import com.cinema.util.Constants;

import javax.swing.*;
import java.awt.*;

public class SearchField extends JPanel {
    private final JTextField field;
    private final JButton searchBtn;
    private final String placeholder;

    public SearchField(String placeholder, Runnable onSearch) {
        this.placeholder = placeholder;
        setLayout(new BorderLayout(8, 0));
        setBackground(Constants.COLOR_CARD_ELEVATED);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD0D9E2), 1, true),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));

        field = new JTextField(placeholder);
        field.setFont(Constants.FONT_BODY);
        field.setBorder(null);
        field.setOpaque(false);
        field.setForeground(Constants.COLOR_TEXT_MUTED);
        field.setCaretColor(Constants.COLOR_TEXT);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Constants.COLOR_TEXT);
                }
                SearchField.this.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Constants.COLOR_PRIMARY, 1, true),
                        BorderFactory.createEmptyBorder(8, 14, 8, 14)
                ));
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Constants.COLOR_TEXT_MUTED);
                }
                SearchField.this.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0xD0D9E2), 1, true),
                        BorderFactory.createEmptyBorder(8, 14, 8, 14)
                ));
            }
        });

        searchBtn = new JButton("🔍");
        searchBtn.setFont(Constants.FONT_BODY);
        searchBtn.setFocusPainted(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setContentAreaFilled(false);
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchBtn.setForeground(Constants.COLOR_TEXT_MUTED);

        add(field, BorderLayout.CENTER);
        add(searchBtn, BorderLayout.EAST);

        searchBtn.addActionListener(e -> onSearch.run());
        field.addActionListener(e -> onSearch.run());
    }

    public String getText() {
        String text = field.getText();
        return text.equals(placeholder) ? "" : text;
    }

    public void setText(String text) {
        field.setText(text);
        field.setForeground(text.isEmpty() ? Constants.COLOR_TEXT_MUTED : Constants.COLOR_TEXT);
    }
}
