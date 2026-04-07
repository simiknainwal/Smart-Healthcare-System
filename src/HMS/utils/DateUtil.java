package HMS.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

//Central date utility for HospiCare.
//Storage format : yyyy-MM-dd  (e.g. "2026-04-07")
//Display format : dd MMM yyyy (e.g. "07 Apr 2026")
public class DateUtil {

    // Storage format used in .txt files
    private static final DateTimeFormatter STORAGE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Human-friendly display format
    private static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    // today's date as a LocalDate
    public static LocalDate today() {
        return LocalDate.now();
    }

    // today's date in storage format
    public static String todayFormatted() {
        return today().format(STORAGE_FMT);
    }

    // Parsing & validation

    // Parse a date string in storage format.
    public static LocalDate parse(String dateStr) {
        try {
            return LocalDate.parse(dateStr.trim(), STORAGE_FMT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Invalid date \"" + dateStr + "\". Expected format: yyyy-MM-dd (e.g. 2026-04-07)");
        }
    }

    // Formatting

    // Convert a storage-format string → pretty display string.
    public static String display(String storageDate) {
        if (storageDate == null || storageDate.isBlank())
            return storageDate;
        try {
            return LocalDate.parse(storageDate.trim(), STORAGE_FMT).format(DISPLAY_FMT);
        } catch (DateTimeParseException e) {
            return storageDate; // fall back gracefully
        }
    }

    // Arithmetic

    // Add days to a storage-format date string and return
    // the result in storage format.
    public static String addDays(String storageDate, int days) {
        return parse(storageDate).plusDays(days).format(STORAGE_FMT);
    }
}
