package HMS.db;

import HMS.model.Bed;
import HMS.utils.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BedDAO — Data Access Object for the "beds" table.
 *
 * Handles all database operations (CRUD) for Bed records.
 * The "occupied" field is stored as INTEGER (0 = false, 1 = true) in SQLite.
 */
public class BedDAO {

    // ==================== CREATE ====================

    /**
     * Inserts a new bed into the database.
     */
    public void insert(Bed b) {
        String sql = "INSERT INTO beds (id, ward_type, occupied, patient_id, admit_date, discharge_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, b.getId());
            pstmt.setString(2, b.getWardType());
            pstmt.setInt(3, b.isOccupied() ? 1 : 0);  // boolean → 0 or 1
            pstmt.setString(4, b.getPatientId());
            pstmt.setString(5, b.getAdmitDate());
            pstmt.setString(6, b.getDischargeDate());
            pstmt.executeUpdate();

            Logger.info("Bed inserted: " + b.getId() + " (" + b.getWardType() + ")");

        } catch (SQLException e) {
            Logger.error("Failed to insert bed " + b.getId() + ": " + e.getMessage());
            System.err.println("Database error (insert bed): " + e.getMessage());
        }
    }

    // ==================== READ ====================

    /**
     * Retrieves all beds from the database.
     */
    public List<Bed> getAll() {
        List<Bed> beds = new ArrayList<>();
        String sql = "SELECT * FROM beds";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                boolean occupied = rs.getInt("occupied") == 1;  // 0 or 1 → boolean
                Bed b = new Bed(
                    rs.getString("id"),
                    rs.getString("ward_type"),
                    occupied,
                    rs.getString("patient_id"),
                    rs.getString("admit_date"),
                    rs.getString("discharge_date")
                );
                beds.add(b);
            }

        } catch (SQLException e) {
            Logger.error("Failed to load beds: " + e.getMessage());
            System.err.println("Database error (load beds): " + e.getMessage());
        }

        return beds;
    }

    /**
     * Finds a single bed by its ID.
     */
    public Bed getById(String id) {
        String sql = "SELECT * FROM beds WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                boolean occupied = rs.getInt("occupied") == 1;
                return new Bed(
                    rs.getString("id"),
                    rs.getString("ward_type"),
                    occupied,
                    rs.getString("patient_id"),
                    rs.getString("admit_date"),
                    rs.getString("discharge_date")
                );
            }

        } catch (SQLException e) {
            Logger.error("Failed to find bed " + id + ": " + e.getMessage());
            System.err.println("Database error (find bed): " + e.getMessage());
        }

        return null;
    }

    // ==================== UPDATE ====================

    /**
     * Updates an existing bed in the database.
     * Used when booking a bed (occupied → true) or discharging (occupied → false).
     */
    public void update(Bed b) {
        String sql = "UPDATE beds SET ward_type = ?, occupied = ?, patient_id = ?, " +
                     "admit_date = ?, discharge_date = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, b.getWardType());
            pstmt.setInt(2, b.isOccupied() ? 1 : 0);
            pstmt.setString(3, b.getPatientId());
            pstmt.setString(4, b.getAdmitDate());
            pstmt.setString(5, b.getDischargeDate());
            pstmt.setString(6, b.getId());
            pstmt.executeUpdate();

            String status = b.isOccupied() ? "BOOKED (Patient: " + b.getPatientId() + ")" : "AVAILABLE";
            Logger.info("Bed updated: " + b.getId() + " → " + status);

        } catch (SQLException e) {
            Logger.error("Failed to update bed " + b.getId() + ": " + e.getMessage());
            System.err.println("Database error (update bed): " + e.getMessage());
        }
    }

    // ==================== DELETE ====================

    /**
     * Deletes a bed from the database by its ID.
     */
    public void delete(String id) {
        String sql = "DELETE FROM beds WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            pstmt.executeUpdate();

            Logger.info("Bed deleted: " + id);

        } catch (SQLException e) {
            Logger.error("Failed to delete bed " + id + ": " + e.getMessage());
            System.err.println("Database error (delete bed): " + e.getMessage());
        }
    }
}
