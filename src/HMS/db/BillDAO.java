package HMS.db;

import HMS.model.Bill;
import HMS.utils.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BillDAO — Data Access Object for the "bills" table.
 * Handles all database operations (CRUD) for Bill records.
 */
public class BillDAO {

    // ==================== CREATE ====================

    /**
     * Inserts a new bill into the database.
     */
    public void insert(Bill b) {
        String sql = "INSERT INTO bills (id, patient_id, amount, description, date, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, b.getId());
            pstmt.setString(2, b.getPatientId());
            pstmt.setDouble(3, b.getAmount());
            pstmt.setString(4, b.getDescription());
            pstmt.setString(5, b.getDate());
            pstmt.setString(6, b.getStatus());
            pstmt.executeUpdate();

            Logger.info("Bill inserted: " + b.getId()
                    + " (Patient: " + b.getPatientId() + ", Amount: " + b.getAmount() + ")");

        } catch (SQLException e) {
            Logger.error("Failed to insert bill " + b.getId() + ": " + e.getMessage());
            System.err.println("Database error (insert bill): " + e.getMessage());
        }
    }

    // ==================== READ ====================

    /**
     * Retrieves all bills from the database.
     */
    public List<Bill> getAll() {
        List<Bill> list = new ArrayList<>();
        String sql = "SELECT * FROM bills";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            Logger.error("Failed to load bills: " + e.getMessage());
            System.err.println("Database error (load bills): " + e.getMessage());
        }

        return list;
    }

    /**
     * Returns all bills for a specific patient.
     */
    public List<Bill> getByPatientId(String patientId) {
        List<Bill> list = new ArrayList<>();
        String sql = "SELECT * FROM bills WHERE patient_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            Logger.error("Failed to load bills for patient " + patientId + ": " + e.getMessage());
            System.err.println("Database error (bills by patient): " + e.getMessage());
        }

        return list;
    }

    // ==================== UPDATE ====================

    /**
     * Updates an existing bill's status in the database.
     * Primarily used to mark a bill as PAID.
     */
    public void update(Bill b) {
        String sql = "UPDATE bills SET status = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, b.getStatus());
            pstmt.setString(2, b.getId());
            pstmt.executeUpdate();

            Logger.info("Bill updated: " + b.getId() + " (Status: " + b.getStatus() + ")");

        } catch (SQLException e) {
            Logger.error("Failed to update bill " + b.getId() + ": " + e.getMessage());
            System.err.println("Database error (update bill): " + e.getMessage());
        }
    }

    // ==================== HELPER ====================

    /**
     * Maps a single ResultSet row to a Bill object.
     */
    private Bill mapRow(ResultSet rs) throws SQLException {
        return new Bill(
                rs.getString("id"),
                rs.getString("patient_id"),
                rs.getDouble("amount"),
                rs.getString("description"),
                rs.getString("date"),
                rs.getString("status")
        );
    }
}
