package HMS.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Logger — A simple file-based logging utility for HospiCare.
 *
 * This class uses FILE HANDLING to write logs to "log.txt".
 * Every important event (insert, update, delete, errors) is logged
 * with a timestamp and severity level.
 *
 * Usage:
 *   Logger.info("Patient added: PAT001");
 *   Logger.warn("Duplicate patient ID detected");
 *   Logger.error("Database connection failed: " + e.getMessage());
 *
 * Log file format:
 *   [2026-04-25 12:30:15] [INFO] Patient added: PAT001
 *   [2026-04-25 12:30:22] [ERROR] Database connection failed: ...
 */
public class Logger {

    // The log file — created in the project root folder
    private static final String LOG_FILE = "log.txt";

    // Timestamp format for each log entry
    private static final DateTimeFormatter TIMESTAMP_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Log an INFO message — for successful operations.
     * Examples: record inserted, record updated, record deleted, app started.
     */
    public static void info(String message) {
        writeLog("INFO", message);
    }

    /**
     * Log a WARN message — for non-critical issues.
     * Examples: record not found, invalid input skipped.
     */
    public static void warn(String message) {
        writeLog("WARN", message);
    }

    /**
     * Log an ERROR message — for failures and exceptions.
     * Examples: database error, file read failure.
     */
    public static void error(String message) {
        writeLog("ERROR", message);
    }

    /**
     * Internal method that actually writes a line to log.txt.
     * Uses FileWriter in APPEND mode (true) so old logs are never lost.
     * Synchronized to be thread-safe (multiple threads won't corrupt the file).
     */
    private static synchronized void writeLog(String level, String message) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FMT);
        String logLine = "[" + timestamp + "] [" + level + "] " + message;

        // FILE HANDLING: Append the log line to log.txt
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println(logLine);
        } catch (IOException e) {
            // If logging itself fails, print to console as fallback
            System.err.println("Logger failed to write: " + e.getMessage());
            System.err.println("Original log: " + logLine);
        }
    }
}
