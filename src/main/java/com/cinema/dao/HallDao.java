package com.cinema.dao;

import com.cinema.model.Hall;
import com.cinema.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HallDao {

    public List<Hall> getAll() {
        String sql = "SELECT hall_id, hall_name, capacity, hall_type FROM HALL ORDER BY hall_id";
        List<Hall> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load halls", e);
        }
        return list;
    }

    public Hall getById(int id) {
        String sql = "SELECT hall_id, hall_name, capacity, hall_type FROM HALL WHERE hall_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load hall", e);
        }
        return null;
    }

    public int insert(Hall h) {
        String sql = "INSERT INTO HALL (hall_name, capacity, hall_type) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"hall_id"})) {
            ps.setString(1, h.getHallName());
            ps.setInt(2, h.getCapacity());
            ps.setString(3, h.getHallType());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert hall", e);
        }
        return -1;
    }

    public void update(Hall h) {
        String sql = "UPDATE HALL SET hall_name = ?, capacity = ?, hall_type = ? WHERE hall_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, h.getHallName());
            ps.setInt(2, h.getCapacity());
            ps.setString(3, h.getHallType());
            ps.setInt(4, h.getHallId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update hall", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM HALL WHERE hall_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete hall", e);
        }
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM HALL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count halls", e);
        }
        return 0;
    }

    private Hall map(ResultSet rs) throws SQLException {
        return new Hall(
                rs.getInt("hall_id"),
                rs.getString("hall_name"),
                rs.getInt("capacity"),
                rs.getString("hall_type")
        );
    }
}
