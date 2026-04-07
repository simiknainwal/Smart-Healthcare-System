package HMS.system;

import java.io.*;

public class CounterManager {
    private static final String COUNTER_FILE = "counters.txt";
    private static int patientCounter = 0;
    private static int doctorCounter = 0;
    private static int appointmentCounter = 0;
    private static int bedCounter = 0;

    static {
        loadCounters();
    }

    private static void loadCounters() {
        try (BufferedReader reader = new BufferedReader(new FileReader(COUNTER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    switch (parts[0]) {
                        case "PATIENT":
                            patientCounter = Integer.parseInt(parts[1]);
                            break;
                        case "DOCTOR":
                            doctorCounter = Integer.parseInt(parts[1]);
                            break;
                        case "APPOINTMENT":
                            appointmentCounter = Integer.parseInt(parts[1]);
                            break;
                        case "BED":
                            bedCounter = Integer.parseInt(parts[1]);
                            break;
                    }
                }
            }

        } catch (Exception e) {
            // counters.txt not found — starting fresh with defaults
        }
    }

    private static void saveCounters() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(COUNTER_FILE))) {
            writer.println("PATIENT=" + patientCounter);
            writer.println("DOCTOR=" + doctorCounter);
            writer.println("APPOINTMENT=" + appointmentCounter);
            writer.println("BED=" + bedCounter);
        } catch (IOException e) {
            System.err.println("Counter save failed: " + e.getMessage());
        }
    }

    public static String getNextPatientId() {
        String id = String.format("PAT%03d", ++patientCounter);
        saveCounters();
        return id;
    }

    public static String getNextDoctorId() {
        String id = String.format("DOC%03d", ++doctorCounter);
        saveCounters();
        return id;
    }

    public static String getNextAppointmentId() {
        String id = String.format("APP%03d", ++appointmentCounter);
        saveCounters();
        return id;
    }

    public static String getNextBedId() {
        String id = String.format("BED%03d", ++bedCounter);
        saveCounters();
        return id;
    }
}
