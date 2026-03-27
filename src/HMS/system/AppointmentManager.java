package HMS.system;

import HMS.model.*;
import java.util.ArrayList;
import java.util.Scanner;

public class AppointmentManager {
    private final ArrayList<Appointment> appointments;
    private final ArrayList<Doctor> doctors;
    private final PatientManager patientManager;
    private final DoctorManager doctorManager;
    private final FileStorageManager storage;

    public AppointmentManager(ArrayList<Appointment> appointments, ArrayList<Doctor> doctors,
                              PatientManager patientManager, DoctorManager doctorManager,
                              FileStorageManager storage) {
        this.appointments = appointments;
        this.doctors = doctors;
        this.patientManager = patientManager;
        this.doctorManager = doctorManager;
        this.storage = storage;
    }

    // ==================== OPERATIONS ====================

    public void scheduleAppointment(String patientId, String doctorId, String date) {
        Patient patient = patientManager.findPatient(patientId);
        Doctor doctor = doctorManager.findDoctor(doctorId);

        if (patient == null) {
            System.out.println("Patient ID " + patientId + " not found!");
            return;
        }
        if (doctor == null) {
            System.out.println("Doctor ID " + doctorId + " not found!");
            return;
        }

        Appointment app = Appointment.create(patientId, doctorId, date);
        appointments.add(app);
        storage.saveAppointments(appointments);
        System.out.println("Appointment scheduled successfully! (ID: " + app.getId() + ")");
    }

    public void viewAppointments() {
        if (appointments.isEmpty()) {
            System.out.println("\n  No appointments scheduled yet.");
            return;
        }
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║      ALL APPOINTMENTS (" + appointments.size() + ")               ║");
        System.out.println("╚══════════════════════════════════════════╝");
        for (Appointment a : appointments) {
            a.display();
        }
    }

    // ==================== MENU ====================

    public void showMenu(Scanner sc) {
        while (true) {
            System.out.println("\n┌──────────────────────────────────────────┐");
            System.out.println("│       APPOINTMENT MANAGEMENT             │");
            System.out.println("├──────────────────────────────────────────┤");
            System.out.println("│   1. Schedule Appointment                │");
            System.out.println("│   2. View All Appointments               │");
            System.out.println("│   3. Update Appointment Status           │");
            System.out.println("│                                          │");
            System.out.println("│   0. Back to Main Menu                   │");
            System.out.println("└──────────────────────────────────────────┘");
            System.out.print("  Enter your choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": inputAppointment(sc); break;
                case "2": viewAppointments(); break;
                case "3": updateAppointmentStatus(sc); break;
                case "0": return;
                default: System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private void inputAppointment(Scanner sc) {
        try {
            System.out.print("  Patient ID: ");
            String pId = sc.nextLine().trim();

            Patient patient = patientManager.findPatient(pId);
            if (patient == null) {
                System.out.println("  Patient ID " + pId + " not found!");
                return;
            }

            System.out.println("  Patient: " + patient.getName() + " | Disease: " + patient.getDisease());

            // Try to suggest a matching doctor
            Doctor suggested = DoctorDirectory.suggestDoctor(patient.getDisease(), doctors);
            String defaultDoctorId = "";

            if (suggested != null) {
                defaultDoctorId = suggested.getId();
                System.out.println("  Suggested Doctor: " + suggested.getName()
                        + " [" + suggested.getId() + "] (" + suggested.getSpecialization() + ")");
                System.out.print("  Doctor ID [" + defaultDoctorId + "]: ");
            } else {
                System.out.println("  No matching doctor found for \"" + patient.getDisease() + "\".");
                doctorManager.viewDoctors();
                System.out.print("  Doctor ID: ");
            }

            String dId = sc.nextLine().trim();
            if (dId.isEmpty() && !defaultDoctorId.isEmpty()) {
                dId = defaultDoctorId;
            }

            // Resolve doctor to get canonical ID
            Doctor doctor = doctorManager.findDoctor(dId);
            if (doctor == null) {
                System.out.println("  Doctor ID " + dId + " not found!");
                return;
            }

            System.out.print("  Date (e.g. 2026-03-26): ");
            String date = sc.nextLine().trim();
            scheduleAppointment(patient.getId(), doctor.getId(), date);
        } catch (Exception e) {
            System.out.println("  Appointment error: " + e.getMessage());
        }
    }

    private void updateAppointmentStatus(Scanner sc) {
        if (appointments.isEmpty()) {
            System.out.println("\n  No appointments to update.");
            return;
        }

        System.out.print("  Enter Appointment ID: ");
        String appId = sc.nextLine().trim();

        Appointment target = null;
        for (Appointment a : appointments) {
            if (a.getId().equalsIgnoreCase(appId)) {
                target = a;
                break;
            }
        }

        if (target == null) {
            System.out.println("  Appointment ID " + appId + " not found!");
            return;
        }

        System.out.println("  Current Status: " + target.getStatus());
        System.out.println("\n  Select New Status:");
        System.out.println("    1. Scheduled");
        System.out.println("    2. Done");
        System.out.println("    3. Cancelled");
        System.out.print("  Choose: ");
        String statusChoice = sc.nextLine().trim();

        String newStatus;
        switch (statusChoice) {
            case "1": newStatus = "Scheduled"; break;
            case "2": newStatus = "Done"; break;
            case "3": newStatus = "Cancelled"; break;
            default:
                System.out.println("  Invalid status choice!");
                return;
        }

        target.setStatus(newStatus);
        storage.saveAppointments(appointments);
        System.out.println("  Appointment " + target.getId() + " status updated to: " + newStatus);
    }
}
