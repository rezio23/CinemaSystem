package com.cinema.dao;

import com.cinema.model.Customer;
import com.cinema.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDao {

    public List<Customer> getAll() {
        String sql = "SELECT customer_id, full_name, email, phone, created_at FROM CUSTOMER ORDER BY customer_id";
        List<Customer> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load customers", e);
        }
        return list;
    }

    public Customer getById(int id) {
        String sql = "SELECT customer_id, full_name, email, phone, created_at FROM CUSTOMER WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load customer", e);
        }
        return null;
    }

    public List<Customer> search(String keyword) {
        String sql = "SELECT customer_id, full_name, email, phone, created_at FROM CUSTOMER " +
                     "WHERE LOWER(full_name) LIKE ? OR LOWER(email) LIKE ? OR phone LIKE ? ORDER BY full_name";
        List<Customer> list = new ArrayList<>();
        String pattern = "%" + keyword.toLowerCase() + "%";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search customers", e);
        }
        return list;
    }

    public int insert(Customer c) {
        String sql = "INSERT INTO CUSTOMER (full_name, email, phone, created_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getFullName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getPhone());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert customer", e);
        }
        return -1;
    }

    public void update(Customer c) {
        String sql = "UPDATE CUSTOMER SET full_name = ?, email = ?, phone = ? WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getFullName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getPhone());
            ps.setInt(4, c.getCustomerId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update customer", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM CUSTOMER WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete customer", e);
        }
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM CUSTOMER";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count customers", e);
        }
        return 0;
    }

    private Customer map(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("created_at");
        return new Customer(
                rs.getInt("customer_id"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("phone"),
                ts != null ? ts.toLocalDateTime() : null
        );
    }
}
