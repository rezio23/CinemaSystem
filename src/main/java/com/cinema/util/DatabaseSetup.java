package com.cinema.util;

import com.cinema.ui.dialog.AppDialog;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseSetup {

    public static boolean checkAndSetup(Component parent) {
        // 1. Test basic connection
        try (Connection conn = DBConnection.getConnection()) {
            if (!conn.isValid(5)) {
                showConnectionError(parent, "Could not connect to Oracle.\nPlease check that Oracle is running and db.properties is configured correctly.");
                return false;
            }
        } catch (SQLException e) {
            showConnectionError(parent, "Database connection failed:\n" + e.getMessage() + "\n\nPlease verify:\n- Oracle is running\n- db.properties has correct credentials\n- The service name (e.g., ORCL) is correct");
            return false;
        }

        // 2. Check if tables exist
        try (Connection conn = DBConnection.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getTables(null, null, "MOVIE", new String[]{"TABLE"})) {
                if (rs.next()) {
                    return true; // tables exist
                }
            }
        } catch (SQLException e) {
            showConnectionError(parent, "Could not verify tables:\n" + e.getMessage());
            return false;
        }

        // 3. Tables missing — offer to set up
        int choice = JOptionPane.showOptionDialog(
            parent,
            "Database tables were not found.\n\nDo you want to run the automatic database setup now?\nThis will create all tables and insert sample data.",
            "Database Setup Required",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            new String[]{"Run Setup", "Cancel"},
            "Run Setup"
        );

        if (choice != JOptionPane.YES_OPTION) {
            return false;
        }

        // 4. Run setup in a background thread with progress dialog
        return runSetupWithProgress(parent);
    }

    private static boolean runSetupWithProgress(Component parent) {
        JDialog progressDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "Setting up database...", true);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setString("Creating tables and inserting data...");
        progressBar.setStringPainted(true);
        progressDialog.add(BorderLayout.CENTER, progressBar);
        progressDialog.setSize(400, 100);
        progressDialog.setLocationRelativeTo(parent);
        progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        final boolean[] success = {false};
        final String[] errorMsg = {null};

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    success[0] = executeSetupScript();
                } catch (Exception e) {
                    errorMsg[0] = e.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                progressDialog.dispose();
            }
        };

        worker.execute();
        progressDialog.setVisible(true);

        if (!success[0]) {
            String msg = errorMsg[0] != null ? errorMsg[0] : "Setup failed for an unknown reason.";
            AppDialog.showMessage(parent,
                "Database setup failed:\n" + msg + "\n\nPlease run setup_oracle.sql manually in SQL Developer or SQL*Plus.",
                "Setup Failed",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        AppDialog.showMessage(parent,
            "Database setup completed successfully!\nAll tables and sample data have been created.",
            "Setup Complete",
            JOptionPane.INFORMATION_MESSAGE);
        return true;
    }

    private static boolean executeSetupScript() throws Exception {
        // Read setup_oracle.sql from the project root
        File file = new File("setup_oracle.sql");
        if (!file.exists()) {
            // Try from working directory parent
            file = new File("../setup_oracle.sql");
        }
        if (!file.exists()) {
            throw new FileNotFoundException("setup_oracle.sql not found. Make sure it is in the project root directory.");
        }

        String sql = readFile(file);
        List<String> statements = splitStatements(sql);

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();

            for (String s : statements) {
                String trimmed = s.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("--")) continue;
                if (trimmed.toUpperCase().startsWith("SELECT")) continue; // skip verification query

                try {
                    stmt.execute(trimmed);
                } catch (SQLException e) {
                    // Ignore "table does not exist" errors on DROP
                    if (trimmed.toUpperCase().startsWith("DROP") && e.getErrorCode() == 942) {
                        continue;
                    }
                    conn.rollback();
                    throw e;
                }
            }

            conn.commit();
        }
        return true;
    }

    private static String readFile(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    private static List<String> splitStatements(String sql) {
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inString = false;

        for (int i = 0; i < sql.length(); i++) {
            char c = sql.charAt(i);
            current.append(c);

            if (c == '\'') {
                inString = !inString;
            }

            if (!inString && c == ';') {
                statements.add(current.toString());
                current.setLength(0);
            }
        }

        if (current.length() > 0) {
            statements.add(current.toString());
        }

        return statements;
    }

    private static void showConnectionError(Component parent, String message) {
        AppDialog.showMessage(parent, message, "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}
