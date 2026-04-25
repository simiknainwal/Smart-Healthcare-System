package HMS.db;

import HMS.model.MedReport;
import HMS.utils.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MedReportDAO — Data Access Object for the "reports" table.
 * Handles all database operations (CRUD) for MedReport records.
 */
public class MedReportDAO {

    // ==================== CREATE ====================

    /**
     * Inserts a new medical report into the database.
     */
    public void insert(MedReport r) {
        String sql = "INSERT INTO reports (id, patient_id, doctor_id, type, content, date) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, r.getId());
            pstmt.setString(2, r.getPatientId());
            pstmt.setString(3, r.getDoctorId());
            pstmt.setString(4, r.getType());
            pstmt.setString(5, r.getContent());
            pstmt.setString(6, r.getDate());
            pstmt.executeUpdate();

            Logger.info("MedReport inserted: " + r.getId()
                    + " (Patient: " + r.getPatientId() + ", Type: " + r.getType() + ")");

        } catch (SQLException e) {
            Logger.error("Failed to insert report " + r.getId() + ": " + e.getMessage());
            System.err.println("Database error (insert report): " + e.getMessage());
        }
    }

    // ==================== READ ====================

    /**
     * Retrieves all medical reports from the database.
     */
    public List<MedReport> getAll() {
        List<MedReport> list = new ArrayList<>();
        String sql = "SELECT * FROM reports";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            Logger.error("Failed to load reports: " + e.getMessage());
            System.err.println("Database error (load reports): " + e.getMessage());
        }

        return list;
    }

    /**
     * Returns all medical reports for a specific patient.
     */
    public List<MedReport> getByPatientId(String patientId) {
        List<MedReport> list = new ArrayList<>();
        String sql = "SELECT * FROM reports WHERE patient_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            Logger.error("Failed to load reports for patient " + patientId + ": " + e.getMessage());
            System.err.println("Database error (reports by patient): " + e.getMessage());
        }

        return list;
    }

    // ==================== HELPER ====================

    /**
     * Maps a single ResultSet row to a MedReport object.
     */
    private MedReport mapRow(ResultSet rs) throws SQLException {
        return new MedReport(
                rs.getString("id"),
                rs.getString("patient_id"),
                rs.getString("doctor_id"),
                rs.getString("type"),
                rs.getString("content"),
                rs.getString("date")
        );
    }
}
