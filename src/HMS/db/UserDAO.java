package HMS.db;

import HMS.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO — Handles all database operations for the users table.
 *
 * Methods:
 *   insert()          — save a new user (signup)
 *   findByUsername()   — look up a user for login
 *   findByLinkedId()  — check if a patient/doctor already has an account
 *   getAll()          — list all users (admin utility)
 *   delete()          — remove a user account (admin utility)
 */
public class UserDAO {

    /** Insert a new user into the database. */
    public void insert(User user) {
        String sql = "INSERT INTO users (username, password_hash, role, linked_id, created_at) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getRole());
            ps.setString(4, user.getLinkedId());
            ps.setString(5, user.getCreatedAt());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[UserDAO] Insert failed: " + e.getMessage());
        }
    }

    /** Find a user by username. Returns null if not found. */
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] findByUsername failed: " + e.getMessage());
        }
        return null;
    }

    /** Find a user by their linked patient/doctor ID. Returns null if not found. */
    public User findByLinkedId(String linkedId) {
        String sql = "SELECT * FROM users WHERE linked_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, linkedId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] findByLinkedId failed: " + e.getMessage());
        }
        return null;
    }

    /** Get all user accounts. */
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] getAll failed: " + e.getMessage());
        }
        return users;
    }

    /** Delete a user by username. */
    public void delete(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[UserDAO] Delete failed: " + e.getMessage());
        }
    }

    /** Update user's credentials (username and password). */
    public void updateCredentials(String oldUsername, String newUsername, String newPasswordHash) {
        String sql = "UPDATE users SET username = ?, password_hash = ? WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newUsername);
            ps.setString(2, newPasswordHash);
            ps.setString(3, oldUsername);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[UserDAO] Update credentials failed: " + e.getMessage());
        }
    }

    /** Helper: maps a ResultSet row to a User object. */
    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
            rs.getString("username"),
            rs.getString("password_hash"),
            rs.getString("role"),
            rs.getString("linked_id"),
            rs.getString("created_at")
        );
    }
}
