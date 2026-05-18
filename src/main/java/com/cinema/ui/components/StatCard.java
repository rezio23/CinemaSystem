package com.cinema.ui.components;

import com.cinema.util.Constants;

import javax.swing.*;
import java.awt.*;

public class StatCard extends JPanel {
    private final JLabel valueLabel;
    private final JLabel titleLabel;

    public StatCard(String title, String value, Color accentColor) {
        setLayout(new BorderLayout());
        setBackground(Constants.COLOR_CARD);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, accentColor),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        titleLabel = new JLabel(title);
        titleLabel.setFont(Constants.FONT_SMALL);
        titleLabel.setForeground(Constants.COLOR_TEXT_MUTED);

        valueLabel = new JLabel(value);
        valueLabel.setFont(Constants.FONT_HEADER);
        valueLabel.setForeground(Constants.COLOR_TEXT);

        add(titleLabel, BorderLayout.NORTH);
        add(valueLabel, BorderLayout.CENTER);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }
}
