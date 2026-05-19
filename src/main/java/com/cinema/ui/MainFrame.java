package com.cinema.ui;

import com.cinema.ui.components.SidebarButton;
import com.cinema.ui.components.SidebarIcon;
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
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Panels
        contentPanel.add(new DashboardPanel(), "DASHBOARD");
        contentPanel.add(new BookingPanel(), "BOOKING");
        contentPanel.add(new BookingsPanel(), "BOOKINGS");
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
        sidebar.setBackground(Constants.COLOR_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(24, 16, 24, 16));

        // Logo area
        JLabel logoIcon = new JLabel("🎬");
        logoIcon.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        logoIcon.setForeground(Constants.COLOR_PRIMARY);
        logoIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel logoText = new JLabel("Cinema System");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logoText.setForeground(Constants.COLOR_TEXT);
        logoText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel logoWrap = new JPanel();
        logoWrap.setLayout(new BoxLayout(logoWrap, BoxLayout.Y_AXIS));
        logoWrap.setOpaque(false);
        logoWrap.add(logoIcon);
        logoWrap.add(Box.createRigidArea(new Dimension(0, 8)));
        logoWrap.add(logoText);
        logoWrap.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoWrap.setBorder(BorderFactory.createEmptyBorder(0, 0, 32, 0));
        sidebar.add(logoWrap);

        addNavButton(sidebar, "Dashboard", SidebarIcon.Type.DASHBOARD, "DASHBOARD");
        addNavButton(sidebar, "Sell Tickets", SidebarIcon.Type.TICKETS, "BOOKING");
        addNavButton(sidebar, "Bookings", SidebarIcon.Type.BOOKINGS, "BOOKINGS");
        addNavButton(sidebar, "Movies", SidebarIcon.Type.MOVIES, "MOVIES");
        addNavButton(sidebar, "Halls", SidebarIcon.Type.HALLS, "HALLS");
        addNavButton(sidebar, "Staff", SidebarIcon.Type.STAFF, "STAFF");
        addNavButton(sidebar, "Customers", SidebarIcon.Type.CUSTOMERS, "CUSTOMERS");
        addNavButton(sidebar, "Shows", SidebarIcon.Type.SHOWS, "SHOWS");
        addNavButton(sidebar, "Reports", SidebarIcon.Type.REPORTS, "REPORTS");

        sidebar.add(Box.createVerticalGlue());

        JLabel footer = new JLabel("v1.0.0 | Final Project");
        footer.setFont(Constants.FONT_SMALL);
        footer.setForeground(Constants.COLOR_TEXT_MUTED);
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(footer);

        return sidebar;
    }

    private void addNavButton(JPanel sidebar, String text, SidebarIcon.Type iconType, String name) {
        SidebarButton btn = new SidebarButton(text, new SidebarIcon(iconType));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(e -> switchPanel(name));
        navButtons.put(name, btn);
        sidebar.add(btn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 6)));
    }

    private void switchPanel(String name) {
        cardLayout.show(contentPanel, name);
        for (Map.Entry<String, SidebarButton> entry : navButtons.entrySet()) {
            entry.getValue().setActive(entry.getKey().equals(name));
        }
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
