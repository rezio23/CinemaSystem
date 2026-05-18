package com.cinema.dao;

import com.cinema.model.Movie;
import com.cinema.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDao {

    public List<Movie> getAll() {
        String sql = "SELECT movie_id, title, genre, director, duration_min, rating, release_year FROM MOVIE ORDER BY movie_id";
        List<Movie> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load movies", e);
        }
        return list;
    }

    public Movie getById(int id) {
        String sql = "SELECT movie_id, title, genre, director, duration_min, rating, release_year FROM MOVIE WHERE movie_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load movie", e);
        }
        return null;
    }

    public List<Movie> search(String keyword) {
        String sql = "SELECT movie_id, title, genre, director, duration_min, rating, release_year FROM MOVIE " +
                     "WHERE LOWER(title) LIKE ? OR LOWER(genre) LIKE ? ORDER BY title";
        List<Movie> list = new ArrayList<>();
        String pattern = "%" + keyword.toLowerCase() + "%";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search movies", e);
        }
        return list;
    }

    public int insert(Movie m) {
        String sql = "INSERT INTO MOVIE (title, genre, director, duration_min, rating, release_year) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.getTitle());
            ps.setString(2, m.getGenre());
            ps.setString(3, m.getDirector());
            ps.setInt(4, m.getDurationMin());
            ps.setString(5, m.getRating());
            ps.setInt(6, m.getReleaseYear());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert movie", e);
        }
        return -1;
    }

    public void update(Movie m) {
        String sql = "UPDATE MOVIE SET title = ?, genre = ?, director = ?, duration_min = ?, rating = ?, release_year = ? WHERE movie_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getTitle());
            ps.setString(2, m.getGenre());
            ps.setString(3, m.getDirector());
            ps.setInt(4, m.getDurationMin());
            ps.setString(5, m.getRating());
            ps.setInt(6, m.getReleaseYear());
            ps.setInt(7, m.getMovieId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update movie", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM MOVIE WHERE movie_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete movie", e);
        }
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM MOVIE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count movies", e);
        }
        return 0;
    }

    private Movie map(ResultSet rs) throws SQLException {
        return new Movie(
                rs.getInt("movie_id"),
                rs.getString("title"),
                rs.getString("genre"),
                rs.getString("director"),
                rs.getInt("duration_min"),
                rs.getString("rating"),
                rs.getInt("release_year")
        );
    }
}
