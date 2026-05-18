package com.cinema.dao;

import com.cinema.model.MovieShow;
import com.cinema.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MovieShowDao {

    public List<MovieShow> getAll() {
        String sql =
            "SELECT s.show_id, s.movie_id, s.hall_id, s.show_datetime, s.base_price, " +
            "m.title AS movie_title, h.hall_name, h.capacity " +
            "FROM MOVIE_SHOW s " +
            "JOIN MOVIE m ON s.movie_id = m.movie_id " +
            "JOIN HALL h ON s.hall_id = h.hall_id " +
            "ORDER BY s.show_datetime";
        List<MovieShow> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load shows", e);
        }
        return list;
    }

    public List<MovieShow> getUpcoming() {
        String sql =
            "SELECT s.show_id, s.movie_id, s.hall_id, s.show_datetime, s.base_price, " +
            "m.title AS movie_title, h.hall_name, h.capacity " +
            "FROM MOVIE_SHOW s " +
            "JOIN MOVIE m ON s.movie_id = m.movie_id " +
            "JOIN HALL h ON s.hall_id = h.hall_id " +
            "WHERE s.show_datetime >= CURRENT_TIMESTAMP " +
            "ORDER BY s.show_datetime";
        List<MovieShow> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load upcoming shows", e);
        }
        return list;
    }

    public MovieShow getById(int id) {
        String sql =
            "SELECT s.show_id, s.movie_id, s.hall_id, s.show_datetime, s.base_price, " +
            "m.title AS movie_title, h.hall_name, h.capacity " +
            "FROM MOVIE_SHOW s " +
            "JOIN MOVIE m ON s.movie_id = m.movie_id " +
            "JOIN HALL h ON s.hall_id = h.hall_id " +
            "WHERE s.show_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load show", e);
        }
        return null;
    }

    public boolean hasOverlap(int hallId, LocalDateTime start, LocalDateTime end, Integer excludeShowId) {
        // Use a 3-hour window on both sides as a safe overlap guard
        LocalDateTime searchStart = start.minusHours(3);
        LocalDateTime searchEnd = start.plusHours(3);
        String sql =
            "SELECT COUNT(*) FROM MOVIE_SHOW " +
            "WHERE hall_id = ? AND show_datetime BETWEEN ? AND ?" +
            (excludeShowId != null ? " AND show_id <> ?" : "");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, hallId);
            ps.setTimestamp(2, Timestamp.valueOf(searchStart));
            ps.setTimestamp(3, Timestamp.valueOf(searchEnd));
            if (excludeShowId != null) {
                ps.setInt(4, excludeShowId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check show overlap", e);
        }
        return false;
    }

    public int insert(MovieShow s) {
        String sql = "INSERT INTO MOVIE_SHOW (movie_id, hall_id, show_datetime, base_price) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, s.getMovieId());
            ps.setInt(2, s.getHallId());
            ps.setTimestamp(3, Timestamp.valueOf(s.getShowDateTime()));
            ps.setBigDecimal(4, s.getBasePrice());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert show", e);
        }
        return -1;
    }

    public void update(MovieShow s) {
        String sql = "UPDATE MOVIE_SHOW SET movie_id = ?, hall_id = ?, show_datetime = ?, base_price = ? WHERE show_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, s.getMovieId());
            ps.setInt(2, s.getHallId());
            ps.setTimestamp(3, Timestamp.valueOf(s.getShowDateTime()));
            ps.setBigDecimal(4, s.getBasePrice());
            ps.setInt(5, s.getShowId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update show", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM MOVIE_SHOW WHERE show_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete show", e);
        }
    }

    public int countUpcoming() {
        String sql = "SELECT COUNT(*) FROM MOVIE_SHOW WHERE show_datetime >= CURRENT_TIMESTAMP";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count upcoming shows", e);
        }
        return 0;
    }

    private MovieShow map(ResultSet rs) throws SQLException {
        MovieShow s = new MovieShow();
        s.setShowId(rs.getInt("show_id"));
        s.setMovieId(rs.getInt("movie_id"));
        s.setHallId(rs.getInt("hall_id"));
        Timestamp ts = rs.getTimestamp("show_datetime");
        s.setShowDateTime(ts != null ? ts.toLocalDateTime() : null);
        s.setBasePrice(rs.getBigDecimal("base_price"));
        s.setMovieTitle(rs.getString("movie_title"));
        s.setHallName(rs.getString("hall_name"));
        s.setHallCapacity(rs.getInt("capacity"));
        return s;
    }
}
