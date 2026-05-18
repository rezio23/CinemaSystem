package com.cinema;

import com.cinema.ui.MainFrame;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Modern dark Look and Feel
            UIManager.setLookAndFeel(new FlatDarkLaf());

            // Global font
            Font defaultFont = new Font("Segoe UI", Font.PLAIN, 14);
            UIManager.put("defaultFont", defaultFont);

            // Rounded corners everywhere FlatLaf supports
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("ScrollPane.arc", 10);
            UIManager.put("ProgressBar.arc", 10);
            UIManager.put("Spinner.arc", 10);
            UIManager.put("Slider.trackWidth", 4);

            // Panel / viewport backgrounds
            UIManager.put("Panel.background", new Color(0x1E1E2D));
            UIManager.put("Viewport.background", new Color(0x1E1E2D));

            // Table styling: borderless, alternating rows, hover, cinematic header
            UIManager.put("Table.background", new Color(0x27273A));
            UIManager.put("Table.alternateRowColor", new Color(0x2B2B40));
            UIManager.put("Table.selectionBackground", new Color(0x3B82F6));
            UIManager.put("Table.selectionForeground", Color.WHITE);
            UIManager.put("Table.hoverBackground", new Color(0x32324A));
            UIManager.put("Table.hoverForeground", new Color(0xE2E8F0));
            UIManager.put("Table.showHorizontalLines", false);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("Table.intercellSpacing", new Dimension(0, 0));
            UIManager.put("Table.rowHeight", 40);
            UIManager.put("Table.cellMargins", new Insets(8, 12, 8, 12));

            // Table header
            UIManager.put("TableHeader.background", new Color(0x1E1E2D));
            UIManager.put("TableHeader.foreground", new Color(0x94A3B8));
            UIManager.put("TableHeader.bottomSeparatorColor", new Color(0x3F3F5F));
            UIManager.put("TableHeader.height", 40);
            UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 13));

            // Buttons: primary cinematic blue
            UIManager.put("Button.background", new Color(0x3B82F6));
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.hoverBackground", new Color(0x2563EB));
            UIManager.put("Button.pressedBackground", new Color(0x1D4ED8));
            UIManager.put("Button.default.background", new Color(0x3B82F6));
            UIManager.put("Button.default.foreground", Color.WHITE);
            UIManager.put("Button.default.hoverBackground", new Color(0x2563EB));
            UIManager.put("Button.default.pressedBackground", new Color(0x1D4ED8));

            // Text fields / combos / lists
            UIManager.put("TextField.background", new Color(0x2B2B40));
            UIManager.put("TextField.foreground", new Color(0xE2E8F0));
            UIManager.put("FormattedTextField.background", new Color(0x2B2B40));
            UIManager.put("PasswordField.background", new Color(0x2B2B40));
            UIManager.put("ComboBox.background", new Color(0x2B2B40));
            UIManager.put("ComboBox.editableBackground", new Color(0x2B2B40));
            UIManager.put("ComboBox.selectionBackground", new Color(0x3B82F6));
            UIManager.put("ComboBox.selectionForeground", Color.WHITE);
            UIManager.put("List.background", new Color(0x27273A));
            UIManager.put("List.selectionBackground", new Color(0x3B82F6));
            UIManager.put("List.selectionForeground", Color.WHITE);

            // Scrollbars
            UIManager.put("ScrollBar.track", new Color(0x1E1E2D));
            UIManager.put("ScrollBar.thumb", new Color(0x3F3F5F));
            UIManager.put("ScrollBar.width", 8);
            UIManager.put("ScrollBar.showButtons", false);

            // Titled borders
            UIManager.put("TitledBorder.titleColor", new Color(0xE2E8F0));
            UIManager.put("TitledBorder.border", BorderFactory.createEmptyBorder());

            // Split pane divider
            UIManager.put("SplitPaneDivider.background", new Color(0x1E1E2D));
            UIManager.put("SplitPaneDivider.draggingColor", new Color(0x3B82F6));

            // Menus / popups
            UIManager.put("MenuBar.background", new Color(0x1E1E2D));
            UIManager.put("PopupMenu.background", new Color(0x27273A));
            UIManager.put("MenuItem.selectionBackground", new Color(0x3B82F6));
            UIManager.put("MenuItem.selectionForeground", Color.WHITE);

        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
