package com.cinema.ui;

import com.cinema.ui.components.SidebarButton;
import com.cinema.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    private final JPanel contentPanel;
    private final CardLayout cardLayout;
    private final Map<String, SidebarButton> navButtons = new HashMap<>();

    public MainFrame() {
        setTitle("Cinema Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Constants.COLOR_BACKGROUND);

        // Sidebar
        JPanel sidebar = createSidebar();
        root.add(sidebar, BorderLayout.WEST);

        // Content area
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Constants.COLOR_BACKGROUND);

        // Panels
        contentPanel.add(new DashboardPanel(), "DASHBOARD");
        contentPanel.add(new BookingPanel(), "BOOKING");
        contentPanel.add(new MoviePanel(), "MOVIES");
        contentPanel.add(new HallPanel(), "HALLS");
        contentPanel.add(new StaffPanel(), "STAFF");
        contentPanel.add(new CustomerPanel(), "CUSTOMERS");
        contentPanel.add(new ShowPanel(), "SHOWS");
        contentPanel.add(new ReportsPanel(), "REPORTS");

        root.add(contentPanel, BorderLayout.CENTER);
        setContentPane(root);

        switchPanel("DASHBOARD");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Constants.COLOR_PRIMARY);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel logo = new JLabel("🎬 Cinema System");
        logo.setFont(Constants.FONT_HEADER);
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        sidebar.add(logo);

        addNavButton(sidebar, "Dashboard", "📊", "DASHBOARD");
        addNavButton(sidebar, "Sell Tickets", "🎟", "BOOKING");
        addNavButton(sidebar, "Movies", "🎬", "MOVIES");
        addNavButton(sidebar, "Halls", "🏠", "HALLS");
        addNavButton(sidebar, "Staff", "💼", "STAFF");
        addNavButton(sidebar, "Customers", "👥", "CUSTOMERS");
        addNavButton(sidebar, "Shows", "📅", "SHOWS");
        addNavButton(sidebar, "Reports", "📊", "REPORTS");

        sidebar.add(Box.createVerticalGlue());

        JLabel footer = new JLabel("v1.0.0 | Final Project");
        footer.setFont(Constants.FONT_SMALL);
        footer.setForeground(new Color(200, 220, 240));
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(footer);

        return sidebar;
    }

    private void addNavButton(JPanel sidebar, String text, String icon, String name) {
        SidebarButton btn = new SidebarButton(text, icon);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(e -> switchPanel(name));
        navButtons.put(name, btn);
        sidebar.add(btn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
    }

    private void switchPanel(String name) {
        cardLayout.show(contentPanel, name);
        for (Map.Entry<String, SidebarButton> entry : navButtons.entrySet()) {
            entry.getValue().setActive(entry.getKey().equals(name));
        }
        // Refresh data when switching
        Component comp = contentPanel.getComponent(0);
        for (Component c : contentPanel.getComponents()) {
            if (c.isVisible()) {
                comp = c;
                break;
            }
        }
        if (comp instanceof Refreshable) {
            ((Refreshable) comp).refreshData();
        }
    }

    public interface Refreshable {
        void refreshData();
    }
}
