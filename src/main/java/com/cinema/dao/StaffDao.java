package com.cinema.dao;

import com.cinema.model.Staff;
import com.cinema.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDao {

    public List<Staff> getAll() {
        String sql = "SELECT staff_id, full_name, role, hire_date FROM STAFF ORDER BY staff_id";
        List<Staff> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load staff", e);
        }
        return list;
    }

    public Staff getById(int id) {
        String sql = "SELECT staff_id, full_name, role, hire_date FROM STAFF WHERE staff_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load staff", e);
        }
        return null;
    }

    public int insert(Staff s) {
        String sql = "INSERT INTO STAFF (full_name, role, hire_date) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getFullName());
            ps.setString(2, s.getRole());
            ps.setDate(3, Date.valueOf(s.getHireDate()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert staff", e);
        }
        return -1;
    }

    public void update(Staff s) {
        String sql = "UPDATE STAFF SET full_name = ?, role = ?, hire_date = ? WHERE staff_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getFullName());
            ps.setString(2, s.getRole());
            ps.setDate(3, Date.valueOf(s.getHireDate()));
            ps.setInt(4, s.getStaffId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update staff", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM STAFF WHERE staff_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete staff", e);
        }
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM STAFF";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count staff", e);
        }
        return 0;
    }

    private Staff map(ResultSet rs) throws SQLException {
        Date hd = rs.getDate("hire_date");
        return new Staff(
                rs.getInt("staff_id"),
                rs.getString("full_name"),
                rs.getString("role"),
                hd != null ? hd.toLocalDate() : null
        );
    }
}
