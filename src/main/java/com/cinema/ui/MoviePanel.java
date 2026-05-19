package com.cinema.ui;

import com.cinema.dao.MovieDao;
import com.cinema.model.Movie;
import com.cinema.ui.components.SearchField;
import com.cinema.ui.components.StyledButton;
import com.cinema.ui.dialog.FormDialog;
import com.cinema.util.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MoviePanel extends JPanel implements MainFrame.Refreshable {

    private final MovieDao dao = new MovieDao();
    private final DefaultTableModel model;
    private final JTable table;
    private final SearchField searchField;

    public MoviePanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Constants.COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);

        JLabel header = new JLabel("Movie Management");
        header.setFont(Constants.FONT_HEADER);
        header.setForeground(Constants.COLOR_TEXT);
        north.add(header, BorderLayout.NORTH);

        JPanel top = new JPanel(new BorderLayout(5, 0));
        top.setOpaque(false);
        searchField = new SearchField("Search by title or genre...", this::search);
        top.add(searchField, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setOpaque(false);
        StyledButton addBtn = new StyledButton("+ Add Movie", StyledButton.Variant.SUCCESS);
        addBtn.addActionListener(e -> addMovie());

        StyledButton editBtn = new StyledButton("Edit", StyledButton.Variant.SECONDARY);
        editBtn.addActionListener(e -> editMovie());

        StyledButton delBtn = new StyledButton("Delete", StyledButton.Variant.DANGER);
        delBtn.addActionListener(e -> deleteMovie());

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(delBtn);
        top.add(btnPanel, BorderLayout.EAST);
        north.add(top, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Title", "Genre", "Director", "Duration", "Rating", "Year"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setFont(Constants.FONT_BODY);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);

        refreshData();
    }

    @Override
    public void refreshData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override protected Void doInBackground() {
                loadTable(dao.getAll());
                return null;
            }
        };
        worker.execute();
    }

    private void loadTable(List<Movie> movies) {
        SwingUtilities.invokeLater(() -> {
            model.setRowCount(0);
            for (Movie m : movies) {
                model.addRow(new Object[]{m.getMovieId(), m.getTitle(), m.getGenre(), m.getDirector(), m.getDurationMin(), m.getRating(), m.getReleaseYear()});
            }
        });
    }

    private void search() {
        String keyword = searchField.getText().trim();
        loadTable(keyword.isEmpty() ? dao.getAll() : dao.search(keyword));
    }

    private void addMovie() {
        Movie m = showForm(null);
        if (m != null) {
            try {
                dao.insert(m);
                refreshData();
                JOptionPane.showMessageDialog(this, "Movie added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editMovie() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) model.getValueAt(row, 0);
        Movie existing = dao.getById(id);
        Movie updated = showForm(existing);
        if (updated != null) {
            try {
                updated.setMovieId(id);
                dao.update(updated);
                refreshData();
                JOptionPane.showMessageDialog(this, "Movie updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteMovie() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this movie?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dao.delete(id);
                refreshData();
                JOptionPane.showMessageDialog(this, "Movie deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Movie showForm(Movie m) {
        JTextField title = new JTextField(m != null ? m.getTitle() : "");
        JTextField genre = new JTextField(m != null ? m.getGenre() : "");
        JTextField director = new JTextField(m != null ? m.getDirector() : "");
        JTextField duration = new JTextField(m != null ? String.valueOf(m.getDurationMin()) : "");
        JTextField rating = new JTextField(m != null ? m.getRating() : "");
        JTextField year = new JTextField(m != null ? String.valueOf(m.getReleaseYear()) : "");

        FormDialog dlg = new FormDialog(SwingUtilities.getWindowAncestor(this), m == null ? "Add Movie" : "Edit Movie", 6);
        dlg.addField("Title:", title);
        dlg.addField("Genre:", genre);
        dlg.addField("Director:", director);
        dlg.addField("Duration (min):", duration);
        dlg.addField("Rating:", rating);
        dlg.addField("Year:", year);
        dlg.setVisible(true);

        if (dlg.isConfirmed()) {
            try {
                Movie movie = new Movie();
                movie.setTitle(title.getText().trim());
                movie.setGenre(genre.getText().trim());
                movie.setDirector(director.getText().trim());
                movie.setDurationMin(Integer.parseInt(duration.getText().trim()));
                movie.setRating(rating.getText().trim());
                movie.setReleaseYear(Integer.parseInt(year.getText().trim()));
                return movie;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }
}
