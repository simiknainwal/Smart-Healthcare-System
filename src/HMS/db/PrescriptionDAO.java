package HMS.db;

import HMS.model.Prescription;
import HMS.utils.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PrescriptionDAO — Data Access Object for the "prescriptions" table.
 * Handles all database operations (CRUD) for Prescription records.
 */
public class PrescriptionDAO {

    // ==================== CREATE ====================

    /**
     * Inserts a new prescription into the database.
     */
    public void insert(Prescription p) {
        String sql = "INSERT INTO prescriptions (id, patient_id, doctor_id, medication, dosage, date) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, p.getId());
            pstmt.setString(2, p.getPatientId());
            pstmt.setString(3, p.getDoctorId());
            pstmt.setString(4, p.getMedication());
            pstmt.setString(5, p.getDosage());
            pstmt.setString(6, p.getDate());
            pstmt.executeUpdate();

            Logger.info("Prescription inserted: " + p.getId()
                    + " (Patient: " + p.getPatientId() + ", Doctor: " + p.getDoctorId() + ")");

        } catch (SQLException e) {
            Logger.error("Failed to insert prescription " + p.getId() + ": " + e.getMessage());
            System.err.println("Database error (insert prescription): " + e.getMessage());
        }
    }

    // ==================== READ ====================

    /**
     * Retrieves all prescriptions from the database.
     */
    public List<Prescription> getAll() {
        List<Prescription> list = new ArrayList<>();
        String sql = "SELECT * FROM prescriptions";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            Logger.error("Failed to load prescriptions: " + e.getMessage());
            System.err.println("Database error (load prescriptions): " + e.getMessage());
        }

        return list;
    }

    /**
     * Returns all prescriptions for a specific patient.
     */
    public List<Prescription> getByPatientId(String patientId) {
        List<Prescription> list = new ArrayList<>();
        String sql = "SELECT * FROM prescriptions WHERE patient_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            Logger.error("Failed to load prescriptions for patient " + patientId + ": " + e.getMessage());
            System.err.println("Database error (prescriptions by patient): " + e.getMessage());
        }

        return list;
    }

    /**
     * Returns all prescriptions written by a specific doctor.
     */
    public List<Prescription> getByDoctorId(String doctorId) {
        List<Prescription> list = new ArrayList<>();
        String sql = "SELECT * FROM prescriptions WHERE doctor_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, doctorId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            Logger.error("Failed to load prescriptions for doctor " + doctorId + ": " + e.getMessage());
            System.err.println("Database error (prescriptions by doctor): " + e.getMessage());
        }

        return list;
    }

    // ==================== HELPER ====================

    /**
     * Maps a single ResultSet row to a Prescription object.
     * Centralised to avoid repeating column names in every method.
     */
    private Prescription mapRow(ResultSet rs) throws SQLException {
        return new Prescription(
                rs.getString("id"),
                rs.getString("patient_id"),
                rs.getString("doctor_id"),
                rs.getString("medication"),
                rs.getString("dosage"),
                rs.getString("date")
        );
    }
}
