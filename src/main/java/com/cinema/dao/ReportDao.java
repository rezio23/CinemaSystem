package com.cinema.dao;

import com.cinema.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportDao {

    public List<Map<String, Object>> revenuePerMovie() {
        return revenuePerMovie(null, null);
    }

    public List<Map<String, Object>> revenuePerMovie(LocalDate from, LocalDate to) {
        StringBuilder innerSql = new StringBuilder(
            "SELECT ms.movie_id, bs.seat_price, bs.booking_seat_id " +
            "FROM MOVIE_SHOW ms " +
            "JOIN BOOKING b ON ms.show_id = b.show_id AND b.status = 'CONFIRMED' " +
            "JOIN BOOKING_SEAT bs ON b.booking_id = bs.booking_id");
        List<Object> params = new ArrayList<>();
        boolean hasFrom = from != null;
        boolean hasTo = to != null;

        if (hasFrom || hasTo) {
            innerSql.append(" WHERE 1=1 ");
            if (hasFrom) {
                innerSql.append("AND TRUNC(b.booking_date) >= ? ");
                params.add(java.sql.Date.valueOf(from));
            }
            if (hasTo) {
                innerSql.append("AND TRUNC(b.booking_date) <= ? ");
                params.add(java.sql.Date.valueOf(to));
            }
        }

        String sql = "SELECT m.movie_id, m.title, COALESCE(SUM(r.seat_price), 0) AS total_revenue, COUNT(r.booking_seat_id) AS tickets_sold " +
                     "FROM MOVIE m LEFT JOIN (" + innerSql + ") r ON m.movie_id = r.movie_id " +
                     "GROUP BY m.movie_id, m.title ORDER BY total_revenue DESC";

        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("movieId", rs.getInt("movie_id"));
                    row.put("title", rs.getString("title"));
                    row.put("totalRevenue", rs.getBigDecimal("total_revenue"));
                    row.put("ticketsSold", rs.getInt("tickets_sold"));
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load revenue per movie", e);
        }
        return list;
    }

    public List<Map<String, Object>> seatsSoldPerShow() {
        return seatsSoldPerShow(null, null);
    }

    public List<Map<String, Object>> seatsSoldPerShow(LocalDate from, LocalDate to) {
        StringBuilder sql = new StringBuilder(
            "SELECT ms.show_id, m.title AS movie_title, h.hall_name, ms.show_datetime, " +
            "COUNT(bs.booking_seat_id) AS seats_sold, h.capacity, " +
            "ROUND(COUNT(bs.booking_seat_id) * 100.0 / NULLIF(h.capacity, 0), 2) AS occupancy_rate " +
            "FROM MOVIE_SHOW ms " +
            "JOIN MOVIE m ON ms.movie_id = m.movie_id " +
            "JOIN HALL h ON ms.hall_id = h.hall_id " +
            "LEFT JOIN BOOKING b ON ms.show_id = b.show_id AND b.status = 'CONFIRMED' " +
            "LEFT JOIN BOOKING_SEAT bs ON b.booking_id = bs.booking_id " +
            "WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        if (from != null) {
            sql.append("AND TRUNC(ms.show_datetime) >= ? ");
            params.add(java.sql.Date.valueOf(from));
        }
        if (to != null) {
            sql.append("AND TRUNC(ms.show_datetime) <= ? ");
            params.add(java.sql.Date.valueOf(to));
        }
        sql.append("GROUP BY ms.show_id, m.title, h.hall_name, ms.show_datetime, h.capacity ORDER BY ms.show_datetime");

        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("showId", rs.getInt("show_id"));
                    row.put("movieTitle", rs.getString("movie_title"));
                    row.put("hallName", rs.getString("hall_name"));
                    Timestamp ts = rs.getTimestamp("show_datetime");
                    row.put("showDateTime", ts != null ? ts.toLocalDateTime() : null);
                    row.put("seatsSold", rs.getInt("seats_sold"));
                    row.put("capacity", rs.getInt("capacity"));
                    row.put("occupancyRate", rs.getDouble("occupancy_rate"));
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load seats sold per show", e);
        }
        return list;
    }

    public List<Map<String, Object>> dailyRevenueAnalysis() {
        return dailyRevenueAnalysis(null, null);
    }

    public List<Map<String, Object>> dailyRevenueAnalysis(LocalDate from, LocalDate to) {
        StringBuilder sql = new StringBuilder(
            "SELECT TRUNC(booking_date) AS day, COUNT(*) AS bookings, COALESCE(SUM(total_amount), 0) AS revenue " +
            "FROM BOOKING WHERE status = 'CONFIRMED' ");
        List<Object> params = new ArrayList<>();
        if (from != null) {
            sql.append("AND TRUNC(booking_date) >= ? ");
            params.add(java.sql.Date.valueOf(from));
        }
        if (to != null) {
            sql.append("AND TRUNC(booking_date) <= ? ");
            params.add(java.sql.Date.valueOf(to));
        }
        sql.append("GROUP BY TRUNC(booking_date) ORDER BY day DESC");

        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("day", rs.getDate("day"));
                    row.put("bookings", rs.getInt("bookings"));
                    row.put("revenue", rs.getBigDecimal("revenue"));
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load daily revenue", e);
        }
        return list;
    }

    public List<Map<String, Object>> weeklyRevenueAnalysis() {
        return weeklyRevenueAnalysis(null, null);
    }

    public List<Map<String, Object>> weeklyRevenueAnalysis(LocalDate from, LocalDate to) {
        StringBuilder sql = new StringBuilder(
            "SELECT TO_CHAR(TRUNC(booking_date, 'IW'), 'YYYY-MM-DD') AS week_start, " +
            "COUNT(*) AS bookings, COALESCE(SUM(total_amount), 0) AS revenue " +
            "FROM BOOKING WHERE status = 'CONFIRMED' ");
        List<Object> params = new ArrayList<>();
        if (from != null) {
            sql.append("AND TRUNC(booking_date) >= ? ");
            params.add(java.sql.Date.valueOf(from));
        }
        if (to != null) {
            sql.append("AND TRUNC(booking_date) <= ? ");
            params.add(java.sql.Date.valueOf(to));
        }
        sql.append("GROUP BY TRUNC(booking_date, 'IW') ORDER BY week_start DESC");

        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("weekStart", rs.getString("week_start"));
                    row.put("bookings", rs.getInt("bookings"));
                    row.put("revenue", rs.getBigDecimal("revenue"));
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load weekly revenue", e);
        }
        return list;
    }

    public List<Map<String, Object>> monthlyRevenueAnalysis() {
        return monthlyRevenueAnalysis(null, null);
    }

    public List<Map<String, Object>> monthlyRevenueAnalysis(LocalDate from, LocalDate to) {
        StringBuilder sql = new StringBuilder(
            "SELECT TO_CHAR(TRUNC(booking_date, 'MM'), 'YYYY-MM') AS month, " +
            "COUNT(*) AS bookings, COALESCE(SUM(total_amount), 0) AS revenue " +
            "FROM BOOKING WHERE status = 'CONFIRMED' ");
        List<Object> params = new ArrayList<>();
        if (from != null) {
            sql.append("AND TRUNC(booking_date) >= ? ");
            params.add(java.sql.Date.valueOf(from));
        }
        if (to != null) {
            sql.append("AND TRUNC(booking_date) <= ? ");
            params.add(java.sql.Date.valueOf(to));
        }
        sql.append("GROUP BY TRUNC(booking_date, 'MM') ORDER BY month DESC");

        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("month", rs.getString("month"));
                    row.put("bookings", rs.getInt("bookings"));
                    row.put("revenue", rs.getBigDecimal("revenue"));
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load monthly revenue", e);
        }
        return list;
    }

    public List<Map<String, Object>> yearlyRevenueAnalysis() {
        return yearlyRevenueAnalysis(null, null);
    }

    public List<Map<String, Object>> yearlyRevenueAnalysis(LocalDate from, LocalDate to) {
        StringBuilder sql = new StringBuilder(
            "SELECT TO_CHAR(TRUNC(booking_date, 'YYYY'), 'YYYY') AS year, " +
            "COUNT(*) AS bookings, COALESCE(SUM(total_amount), 0) AS revenue " +
            "FROM BOOKING WHERE status = 'CONFIRMED' ");
        List<Object> params = new ArrayList<>();
        if (from != null) {
            sql.append("AND TRUNC(booking_date) >= ? ");
            params.add(java.sql.Date.valueOf(from));
        }
        if (to != null) {
            sql.append("AND TRUNC(booking_date) <= ? ");
            params.add(java.sql.Date.valueOf(to));
        }
        sql.append("GROUP BY TRUNC(booking_date, 'YYYY') ORDER BY year DESC");

        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("year", rs.getString("year"));
                    row.put("bookings", rs.getInt("bookings"));
                    row.put("revenue", rs.getBigDecimal("revenue"));
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load yearly revenue", e);
        }
        return list;
    }

    public List<Map<String, Object>> customerBookingHistory(int customerId) {
        String sql =
            "SELECT b.booking_id, b.booking_date, b.total_amount, b.status, b.payment_method, " +
            "m.title AS movie_title, h.hall_name, ms.show_datetime, " +
            "LISTAGG(bs.seat_number, ', ') WITHIN GROUP (ORDER BY bs.seat_number) AS seats " +
            "FROM BOOKING b " +
            "JOIN MOVIE_SHOW ms ON b.show_id = ms.show_id " +
            "JOIN MOVIE m ON ms.movie_id = m.movie_id " +
            "JOIN HALL h ON ms.hall_id = h.hall_id " +
            "LEFT JOIN BOOKING_SEAT bs ON b.booking_id = bs.booking_id " +
            "WHERE b.customer_id = ? " +
            "GROUP BY b.booking_id, b.booking_date, b.total_amount, b.status, b.payment_method, " +
            "m.title, h.hall_name, ms.show_datetime " +
            "ORDER BY b.booking_date DESC";
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("bookingId", rs.getInt("booking_id"));
                    Timestamp ts = rs.getTimestamp("booking_date");
                    row.put("bookingDate", ts != null ? ts.toLocalDateTime() : null);
                    row.put("totalAmount", rs.getBigDecimal("total_amount"));
                    row.put("status", rs.getString("status"));
                    row.put("paymentMethod", rs.getString("payment_method"));
                    row.put("movieTitle", rs.getString("movie_title"));
                    row.put("hallName", rs.getString("hall_name"));
                    Timestamp sts = rs.getTimestamp("show_datetime");
                    row.put("showDateTime", sts != null ? sts.toLocalDateTime() : null);
                    row.put("seats", rs.getString("seats"));
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load customer history", e);
        }
        return list;
    }

    public double getOverallOccupancyRate() {
        String sql =
            "SELECT ROUND(COUNT(bs.booking_seat_id) * 100.0 / NULLIF(SUM(h.capacity), 0), 2) " +
            "FROM MOVIE_SHOW ms " +
            "JOIN HALL h ON ms.hall_id = h.hall_id " +
            "LEFT JOIN BOOKING b ON ms.show_id = b.show_id AND b.status = 'CONFIRMED' " +
            "LEFT JOIN BOOKING_SEAT bs ON b.booking_id = bs.booking_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get occupancy rate", e);
        }
        return 0;
    }
}
