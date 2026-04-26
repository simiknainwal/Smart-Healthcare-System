package HMS.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DBConnection — Manages the SQLite database connection for HospiCare.
 *
 * How it works:
 * - The database is a single file called "hospicare.db" in the project folder.
 * - No username or password is needed (unlike MySQL).
 * - Call DBConnection.getConnection() to get a live connection.
 * - Call DBConnection.initializeDatabase() once at startup to create tables.
 */
public class DBConnection {

    // All runtime data files live inside the "data" folder
    private static final String DATA_DIR = "data";
    private static final String URL = "jdbc:sqlite:" + DATA_DIR + "/hospicare.db";

    /**
     * Returns a new connection to the SQLite database.
     * Each caller is responsible for closing the connection when done.
     *
     * Usage:
     * Connection conn = DBConnection.getConnection();
     * // ... use the connection ...
     * conn.close();
     */
    public static Connection getConnection() throws SQLException {
        // Ensure the data directory exists before connecting
        new File(DATA_DIR).mkdirs();
        return DriverManager.getConnection(URL);
    }

    /**
     * Creates all required tables if they don't already exist.
     * Call this ONCE when the application starts up.
     * If the tables already exist, this method does nothing (safe to call
     * repeatedly).
     */
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {

            // --- patients table ---
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS patients (" +
                            "    id TEXT PRIMARY KEY," +
                            "    name TEXT NOT NULL," +
                            "    age INTEGER NOT NULL," +
                            "    disease TEXT NOT NULL" +
                            ")");

            // --- doctors table ---
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS doctors (" +
                            "    id TEXT PRIMARY KEY," +
                            "    name TEXT NOT NULL," +
                            "    age INTEGER NOT NULL," +
                            "    specialization TEXT NOT NULL" +
                            ")");

            // --- appointments table ---
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS appointments (" +
                            "    id TEXT PRIMARY KEY," +
                            "    patient_id TEXT NOT NULL," +
                            "    doctor_id TEXT NOT NULL," +
                            "    date TEXT NOT NULL," +
                            "    status TEXT NOT NULL" +
                            ")");

            // --- beds table ---
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS beds (" +
                            "    id TEXT PRIMARY KEY," +
                            "    ward_type TEXT NOT NULL," +
                            "    occupied INTEGER NOT NULL DEFAULT 0," +
                            "    patient_id TEXT DEFAULT ''," +
                            "    admit_date TEXT DEFAULT ''," +
                            "    discharge_date TEXT DEFAULT ''" +
                            ")");

            // --- counters table (replaces counters.txt) ---
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS counters (" +
                            "    entity TEXT PRIMARY KEY," +
                            "    counter INTEGER NOT NULL DEFAULT 0" +
                            ")");

            // --- users table (login accounts) ---
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "    username TEXT PRIMARY KEY," +
                            "    password_hash TEXT NOT NULL," +
                            "    role TEXT NOT NULL," +
                            "    linked_id TEXT DEFAULT ''," +
                            "    created_at TEXT NOT NULL" +
                            ")");

            // --- prescriptions table ---
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS prescriptions (" +
                            "    id TEXT PRIMARY KEY," +
                            "    patient_id TEXT NOT NULL," +
                            "    doctor_id TEXT NOT NULL," +
                            "    medication TEXT NOT NULL," +
                            "    dosage TEXT NOT NULL," +
                            "    date TEXT NOT NULL" +
                            ")");

            // --- bills table ---
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS bills (" +
                            "    id TEXT PRIMARY KEY," +
                            "    patient_id TEXT NOT NULL," +
                            "    amount REAL NOT NULL," +
                            "    description TEXT NOT NULL," +
                            "    date TEXT NOT NULL," +
                            "    status TEXT NOT NULL DEFAULT 'UNPAID'" +
                            ")");

            System.out.println("[DB] Database initialized successfully.");
            
            // Seed default admin account
            HMS.utils.AuthService.seedDefaultAdmin();

        } catch (SQLException e) {
            System.err.println("[DB] Failed to initialize database: " + e.getMessage());
        }
    }
}
