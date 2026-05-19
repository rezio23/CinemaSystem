package com.cinema.ui.dialog;

import com.cinema.ui.components.RoundedPanel;
import com.cinema.ui.components.StyledButton;
import com.cinema.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class AppDialog {
    private static final int MIN_WIDTH = 420;
    private static final int ARC = 18;

    private AppDialog() {}

    public static void showMessage(Component parent, String message, String title, int messageType) {
        showOptions(parent, title, createMessagePanel(message, messageType),
                new DialogAction("OK", JOptionPane.OK_OPTION, StyledButton.Variant.PRIMARY, true));
    }

    public static void showContent(Component parent, JComponent content, String title, int messageType) {
        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.add(content, BorderLayout.CENTER);
        showOptions(parent, title, body,
                new DialogAction("OK", JOptionPane.OK_OPTION, StyledButton.Variant.PRIMARY, true));
    }

    public static int showConfirm(Component parent, String message, String title, int optionType) {
        return showConfirm(parent, createMessagePanel(message, JOptionPane.QUESTION_MESSAGE), title, optionType);
    }

    public static int showConfirm(Component parent, JComponent content, String title, int optionType) {
        if (optionType == JOptionPane.YES_NO_OPTION) {
            return showOptions(parent, title, content,
                    new DialogAction("No", JOptionPane.NO_OPTION, StyledButton.Variant.SECONDARY, false),
                    new DialogAction("Yes", JOptionPane.YES_OPTION, StyledButton.Variant.PRIMARY, true));
        }

        return showOptions(parent, title, content,
                new DialogAction("Cancel", JOptionPane.CANCEL_OPTION, StyledButton.Variant.SECONDARY, false),
                new DialogAction("OK", JOptionPane.OK_OPTION, StyledButton.Variant.PRIMARY, true));
    }

    public static JPanel createActionBar(JButton... buttons) {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bar.setOpaque(false);
        for (JButton button : buttons) {
            styleButtonSize(button);
            bar.add(button);
        }
        return bar;
    }

    public static void installShell(JDialog dialog, String title, JComponent content, JComponent actions, JButton defaultButton) {
        dialog.setUndecorated(true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setBackground(new Color(0, 0, 0, 0));

        RoundedPanel shell = new RoundedPanel(new BorderLayout(0, 18));
        shell.setArc(ARC);
        shell.setBackground(Constants.COLOR_CARD);
        shell.setBorder(BorderFactory.createCompoundBorder(
                Constants.createRoundedBorder(Constants.INPUT_BORDER_COLOR, 1, ARC),
                BorderFactory.createEmptyBorder(18, 22, 18, 22)
        ));

        shell.add(createTitleBar(dialog, title), BorderLayout.NORTH);
        shell.add(content, BorderLayout.CENTER);
        shell.add(actions, BorderLayout.SOUTH);
        dialog.setContentPane(shell);

        if (defaultButton != null) {
            dialog.getRootPane().setDefaultButton(defaultButton);
        }
        dialog.getRootPane().registerKeyboardAction(
                e -> dialog.dispose(),
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    public static void prepareAndCenter(JDialog dialog, Component parent, int minWidth) {
        dialog.pack();
        Dimension size = dialog.getSize();
        size.width = Math.max(size.width, minWidth);
        dialog.setSize(size);
        dialog.setLocationRelativeTo(parent);
    }

    private static int showOptions(Component parent, String title, JComponent content, DialogAction... actions) {
        Window owner = parent != null ? SwingUtilities.getWindowAncestor(parent) : null;
        JDialog dialog = owner != null
                ? new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL)
                : new JDialog((Frame) null, title, true);
        int[] result = {JOptionPane.CLOSED_OPTION};

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bar.setOpaque(false);
        JButton defaultButton = null;
        for (DialogAction action : actions) {
            StyledButton button = new StyledButton(action.text, action.variant);
            styleButtonSize(button);
            button.addActionListener(e -> {
                result[0] = action.value;
                dialog.dispose();
            });
            if (action.defaultAction) {
                defaultButton = button;
            }
            bar.add(button);
        }

        installShell(dialog, title, content, bar, defaultButton);
        prepareAndCenter(dialog, parent, MIN_WIDTH);
        dialog.setVisible(true);
        return result[0];
    }

    private static JPanel createTitleBar(JDialog dialog, String title) {
        JPanel titleBar = new JPanel(new BorderLayout(12, 0));
        titleBar.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Constants.FONT_SUBHEADER);
        titleLabel.setForeground(Constants.COLOR_TEXT);
        titleBar.add(titleLabel, BorderLayout.CENTER);

        JButton closeBtn = new JButton("X");
        closeBtn.setFont(Constants.FONT_BODY);
        closeBtn.setForeground(Constants.COLOR_TEXT_MUTED);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.setPreferredSize(new Dimension(32, 32));
        closeBtn.addActionListener(e -> dialog.dispose());
        titleBar.add(closeBtn, BorderLayout.EAST);

        installDrag(dialog, titleBar);
        installDrag(dialog, titleLabel);
        return titleBar;
    }

    private static JPanel createMessagePanel(String message, int messageType) {
        JPanel panel = new JPanel(new BorderLayout(14, 0));
        panel.setOpaque(false);
        panel.add(new DialogBadge(messageType), BorderLayout.WEST);

        JTextArea text = new JTextArea(message);
        text.setFont(Constants.FONT_BODY);
        text.setForeground(Constants.COLOR_TEXT);
        text.setOpaque(false);
        text.setEditable(false);
        text.setFocusable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setColumns(34);
        text.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        panel.add(text, BorderLayout.CENTER);
        return panel;
    }

    private static void styleButtonSize(JButton button) {
        Dimension preferred = button.getPreferredSize();
        preferred.width = Math.max(preferred.width, 96);
        preferred.height = Constants.INPUT_HEIGHT;
        button.setPreferredSize(preferred);
        button.setMinimumSize(preferred);
    }

    private static void installDrag(JDialog dialog, JComponent handle) {
        Point[] offset = new Point[1];
        handle.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                offset[0] = e.getPoint();
            }
        });
        handle.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (offset[0] == null) return;
                Point location = e.getLocationOnScreen();
                dialog.setLocation(location.x - offset[0].x, location.y - offset[0].y);
            }
        });
    }

    private static final class DialogAction {
        private final String text;
        private final int value;
        private final StyledButton.Variant variant;
        private final boolean defaultAction;

        private DialogAction(String text, int value, StyledButton.Variant variant, boolean defaultAction) {
            this.text = text;
            this.value = value;
            this.variant = variant;
            this.defaultAction = defaultAction;
        }
    }

    private static final class DialogBadge extends JComponent {
        private final int messageType;

        private DialogBadge(int messageType) {
            this.messageType = messageType;
            setPreferredSize(new Dimension(42, 42));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color color = switch (messageType) {
                case JOptionPane.ERROR_MESSAGE -> Constants.COLOR_DANGER;
                case JOptionPane.WARNING_MESSAGE -> Constants.COLOR_WARNING;
                case JOptionPane.QUESTION_MESSAGE -> Constants.COLOR_PRIMARY_LIGHT;
                default -> Constants.COLOR_PRIMARY;
            };
            String text = switch (messageType) {
                case JOptionPane.ERROR_MESSAGE -> "!";
                case JOptionPane.WARNING_MESSAGE -> "!";
                case JOptionPane.QUESTION_MESSAGE -> "?";
                default -> "i";
            };
            g2.setColor(color);
            g2.fillOval(0, 0, 42, 42);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
            FontMetrics fm = g2.getFontMetrics();
            int x = (42 - fm.stringWidth(text)) / 2;
            int y = (42 - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(text, x, y);
            g2.dispose();
        }
    }
}
