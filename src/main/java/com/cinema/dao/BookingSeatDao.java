package com.cinema.dao;

import com.cinema.model.BookingSeat;
import com.cinema.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingSeatDao {

    public List<BookingSeat> getByBookingId(int bookingId) {
        String sql = "SELECT booking_seat_id, booking_id, show_id, seat_number, seat_price, seat_type FROM BOOKING_SEAT WHERE booking_id = ?";
        List<BookingSeat> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load booking seats", e);
        }
        return list;
    }

    public List<BookingSeat> getTakenSeatsByShow(int showId) {
        String sql = "SELECT booking_seat_id, booking_id, show_id, seat_number, seat_price, seat_type FROM BOOKING_SEAT WHERE show_id = ?";
        List<BookingSeat> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, showId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load taken seats", e);
        }
        return list;
    }

    public List<String> getTakenSeatNumbersByShow(int showId) {
        String sql = "SELECT bs.seat_number FROM BOOKING_SEAT bs JOIN BOOKING b ON bs.booking_id = b.booking_id WHERE bs.show_id = ? AND b.status = 'CONFIRMED'";
        List<String> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, showId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("seat_number"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load taken seat numbers", e);
        }
        return list;
    }

    public List<String> getTakenSeatNumbersByShow(Connection conn, int showId) throws SQLException {
        String sql = "SELECT bs.seat_number FROM BOOKING_SEAT bs JOIN BOOKING b ON bs.booking_id = b.booking_id WHERE bs.show_id = ? AND b.status = 'CONFIRMED'";
        List<String> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, showId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("seat_number"));
                }
            }
        }
        return list;
    }

    public void insert(Connection conn, BookingSeat bs) throws SQLException {
        String sql = "INSERT INTO BOOKING_SEAT (booking_id, show_id, seat_number, seat_price, seat_type) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bs.getBookingId());
            ps.setInt(2, bs.getShowId());
            ps.setString(3, bs.getSeatNumber());
            ps.setBigDecimal(4, bs.getSeatPrice());
            ps.setString(5, bs.getSeatType());
            ps.executeUpdate();
        }
    }

    public void deleteByBookingId(int bookingId) {
        String sql = "DELETE FROM BOOKING_SEAT WHERE booking_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete booking seats", e);
        }
    }

    private BookingSeat map(ResultSet rs) throws SQLException {
        return new BookingSeat(
                rs.getInt("booking_seat_id"),
                rs.getInt("booking_id"),
                rs.getInt("show_id"),
                rs.getString("seat_number"),
                rs.getBigDecimal("seat_price"),
                rs.getString("seat_type")
        );
    }
}
