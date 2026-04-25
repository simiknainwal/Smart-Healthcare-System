package HMS.service;

import HMS.db.AppointmentDAO;
import HMS.model.*;
import HMS.utils.DateUtil;
import HMS.utils.DoctorDirectory;
import HMS.utils.Logger;
import java.util.ArrayList;
import java.util.Scanner;

public class AppointmentManager {
    private final ArrayList<Appointment> appointments;
    private final ArrayList<Doctor> doctors;
    private final PatientManager patientManager;
    private final DoctorManager doctorManager;
    private final AppointmentDAO appointmentDAO;

    public AppointmentManager(ArrayList<Appointment> appointments, ArrayList<Doctor> doctors,
            PatientManager patientManager, DoctorManager doctorManager,
            AppointmentDAO appointmentDAO) {
        this.appointments = appointments;
        this.doctors = doctors;
        this.patientManager = patientManager;
        this.doctorManager = doctorManager;
        this.appointmentDAO = appointmentDAO;
    }

    public ArrayList<Appointment> getAppointments() {
        return appointments;
    }

    /** Called by UI panels to persist an appointment update to the database. */
    public void updateAppointmentInDB(Appointment a) {
        appointmentDAO.update(a);
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
        appointmentDAO.insert(app);  // Save to database
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
            System.out.println("│   4. Reschedule Appointment              │");
            System.out.println("│                                          │");
            System.out.println("│   0. Back to Main Menu                   │");
            System.out.println("└──────────────────────────────────────────┘");
            System.out.print("  Enter your choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    inputAppointment(sc);
                    break;
                case "2":
                    viewAppointments();
                    break;
                case "3":
                    updateAppointmentStatus(sc);
                    break;
                case "4":
                    rescheduleAppointment(sc);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option! Please try again.");
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
                System.out.print(" Doctor ID [" + defaultDoctorId + "]: ");
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

            // ── Appointment date (default: today) ────────────────────────────
            System.out.print("  Date [" + DateUtil.todayFormatted() + "]: ");
            String dateInput = sc.nextLine().trim();
            String date;
            if (dateInput.isEmpty()) {
                date = DateUtil.todayFormatted();
            } else {
                try {
                    DateUtil.parse(dateInput); // validate
                    date = dateInput;
                } catch (IllegalArgumentException e) {
                    System.out.println("  " + e.getMessage());
                    return;
                }
            }
            scheduleAppointment(patient.getId(), doctor.getId(), date);
        } catch (Exception e) {
            System.out.println("  Appointment error: " + e.getMessage());
            Logger.error("Appointment input error: " + e.getMessage());
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
            case "1":
                newStatus = "Scheduled";
                break;
            case "2":
                newStatus = "Done";
                break;
            case "3":
                newStatus = "Cancelled";
                break;
            default:
                System.out.println("  Invalid status choice!");
                return;
        }

        target.setStatus(newStatus);
        appointmentDAO.update(target);  // Save to database
        System.out.println("  Appointment " + target.getId() + " status updated to: " + newStatus);
    }

    private void rescheduleAppointment(Scanner sc) {
        if (appointments.isEmpty()) {
            System.out.println("\n  No appointments to reschedule.");
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

        System.out.println("  Current Date: " + DateUtil.display(target.getDate())
                + "  |  Status: " + target.getStatus());
        System.out.print("  New Date [" + DateUtil.todayFormatted() + "]: ");
        String newDateInput = sc.nextLine().trim();

        String newDate;
        if (newDateInput.isEmpty()) {
            newDate = DateUtil.todayFormatted();
        } else {
            try {
                DateUtil.parse(newDateInput); // validate
                newDate = newDateInput;
            } catch (IllegalArgumentException e) {
                System.out.println("  " + e.getMessage());
                return;
            }
        }

        target.setDate(newDate);
        // Reset status to Scheduled on reschedule
        target.setStatus("Scheduled");
        appointmentDAO.update(target);  // Save to database
        System.out.println("  Appointment " + target.getId() + " rescheduled to: "
                + DateUtil.display(newDate) + " (Status reset to Scheduled)");
    }
}
