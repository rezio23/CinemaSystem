package com.cinema;

import com.cinema.ui.MainFrame;
import com.cinema.util.DatabaseSetup;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Modern light Look and Feel
            UIManager.setLookAndFeel(new FlatLightLaf());

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
            UIManager.put("Panel.background", new Color(0xF5F7FA));
            UIManager.put("Viewport.background", new Color(0xF5F7FA));

            // Table styling: borderless, alternating rows, hover, cinematic header
            UIManager.put("Table.background", new Color(0xFFFFFF));
            UIManager.put("Table.alternateRowColor", new Color(0xE8EDF2));
            UIManager.put("Table.selectionBackground", new Color(0x1A237E));
            UIManager.put("Table.selectionForeground", Color.WHITE);
            UIManager.put("Table.hoverBackground", new Color(0xE0E6ED));
            UIManager.put("Table.hoverForeground", new Color(0x0D1B2A));
            UIManager.put("Table.showHorizontalLines", false);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("Table.intercellSpacing", new Dimension(0, 0));
            UIManager.put("Table.rowHeight", 40);
            UIManager.put("Table.cellMargins", new Insets(8, 12, 8, 12));

            // Table header
            UIManager.put("TableHeader.background", new Color(0xFFFFFF));
            UIManager.put("TableHeader.foreground", new Color(0x546E7A));
            UIManager.put("TableHeader.bottomSeparatorColor", new Color(0xE0E6ED));
            UIManager.put("TableHeader.height", 40);
            UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 13));

            // Buttons: navy
            UIManager.put("Button.background", new Color(0x1A237E));
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.hoverBackground", new Color(0x3949AB));
            UIManager.put("Button.pressedBackground", new Color(0x0D1B2A));
            UIManager.put("Button.default.background", new Color(0x1A237E));
            UIManager.put("Button.default.foreground", Color.WHITE);
            UIManager.put("Button.default.hoverBackground", new Color(0x3949AB));
            UIManager.put("Button.default.pressedBackground", new Color(0x0D1B2A));

            // Text fields / combos / lists
            UIManager.put("TextField.background", new Color(0xE8EDF2));
            UIManager.put("TextField.foreground", new Color(0x0D1B2A));
            UIManager.put("FormattedTextField.background", new Color(0xE8EDF2));
            UIManager.put("PasswordField.background", new Color(0xE8EDF2));
            UIManager.put("ComboBox.background", new Color(0xE8EDF2));
            UIManager.put("ComboBox.editableBackground", new Color(0xE8EDF2));
            UIManager.put("ComboBox.selectionBackground", new Color(0x1A237E));
            UIManager.put("ComboBox.selectionForeground", Color.WHITE);
            UIManager.put("List.background", new Color(0xFFFFFF));
            UIManager.put("List.selectionBackground", new Color(0x1A237E));
            UIManager.put("List.selectionForeground", Color.WHITE);

            // Scrollbars
            UIManager.put("ScrollBar.track", new Color(0xF5F7FA));
            UIManager.put("ScrollBar.thumb", new Color(0xD0D9E2));
            UIManager.put("ScrollBar.width", 8);
            UIManager.put("ScrollBar.showButtons", false);

            // Titled borders
            UIManager.put("TitledBorder.titleColor", new Color(0x0D1B2A));
            UIManager.put("TitledBorder.border", BorderFactory.createEmptyBorder());

            // Split pane divider
            UIManager.put("SplitPaneDivider.background", new Color(0xF5F7FA));
            UIManager.put("SplitPaneDivider.draggingColor", new Color(0x1A237E));

            // Menus / popups
            UIManager.put("MenuBar.background", new Color(0xF5F7FA));
            UIManager.put("PopupMenu.background", new Color(0xFFFFFF));
            UIManager.put("MenuItem.selectionBackground", new Color(0x1A237E));
            UIManager.put("MenuItem.selectionForeground", Color.WHITE);

        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            if (!DatabaseSetup.checkAndSetup(null)) {
                System.exit(1);
            }
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
