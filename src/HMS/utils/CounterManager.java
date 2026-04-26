package HMS.utils;

import HMS.db.DBConnection;

import java.sql.*;

/**
 * CounterManager — Generates unique auto-incrementing IDs for all entities.
 *
 * BEFORE: Read/wrote counters from counters.txt (file handling)
 * AFTER:  Read/writes counters from the "counters" table in SQLite (JDBC)
 *
 * The counters table stores:
 *   entity   | counter
 *   ---------+--------
 *   PATIENT  | 5
 *   DOCTOR   | 10
 *   ...
 */
public class CounterManager {

    private static int patientCounter = 0;
    private static int doctorCounter = 0;
    private static int appointmentCounter = 0;
    private static int bedCounter = 0;
    private static int prescriptionCounter = 0;
    private static int billCounter = 0;

    // Static initializer — runs once when the class is first used
    static {
        loadCounters();
    }

    /**
     * Loads all counter values from the database.
     * If the counters table is empty (first run), counters stay at 0.
     */
    private static void loadCounters() {
        String sql = "SELECT entity, counter FROM counters";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String entity = rs.getString("entity");
                int value = rs.getInt("counter");

                switch (entity) {
                    case "PATIENT":      patientCounter = value;      break;
                    case "DOCTOR":       doctorCounter = value;       break;
                    case "APPOINTMENT":  appointmentCounter = value;  break;
                    case "BED":          bedCounter = value;          break;
                    case "PRESCRIPTION": prescriptionCounter = value; break;
                    case "BILL":         billCounter = value;         break;
                }
            }

        } catch (SQLException e) {
            System.err.println("Counter load failed (using defaults): " + e.getMessage());
            Logger.error("Counter load failed: " + e.getMessage());
        }
    }

    /**
     * Saves a single counter value to the database.
     * Uses INSERT OR REPLACE to handle both first-time inserts and updates.
     */
    private static void saveCounter(String entity, int value) {
        String sql = "INSERT OR REPLACE INTO counters (entity, counter) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, entity);
            pstmt.setInt(2, value);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Counter save failed for " + entity + ": " + e.getMessage());
            Logger.error("Counter save failed for " + entity + ": " + e.getMessage());
        }
    }

    // ==================== ID GENERATORS ====================

    public static String getNextPatientId() {
        String id = String.format("PAT%03d", ++patientCounter);
        saveCounter("PATIENT", patientCounter);
        return id;
    }

    public static String getNextDoctorId() {
        String id = String.format("DOC%03d", ++doctorCounter);
        saveCounter("DOCTOR", doctorCounter);
        return id;
    }

    public static String getNextAppointmentId() {
        String id = String.format("APP%03d", ++appointmentCounter);
        saveCounter("APPOINTMENT", appointmentCounter);
        return id;
    }

    public static String getNextBedId() {
        String id = String.format("BED%03d", ++bedCounter);
        saveCounter("BED", bedCounter);
        return id;
    }

    public static String getNextPrescriptionId() {
        String id = String.format("PRE%03d", ++prescriptionCounter);
        saveCounter("PRESCRIPTION", prescriptionCounter);
        return id;
    }

    public static String getNextBillId() {
        String id = String.format("BIL%03d", ++billCounter);
        saveCounter("BILL", billCounter);
        return id;
    }
}
