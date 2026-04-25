package HMS.db;

import HMS.model.Patient;
import HMS.utils.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PatientDAO — Data Access Object for the "patients" table.
 *
 * This class handles all database operations (CRUD) for Patient records.
 * Each method opens its own connection, executes the query, and closes the connection.
 *
 * CRUD = Create, Read, Update, Delete
 */
public class PatientDAO {

    // ==================== CREATE ====================

    /**
     * Inserts a new patient into the database.
     *
     * @param p The Patient object to insert
     */
    public void insert(Patient p) {
        String sql = "INSERT INTO patients (id, name, age, disease) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, p.getId());
            pstmt.setString(2, p.getName());
            pstmt.setInt(3, p.getAge());
            pstmt.setString(4, p.getDisease());
            pstmt.executeUpdate();

            Logger.info("Patient inserted: " + p.getId() + " (" + p.getName() + ")");

        } catch (SQLException e) {
            Logger.error("Failed to insert patient " + p.getId() + ": " + e.getMessage());
            System.err.println("Database error (insert patient): " + e.getMessage());
        }
    }

    // ==================== READ ====================

    /**
     * Retrieves all patients from the database.
     *
     * @return A list of all Patient objects
     */
    public List<Patient> getAll() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Patient p = new Patient(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getInt("age"),
                    rs.getString("disease")
                );
                patients.add(p);
            }

        } catch (SQLException e) {
            Logger.error("Failed to load patients: " + e.getMessage());
            System.err.println("Database error (load patients): " + e.getMessage());
        }

        return patients;
    }

    /**
     * Finds a single patient by their ID.
     *
     * @param id The patient ID (e.g., "PAT001")
     * @return The Patient object, or null if not found
     */
    public Patient getById(String id) {
        String sql = "SELECT * FROM patients WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Patient(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getInt("age"),
                    rs.getString("disease")
                );
            }

        } catch (SQLException e) {
            Logger.error("Failed to find patient " + id + ": " + e.getMessage());
            System.err.println("Database error (find patient): " + e.getMessage());
        }

        return null;
    }

    // ==================== UPDATE ====================

    /**
     * Updates an existing patient's details in the database.
     *
     * @param p The Patient object with updated values
     */
    public void update(Patient p) {
        String sql = "UPDATE patients SET name = ?, age = ?, disease = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, p.getName());
            pstmt.setInt(2, p.getAge());
            pstmt.setString(3, p.getDisease());
            pstmt.setString(4, p.getId());
            pstmt.executeUpdate();

            Logger.info("Patient updated: " + p.getId() + " (" + p.getName() + ")");

        } catch (SQLException e) {
            Logger.error("Failed to update patient " + p.getId() + ": " + e.getMessage());
            System.err.println("Database error (update patient): " + e.getMessage());
        }
    }

    // ==================== DELETE ====================

    /**
     * Deletes a patient from the database by their ID.
     *
     * @param id The patient ID to delete
     */
    public void delete(String id) {
        String sql = "DELETE FROM patients WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            pstmt.executeUpdate();

            Logger.info("Patient deleted: " + id);

        } catch (SQLException e) {
            Logger.error("Failed to delete patient " + id + ": " + e.getMessage());
            System.err.println("Database error (delete patient): " + e.getMessage());
        }
    }
}
