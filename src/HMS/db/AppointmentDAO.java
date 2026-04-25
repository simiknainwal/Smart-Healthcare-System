package HMS.db;

import HMS.model.Appointment;
import HMS.utils.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AppointmentDAO — Data Access Object for the "appointments" table.
 *
 * Handles all database operations (CRUD) for Appointment records.
 */
public class AppointmentDAO {

    // ==================== CREATE ====================

    /**
     * Inserts a new appointment into the database.
     */
    public void insert(Appointment a) {
        String sql = "INSERT INTO appointments (id, patient_id, doctor_id, date, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, a.getId());
            pstmt.setString(2, a.getPatientId());
            pstmt.setString(3, a.getDoctorId());
            pstmt.setString(4, a.getDate());
            pstmt.setString(5, a.getStatus());
            pstmt.executeUpdate();

            Logger.info("Appointment inserted: " + a.getId()
                    + " (Patient: " + a.getPatientId() + " → Doctor: " + a.getDoctorId() + ")");

        } catch (SQLException e) {
            Logger.error("Failed to insert appointment " + a.getId() + ": " + e.getMessage());
            System.err.println("Database error (insert appointment): " + e.getMessage());
        }
    }

    // ==================== READ ====================

    /**
     * Retrieves all appointments from the database.
     */
    public List<Appointment> getAll() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Appointment a = new Appointment(
                    rs.getString("id"),
                    rs.getString("patient_id"),
                    rs.getString("doctor_id"),
                    rs.getString("date")
                );
                a.setStatus(rs.getString("status"));
                appointments.add(a);
            }

        } catch (SQLException e) {
            Logger.error("Failed to load appointments: " + e.getMessage());
            System.err.println("Database error (load appointments): " + e.getMessage());
        }

        return appointments;
    }

    /**
     * Finds a single appointment by its ID.
     */
    public Appointment getById(String id) {
        String sql = "SELECT * FROM appointments WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Appointment a = new Appointment(
                    rs.getString("id"),
                    rs.getString("patient_id"),
                    rs.getString("doctor_id"),
                    rs.getString("date")
                );
                a.setStatus(rs.getString("status"));
                return a;
            }

        } catch (SQLException e) {
            Logger.error("Failed to find appointment " + id + ": " + e.getMessage());
            System.err.println("Database error (find appointment): " + e.getMessage());
        }

        return null;
    }

    // ==================== UPDATE ====================

    /**
     * Updates an existing appointment in the database.
     * Typically used to change the status or reschedule the date.
     */
    public void update(Appointment a) {
        String sql = "UPDATE appointments SET patient_id = ?, doctor_id = ?, date = ?, status = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, a.getPatientId());
            pstmt.setString(2, a.getDoctorId());
            pstmt.setString(3, a.getDate());
            pstmt.setString(4, a.getStatus());
            pstmt.setString(5, a.getId());
            pstmt.executeUpdate();

            Logger.info("Appointment updated: " + a.getId() + " (Status: " + a.getStatus() + ")");

        } catch (SQLException e) {
            Logger.error("Failed to update appointment " + a.getId() + ": " + e.getMessage());
            System.err.println("Database error (update appointment): " + e.getMessage());
        }
    }

    // ==================== DELETE ====================

    /**
     * Deletes an appointment from the database by its ID.
     */
    public void delete(String id) {
        String sql = "DELETE FROM appointments WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            pstmt.executeUpdate();

            Logger.info("Appointment deleted: " + id);

        } catch (SQLException e) {
            Logger.error("Failed to delete appointment " + id + ": " + e.getMessage());
            System.err.println("Database error (delete appointment): " + e.getMessage());
        }
    }
}
