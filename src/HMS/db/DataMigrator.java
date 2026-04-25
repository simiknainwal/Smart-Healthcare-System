package HMS.db;

import HMS.model.*;
import HMS.utils.FileStorageManager;
import HMS.utils.Logger;

import java.io.*;
import java.sql.*;
import java.util.List;

/**
 * DataMigrator — One-time tool to migrate data from .txt files into SQLite.
 *
 * HOW TO USE:
 *   1. Make sure your .txt files (patients.txt, doctors.txt, etc.) are in the project root.
 *   2. Run this class directly:
 *        java -cp "out;sqlite-jdbc-3.53.0.0.jar" HMS.db.DataMigrator
 *   3. It will read all .txt files and insert the data into hospicare.db.
 *   4. You only need to run this ONCE.
 *
 * After migration, the app will read/write from the database only.
 * The .txt files are kept as backup but are no longer used.
 */
public class DataMigrator {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║     HOSPICARE DATA MIGRATION TOOL       ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║  Migrating data from .txt → SQLite DB   ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println();

        // Step 1: Initialize the database (creates tables)
        DBConnection.initializeDatabase();

        // Step 2: Read data from .txt files using the old FileStorageManager
        FileStorageManager fileStorage = new FileStorageManager();

        List<Patient> patients = fileStorage.loadPatients();
        List<Doctor> doctors = fileStorage.loadDoctors();
        List<Appointment> appointments = fileStorage.loadAppointments();
        List<Bed> beds = fileStorage.loadBeds();

        // Step 3: Create DAOs
        PatientDAO patientDAO = new PatientDAO();
        DoctorDAO doctorDAO = new DoctorDAO();
        AppointmentDAO appointmentDAO = new AppointmentDAO();
        BedDAO bedDAO = new BedDAO();

        // Step 4: Insert data into the database
        System.out.println("Migrating patients...");
        for (Patient p : patients) {
            patientDAO.insert(p);
            System.out.println("  ✓ " + p.getId() + " - " + p.getName());
        }

        System.out.println("\nMigrating doctors...");
        for (Doctor d : doctors) {
            doctorDAO.insert(d);
            System.out.println("  ✓ " + d.getId() + " - " + d.getName());
        }

        System.out.println("\nMigrating appointments...");
        for (Appointment a : appointments) {
            appointmentDAO.insert(a);
            System.out.println("  ✓ " + a.getId() + " (Patient: " + a.getPatientId()
                    + " → Doctor: " + a.getDoctorId() + ")");
        }

        System.out.println("\nMigrating beds...");
        for (Bed b : beds) {
            bedDAO.insert(b);
            String status = b.isOccupied() ? "OCCUPIED" : "AVAILABLE";
            System.out.println("  ✓ " + b.getId() + " - " + b.getWardType() + " - " + status);
        }

        // Step 5: Migrate counters
        System.out.println("\nMigrating counters...");
        migrateCounters();

        // Step 6: Print summary
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║         MIGRATION COMPLETE! ✓            ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║  Patients:     " + padRight(String.valueOf(patients.size()), 25) + "║");
        System.out.println("║  Doctors:      " + padRight(String.valueOf(doctors.size()), 25) + "║");
        System.out.println("║  Appointments: " + padRight(String.valueOf(appointments.size()), 25) + "║");
        System.out.println("║  Beds:         " + padRight(String.valueOf(beds.size()), 25) + "║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println("\nYou can now run the main application.");
        System.out.println("The .txt files are no longer used (kept as backup).");

        Logger.info("Data migration completed: " + patients.size() + " patients, "
                + doctors.size() + " doctors, " + appointments.size() + " appointments, "
                + beds.size() + " beds.");
    }

    /**
     * Reads counters from counters.txt and inserts them into the counters table.
     */
    private static void migrateCounters() {
        String counterFile = "counters.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(counterFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String entity = parts[0].trim();
                    int value = Integer.parseInt(parts[1].trim());

                    String sql = "INSERT OR REPLACE INTO counters (entity, counter) VALUES (?, ?)";
                    try (Connection conn = DBConnection.getConnection();
                         PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, entity);
                        pstmt.setInt(2, value);
                        pstmt.executeUpdate();
                        System.out.println("  ✓ " + entity + " = " + value);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("  counters.txt not found — skipping counter migration.");
            Logger.warn("counters.txt not found during migration.");
        } catch (Exception e) {
            System.err.println("  Counter migration failed: " + e.getMessage());
            Logger.error("Counter migration failed: " + e.getMessage());
        }
    }

    /**
     * Helper to right-pad a string for formatted output.
     */
    private static String padRight(String text, int length) {
        return String.format("%-" + length + "s", text);
    }
}
