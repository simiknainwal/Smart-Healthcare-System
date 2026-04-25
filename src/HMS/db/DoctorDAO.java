package HMS.db;

import HMS.model.Doctor;
import HMS.utils.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DoctorDAO — Data Access Object for the "doctors" table.
 *
 * Handles all database operations (CRUD) for Doctor records.
 */
public class DoctorDAO {

    // ==================== CREATE ====================

    /**
     * Inserts a new doctor into the database.
     */
    public void insert(Doctor d) {
        String sql = "INSERT INTO doctors (id, name, age, specialization) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, d.getId());
            pstmt.setString(2, d.getName());
            pstmt.setInt(3, d.getAge());
            pstmt.setString(4, d.getSpecialization());
            pstmt.executeUpdate();

            Logger.info("Doctor inserted: " + d.getId() + " (" + d.getName() + ")");

        } catch (SQLException e) {
            Logger.error("Failed to insert doctor " + d.getId() + ": " + e.getMessage());
            System.err.println("Database error (insert doctor): " + e.getMessage());
        }
    }

    // ==================== READ ====================

    /**
     * Retrieves all doctors from the database.
     */
    public List<Doctor> getAll() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Doctor d = new Doctor(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getInt("age"),
                    rs.getString("specialization")
                );
                doctors.add(d);
            }

        } catch (SQLException e) {
            Logger.error("Failed to load doctors: " + e.getMessage());
            System.err.println("Database error (load doctors): " + e.getMessage());
        }

        return doctors;
    }

    /**
     * Finds a single doctor by their ID.
     */
    public Doctor getById(String id) {
        String sql = "SELECT * FROM doctors WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Doctor(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getInt("age"),
                    rs.getString("specialization")
                );
            }

        } catch (SQLException e) {
            Logger.error("Failed to find doctor " + id + ": " + e.getMessage());
            System.err.println("Database error (find doctor): " + e.getMessage());
        }

        return null;
    }

    // ==================== UPDATE ====================

    /**
     * Updates an existing doctor's details in the database.
     */
    public void update(Doctor d) {
        String sql = "UPDATE doctors SET name = ?, age = ?, specialization = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, d.getName());
            pstmt.setInt(2, d.getAge());
            pstmt.setString(3, d.getSpecialization());
            pstmt.setString(4, d.getId());
            pstmt.executeUpdate();

            Logger.info("Doctor updated: " + d.getId() + " (" + d.getName() + ")");

        } catch (SQLException e) {
            Logger.error("Failed to update doctor " + d.getId() + ": " + e.getMessage());
            System.err.println("Database error (update doctor): " + e.getMessage());
        }
    }

    // ==================== DELETE ====================

    /**
     * Deletes a doctor from the database by their ID.
     */
    public void delete(String id) {
        String sql = "DELETE FROM doctors WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            pstmt.executeUpdate();

            Logger.info("Doctor deleted: " + id);

        } catch (SQLException e) {
            Logger.error("Failed to delete doctor " + id + ": " + e.getMessage());
            System.err.println("Database error (delete doctor): " + e.getMessage());
        }
    }
}
