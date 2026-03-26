package HMS.system;

import java.io.*;

public class CounterManager {
    private static final String COUNTER_FILE = "counters.txt";
    private static int patientCounter = 0;
    private static int doctorCounter = 0;
    private static int appointmentCounter = 0;

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
                    }
                }
            }
            System.out.println("Counters loaded: PAT=" + patientCounter + ", DOC=" + doctorCounter + ", APP="
                    + appointmentCounter);
        } catch (Exception e) {
            System.out.println("No existing counters file - starting fresh");
        }
    }

    private static void saveCounters() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(COUNTER_FILE))) {
            writer.println("PATIENT=" + patientCounter);
            writer.println("DOCTOR=" + doctorCounter);
            writer.println("APPOINTMENT=" + appointmentCounter);
            System.out.println("Counters saved");
        } catch (IOException e) {
            System.err.println("Counter save failed: " + e.getMessage());
        }
    }

    public static String getNextPatientId() {
        return String.format("PAT%03d", ++patientCounter);
    }

    public static String getNextDoctorId() {
        return String.format("DOC%03d", ++doctorCounter);
    }

    public static String getNextAppointmentId() {
        return String.format("APP%03d", ++appointmentCounter);
    }

    public static void saveAll() {
        saveCounters();
    }
}
