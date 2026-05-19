package com.cinema.dao;

import com.cinema.model.Booking;
import com.cinema.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingDao {

    public List<Booking> getAll() {
        String sql =
            "SELECT b.booking_id, b.customer_id, b.show_id, b.staff_id, b.booking_date, " +
            "b.payment_method, b.total_amount, b.status, " +
            "c.full_name AS customer_name, m.title AS movie_title, h.hall_name, " +
            "ms.show_datetime, st.full_name AS staff_name " +
            "FROM BOOKING b " +
            "JOIN CUSTOMER c ON b.customer_id = c.customer_id " +
            "JOIN MOVIE_SHOW ms ON b.show_id = ms.show_id " +
            "JOIN MOVIE m ON ms.movie_id = m.movie_id " +
            "JOIN HALL h ON ms.hall_id = h.hall_id " +
            "LEFT JOIN STAFF st ON b.staff_id = st.staff_id " +
            "ORDER BY b.booking_date DESC";
        List<Booking> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load bookings", e);
        }
        return list;
    }

    public List<Booking> getByCustomerId(int customerId) {
        String sql =
            "SELECT b.booking_id, b.customer_id, b.show_id, b.staff_id, b.booking_date, " +
            "b.payment_method, b.total_amount, b.status, " +
            "c.full_name AS customer_name, m.title AS movie_title, h.hall_name, " +
            "ms.show_datetime, st.full_name AS staff_name " +
            "FROM BOOKING b " +
            "JOIN CUSTOMER c ON b.customer_id = c.customer_id " +
            "JOIN MOVIE_SHOW ms ON b.show_id = ms.show_id " +
            "JOIN MOVIE m ON ms.movie_id = m.movie_id " +
            "JOIN HALL h ON ms.hall_id = h.hall_id " +
            "LEFT JOIN STAFF st ON b.staff_id = st.staff_id " +
            "WHERE b.customer_id = ? ORDER BY b.booking_date DESC";
        List<Booking> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load customer bookings", e);
        }
        return list;
    }

    public Booking getById(int id) {
        String sql =
            "SELECT b.booking_id, b.customer_id, b.show_id, b.staff_id, b.booking_date, " +
            "b.payment_method, b.total_amount, b.status, " +
            "c.full_name AS customer_name, m.title AS movie_title, h.hall_name, " +
            "ms.show_datetime, st.full_name AS staff_name " +
            "FROM BOOKING b " +
            "JOIN CUSTOMER c ON b.customer_id = c.customer_id " +
            "JOIN MOVIE_SHOW ms ON b.show_id = ms.show_id " +
            "JOIN MOVIE m ON ms.movie_id = m.movie_id " +
            "JOIN HALL h ON ms.hall_id = h.hall_id " +
            "LEFT JOIN STAFF st ON b.staff_id = st.staff_id " +
            "WHERE b.booking_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load booking", e);
        }
        return null;
    }

    public int insert(Connection conn, Booking b) throws SQLException {
        String sql = "INSERT INTO BOOKING (customer_id, show_id, staff_id, booking_date, payment_method, total_amount, status) " +
                     "VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, ?, 'CONFIRMED')";
        try (PreparedStatement ps = conn.prepareStatement(sql, new String[]{"booking_id"})) {
            ps.setInt(1, b.getCustomerId());
            ps.setInt(2, b.getShowId());
            ps.setInt(3, b.getStaffId());
            ps.setString(4, b.getPaymentMethod());
            ps.setBigDecimal(5, b.getTotalAmount());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public void updateStatus(int bookingId, String status) {
        String sql = "UPDATE BOOKING SET status = ? WHERE booking_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, bookingId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update booking status", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM BOOKING WHERE booking_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete booking", e);
        }
    }

    public int countConfirmed() {
        String sql = "SELECT COUNT(*) FROM BOOKING WHERE status = 'CONFIRMED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count bookings", e);
        }
        return 0;
    }

    public int countTicketsSold() {
        String sql = "SELECT COUNT(*) FROM BOOKING_SEAT bs JOIN BOOKING b ON bs.booking_id = b.booking_id WHERE b.status = 'CONFIRMED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count tickets", e);
        }
        return 0;
    }

    public double getRevenueToday() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM BOOKING WHERE status = 'CONFIRMED' AND TRUNC(booking_date) = TRUNC(CURRENT_TIMESTAMP)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get today's revenue", e);
        }
        return 0;
    }

    public Map<String, Integer> countByStatus() {
        String sql = "SELECT status, COUNT(*) FROM BOOKING GROUP BY status";
        Map<String, Integer> map = new HashMap<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString(1), rs.getInt(2));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count by status", e);
        }
        return map;
    }

    private Booking map(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setBookingId(rs.getInt("booking_id"));
        b.setCustomerId(rs.getInt("customer_id"));
        b.setShowId(rs.getInt("show_id"));
        b.setStaffId(rs.getInt("staff_id"));
        Timestamp ts = rs.getTimestamp("booking_date");
        b.setBookingDate(ts != null ? ts.toLocalDateTime() : null);
        b.setPaymentMethod(rs.getString("payment_method"));
        b.setTotalAmount(rs.getBigDecimal("total_amount"));
        b.setStatus(rs.getString("status"));
        b.setCustomerName(rs.getString("customer_name"));
        b.setMovieTitle(rs.getString("movie_title"));
        b.setHallName(rs.getString("hall_name"));
        Timestamp sts = rs.getTimestamp("show_datetime");
        b.setShowDateTime(sts != null ? sts.toLocalDateTime() : null);
        b.setStaffName(rs.getString("staff_name"));
        return b;
    }
}
