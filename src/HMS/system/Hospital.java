package HMS.system;

import HMS.model.*;
import java.util.*;

public class Hospital {
    private final ArrayList<Patient> patients;
    private final ArrayList<Doctor> doctors;
    private final ArrayList<Appointment> appointments;
    private final FileStorageManager storage;

    public Hospital() {
        storage = new FileStorageManager();
        patients = new ArrayList<>(storage.loadPatients());
        doctors = new ArrayList<>(storage.loadDoctors());
        appointments = new ArrayList<>(storage.loadAppointments());
    }

    // ==================== PATIENT OPERATIONS ====================

    public void addPatient(Patient p) {
        patients.add(p);
        storage.savePatients(patients);
        System.out.println("Patient added successfully! (ID: " + p.getId() + ")");
    }

    public Patient findPatient(String id) {
        for (Patient p : patients) {
            if (p.getId().equalsIgnoreCase(id)) return p;
        }
        return null;
    }

    public void viewPatients() {
        if (patients.isEmpty()) {
            System.out.println("\n  No patients registered yet.");
            return;
        }
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║        ALL PATIENTS (" + patients.size() + ")                  ║");
        System.out.println("╚══════════════════════════════════════════╝");
        for (Patient p : patients) {
            p.display();
        }
    }

    public boolean removePatient(String id) {
        Patient p = findPatient(id);
        if (p == null) {
            System.out.println("Patient with ID " + id + " not found!");
            return false;
        }
        patients.remove(p);
        storage.savePatients(patients);
        System.out.println("Patient " + p.getName() + " (ID: " + id + ") removed successfully.");
        return true;
    }

    // ==================== DOCTOR OPERATIONS ====================

    public void addDoctor(Doctor d) {
        doctors.add(d);
        storage.saveDoctors(doctors);
        System.out.println("Doctor added successfully! (ID: " + d.getId() + ")");
    }

    public Doctor findDoctor(String id) {
        for (Doctor d : doctors) {
            if (d.getId().equalsIgnoreCase(id)) return d;
        }
        return null;
    }

    public void viewDoctors() {
        if (doctors.isEmpty()) {
            System.out.println("\n  No doctors registered yet.");
            return;
        }
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║        ALL DOCTORS (" + doctors.size() + ")                   ║");
        System.out.println("╚══════════════════════════════════════════╝");
        for (Doctor d : doctors) {
            d.display();
        }
    }

    public boolean removeDoctor(String id) {
        Doctor d = findDoctor(id);
        if (d == null) {
            System.out.println("Doctor with ID " + id + " not found!");
            return false;
        }
        doctors.remove(d);
        storage.saveDoctors(doctors);
        System.out.println("Dr. " + d.getName() + " (ID: " + id + ") removed successfully.");
        return true;
    }

    // ==================== APPOINTMENT OPERATIONS ====================

    public void scheduleAppointment(String patientId, String doctorId, String date) {
        Patient patient = findPatient(patientId);
        Doctor doctor = findDoctor(doctorId);

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

    // ==================== MENU SYSTEM ====================

    public void run() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            printMainMenu();
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": patientMenu(sc); break;
                case "2": doctorMenu(sc); break;
                case "3": appointmentMenu(sc); break;
                case "0":
                    CounterManager.saveAll();
                    System.out.println("\nThank you for using HospiCare. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║      HOSPICARE MANAGEMENT SYSTEM         ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║                                          ║");
        System.out.println("║   1. Patient Management                  ║");
        System.out.println("║   2. Doctor Management                   ║");
        System.out.println("║   3. Appointment Management              ║");
        System.out.println("║                                          ║");
        System.out.println("║   0. Exit                                ║");
        System.out.println("║                                          ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.print("  Enter your choice: ");
    }

    // ---------- PATIENT MENU ----------

    private void patientMenu(Scanner sc) {
        while (true) {
            System.out.println("\n┌──────────────────────────────────────────┐");
            System.out.println("│         PATIENT MANAGEMENT               │");
            System.out.println("├──────────────────────────────────────────┤");
            System.out.println("│   1. Add Patient                         │");
            System.out.println("│   2. View All Patients                   │");
            System.out.println("│   3. Search Patient by ID                │");
            System.out.println("│   4. Update Patient                      │");
            System.out.println("│   5. Remove Patient                      │");
            System.out.println("│                                          │");
            System.out.println("│   0. Back to Main Menu                   │");
            System.out.println("└──────────────────────────────────────────┘");
            System.out.print("  Enter your choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": inputPatient(sc); break;
                case "2": viewPatients(); break;
                case "3": searchPatient(sc); break;
                case "4": updatePatient(sc); break;
                case "5": inputRemovePatient(sc); break;
                case "0": return;
                default: System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private void inputPatient(Scanner sc) {
        try {
            System.out.print("  Name: ");
            String name = sc.nextLine().trim();
            System.out.print("  Age: ");
            int age = Integer.parseInt(sc.nextLine().trim());
            System.out.print("  Disease: ");
            String disease = sc.nextLine().trim();
            Patient p = Patient.create(name, age, disease);
            addPatient(p);
        } catch (Exception e) {
            System.out.println("  Input error: " + e.getMessage());
        }
    }

    private void searchPatient(Scanner sc) {
        System.out.print("  Enter Patient ID: ");
        String id = sc.nextLine().trim();
        Patient p = findPatient(id);
        if (p == null) {
            System.out.println("  Patient with ID " + id + " not found!");
        } else {
            System.out.println();
            p.display();
        }
    }

    private void updatePatient(Scanner sc) {
        System.out.print("  Enter Patient ID to update: ");
        String id = sc.nextLine().trim();
        Patient p = findPatient(id);
        if (p == null) {
            System.out.println("  Patient with ID " + id + " not found!");
            return;
        }

        System.out.println("  Current details:");
        p.display();
        System.out.println("  (Press Enter to skip a field)\n");

        try {
            System.out.print("  New Name [" + p.getName() + "]: ");
            String name = sc.nextLine().trim();
            if (!name.isEmpty()) {
                p.setName(name);
            }

            System.out.print("  New Age [" + p.getAge() + "]: ");
            String ageStr = sc.nextLine().trim();
            if (!ageStr.isEmpty()) {
                p.setAge(Integer.parseInt(ageStr));
            }

            System.out.print("  New Disease [" + p.getDisease() + "]: ");
            String disease = sc.nextLine().trim();
            if (!disease.isEmpty()) {
                p.setDisease(disease);
            }

            storage.savePatients(patients);
            System.out.println("  Patient updated successfully!");
        } catch (Exception e) {
            System.out.println("  Update error: " + e.getMessage());
        }
    }

    private void inputRemovePatient(Scanner sc) {
        System.out.print("  Enter Patient ID to remove: ");
        String id = sc.nextLine().trim();
        removePatient(id);
    }

    // ---------- DOCTOR MENU ----------

    private void doctorMenu(Scanner sc) {
        while (true) {
            System.out.println("\n┌──────────────────────────────────────────┐");
            System.out.println("│          DOCTOR MANAGEMENT               │");
            System.out.println("├──────────────────────────────────────────┤");
            System.out.println("│   1. Add Doctor                          │");
            System.out.println("│   2. View All Doctors                    │");
            System.out.println("│   3. Search Doctor by ID                 │");
            System.out.println("│   4. Update Doctor                       │");
            System.out.println("│   5. Remove Doctor                       │");
            System.out.println("│                                          │");
            System.out.println("│   0. Back to Main Menu                   │");
            System.out.println("└──────────────────────────────────────────┘");
            System.out.print("  Enter your choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": inputDoctor(sc); break;
                case "2": viewDoctors(); break;
                case "3": searchDoctor(sc); break;
                case "4": updateDoctor(sc); break;
                case "5": inputRemoveDoctor(sc); break;
                case "0": return;
                default: System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private void inputDoctor(Scanner sc) {
        try {
            System.out.print("  Name: ");
            String name = sc.nextLine().trim();
            System.out.print("  Age: ");
            int age = Integer.parseInt(sc.nextLine().trim());
            System.out.print("  Specialization: ");
            String specialization = sc.nextLine().trim();
            Doctor d = Doctor.create(name, age, specialization);
            addDoctor(d);
        } catch (Exception e) {
            System.out.println("  Input error: " + e.getMessage());
        }
    }

    private void searchDoctor(Scanner sc) {
        System.out.print("  Enter Doctor ID: ");
        String id = sc.nextLine().trim();
        Doctor d = findDoctor(id);
        if (d == null) {
            System.out.println("  Doctor with ID " + id + " not found!");
        } else {
            System.out.println();
            d.display();
        }
    }

    private void updateDoctor(Scanner sc) {
        System.out.print("  Enter Doctor ID to update: ");
        String id = sc.nextLine().trim();
        Doctor d = findDoctor(id);
        if (d == null) {
            System.out.println("  Doctor with ID " + id + " not found!");
            return;
        }

        System.out.println("  Current details:");
        d.display();
        System.out.println("  (Press Enter to skip a field)\n");

        try {
            System.out.print("  New Name [" + d.getName() + "]: ");
            String name = sc.nextLine().trim();
            if (!name.isEmpty()) {
                d.setName(name);
            }

            System.out.print("  New Age [" + d.getAge() + "]: ");
            String ageStr = sc.nextLine().trim();
            if (!ageStr.isEmpty()) {
                d.setAge(Integer.parseInt(ageStr));
            }

            System.out.print("  New Specialization [" + d.getSpecialization() + "]: ");
            String spec = sc.nextLine().trim();
            if (!spec.isEmpty()) {
                d.setSpecialization(spec);
            }

            storage.saveDoctors(doctors);
            System.out.println("  Doctor updated successfully!");
        } catch (Exception e) {
            System.out.println("  Update error: " + e.getMessage());
        }
    }

    private void inputRemoveDoctor(Scanner sc) {
        System.out.print("  Enter Doctor ID to remove: ");
        String id = sc.nextLine().trim();
        removeDoctor(id);
    }

    // ---------- APPOINTMENT MENU ----------

    private void appointmentMenu(Scanner sc) {
        while (true) {
            System.out.println("\n┌──────────────────────────────────────────┐");
            System.out.println("│       APPOINTMENT MANAGEMENT             │");
            System.out.println("├──────────────────────────────────────────┤");
            System.out.println("│   1. Schedule Appointment                │");
            System.out.println("│   2. View All Appointments               │");
            System.out.println("│                                          │");
            System.out.println("│   0. Back to Main Menu                   │");
            System.out.println("└──────────────────────────────────────────┘");
            System.out.print("  Enter your choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": inputAppointment(sc); break;
                case "2": viewAppointments(); break;
                case "0": return;
                default: System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private void inputAppointment(Scanner sc) {
        try {
            System.out.print("  Patient ID: ");
            String pId = sc.nextLine().trim();
            System.out.print("  Doctor ID: ");
            String dId = sc.nextLine().trim();
            System.out.print("  Date (e.g. 2026-03-26): ");
            String date = sc.nextLine().trim();
            scheduleAppointment(pId, dId, date);
        } catch (Exception e) {
            System.out.println("  Appointment error: " + e.getMessage());
        }
    }
}
