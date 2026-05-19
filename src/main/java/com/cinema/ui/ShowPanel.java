package com.cinema.ui;

import com.cinema.dao.HallDao;
import com.cinema.dao.MovieDao;
import com.cinema.dao.MovieShowDao;
import com.cinema.model.Hall;
import com.cinema.model.Movie;
import com.cinema.model.MovieShow;
import com.cinema.ui.dialog.AppDialog;
import com.cinema.ui.dialog.FormDialog;
import com.cinema.util.Constants;

import com.cinema.ui.components.StyledButton;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ShowPanel extends JPanel implements MainFrame.Refreshable {

    private final MovieShowDao showDao = new MovieShowDao();
    private final MovieDao movieDao = new MovieDao();
    private final HallDao hallDao = new HallDao();

    private final DefaultTableModel model;
    private final JTable table;

    public ShowPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Constants.COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel north = new JPanel(new BorderLayout(0, Constants.PAGE_HEADER_GAP));
        north.setOpaque(false);

        JLabel header = new JLabel("Show Management");
        header.setFont(Constants.FONT_HEADER);
        header.setForeground(Constants.COLOR_TEXT);
        north.add(header, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setOpaque(false);
        StyledButton addBtn = new StyledButton("+ Add Show", StyledButton.Variant.SUCCESS);
        addBtn.addActionListener(e -> addShow());

        StyledButton editBtn = new StyledButton("Edit", StyledButton.Variant.SECONDARY);
        editBtn.addActionListener(e -> editShow());

        StyledButton delBtn = new StyledButton("Delete", StyledButton.Variant.DANGER);
        delBtn.addActionListener(e -> deleteShow());

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(delBtn);
        north.add(btnPanel, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Movie", "Hall", "Date/Time", "Base Price"}, 0) {
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
        SwingUtilities.invokeLater(() -> {
            model.setRowCount(0);
            List<MovieShow> list = showDao.getAll();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (MovieShow s : list) {
                model.addRow(new Object[]{s.getShowId(), s.getMovieTitle(), s.getHallName(),
                        s.getShowDateTime() != null ? s.getShowDateTime().format(dtf) : "",
                        "$" + s.getBasePrice()});
            }
        });
    }

    private void addShow() {
        MovieShow s = showForm(null);
        if (s != null) {
            try {
                Movie movie = movieDao.getById(s.getMovieId());
                int duration = movie != null && movie.getDurationMin() > 0 ? movie.getDurationMin() : 150;
                if (showDao.hasOverlap(s.getHallId(), s.getShowDateTime(), s.getShowDateTime().plusMinutes(duration), null)) {
                    AppDialog.showMessage(this, "There is already a show in this hall around that time.", "Overlap", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                showDao.insert(s);
                refreshData();
                AppDialog.showMessage(this, "Show added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                AppDialog.showMessage(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editShow() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) model.getValueAt(row, 0);
        MovieShow existing = showDao.getById(id);
        MovieShow updated = showForm(existing);
        if (updated != null) {
            try {
                updated.setShowId(id);
                Movie movie = movieDao.getById(updated.getMovieId());
                int duration = movie != null && movie.getDurationMin() > 0 ? movie.getDurationMin() : 150;
                if (showDao.hasOverlap(updated.getHallId(), updated.getShowDateTime(), updated.getShowDateTime().plusMinutes(duration), id)) {
                    AppDialog.showMessage(this, "There is already a show in this hall around that time.", "Overlap", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                showDao.update(updated);
                refreshData();
                AppDialog.showMessage(this, "Show updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                AppDialog.showMessage(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteShow() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) model.getValueAt(row, 0);
        int confirm = AppDialog.showConfirm(this, "Delete this show?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                showDao.delete(id);
                refreshData();
                AppDialog.showMessage(this, "Show deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                AppDialog.showMessage(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private MovieShow showForm(MovieShow s) {
        List<Movie> movies = movieDao.getAll();
        List<Hall> halls = hallDao.getAll();

        JComboBox<Movie> movieCombo = new JComboBox<>();
        for (Movie m : movies) movieCombo.addItem(m);

        JComboBox<Hall> hallCombo = new JComboBox<>();
        for (Hall h : halls) hallCombo.addItem(h);

        JTextField dateTime = new JTextField(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        JTextField price = new JTextField("10.00");

        if (s != null) {
            for (int i = 0; i < movieCombo.getItemCount(); i++) {
                if (movieCombo.getItemAt(i).getMovieId() == s.getMovieId()) {
                    movieCombo.setSelectedIndex(i);
                    break;
                }
            }
            for (int i = 0; i < hallCombo.getItemCount(); i++) {
                if (hallCombo.getItemAt(i).getHallId() == s.getHallId()) {
                    hallCombo.setSelectedIndex(i);
                    break;
                }
            }
            dateTime.setText(s.getShowDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            price.setText(s.getBasePrice().toString());
        }

        FormDialog dlg = new FormDialog(SwingUtilities.getWindowAncestor(this), s == null ? "Add Show" : "Edit Show", 4);
        dlg.addField("Movie:", movieCombo);
        dlg.addField("Hall:", hallCombo);
        dlg.addField("Date/Time:", dateTime);
        dlg.addField("Base Price:", price);
        dlg.setVisible(true);

        if (dlg.isConfirmed()) {
            try {
                MovieShow show = new MovieShow();
                show.setMovieId(((Movie) movieCombo.getSelectedItem()).getMovieId());
                show.setHallId(((Hall) hallCombo.getSelectedItem()).getHallId());
                show.setShowDateTime(LocalDateTime.parse(dateTime.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                show.setBasePrice(new BigDecimal(price.getText().trim()));
                return show;
            } catch (Exception ex) {
                AppDialog.showMessage(this, "Invalid date or price format. Use yyyy-MM-dd HH:mm", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }
}
