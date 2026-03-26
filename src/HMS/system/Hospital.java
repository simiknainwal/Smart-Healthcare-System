package HMS.system;

import HMS.model.*;
import java.util.*;

public class Hospital {
    private final ArrayList<Patient> patients;
    private final ArrayList<Doctor> doctors;
    private final ArrayList<Appointment> appointments;
    private final ArrayList<Bed> beds;
    private final FileStorageManager storage;

    // Fixed stay duration (in days) for each ward type
    private static final int GENERAL_STAY = 7;
    private static final int ICU_STAY = 3;
    private static final int PRIVATE_STAY = 10;
    private static final int SEMI_PRIVATE_STAY = 5;

    public Hospital() {
        storage = new FileStorageManager();
        patients = new ArrayList<>(storage.loadPatients());
        doctors = new ArrayList<>(storage.loadDoctors());
        appointments = new ArrayList<>(storage.loadAppointments());
        beds = new ArrayList<>(storage.loadBeds());
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
                case "4": bedMenu(sc); break;
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
        System.out.println("║   4. Bed Management                      ║");
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

            Patient patient = findPatient(pId);
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
                viewDoctors();
                System.out.print("  Doctor ID: ");
            }

            String dId = sc.nextLine().trim();
            if (dId.isEmpty() && !defaultDoctorId.isEmpty()) {
                dId = defaultDoctorId;
            }

            System.out.print("  Date (e.g. 2026-03-26): ");
            String date = sc.nextLine().trim();
            scheduleAppointment(pId, dId, date);
        } catch (Exception e) {
            System.out.println("  Appointment error: " + e.getMessage());
        }
    }

    // ---------- BED MENU ----------

    private void bedMenu(Scanner sc) {
        while (true) {
            System.out.println("\n┌──────────────────────────────────────────┐");
            System.out.println("│           BED MANAGEMENT                 │");
            System.out.println("├──────────────────────────────────────────┤");
            System.out.println("│   1. View All Beds                       │");
            System.out.println("│   2. Check Availability by Ward          │");
            System.out.println("│   3. Book a Bed                          │");
            System.out.println("│   4. Discharge Patient (Free Bed)        │");
            System.out.println("│                                          │");
            System.out.println("│   0. Back to Main Menu                   │");
            System.out.println("└──────────────────────────────────────────┘");
            System.out.print("  Enter your choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": viewAllBeds(); break;
                case "2": checkAvailability(sc); break;
                case "3": bookBed(sc); break;
                case "4": dischargeBed(sc); break;
                case "0": return;
                default: System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private void viewAllBeds() {
        if (beds.isEmpty()) {
            System.out.println("\n  No beds in the system.");
            return;
        }
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║            ALL BEDS (" + beds.size() + ")                  ║");
        System.out.println("╚══════════════════════════════════════════╝");
        for (Bed b : beds) {
            b.display();
        }
    }

    private void checkAvailability(Scanner sc) {
        System.out.println("\n  Ward Types: General, ICU, Private, Semi-Private");
        System.out.print("  Enter ward type (or press Enter for all): ");
        String ward = sc.nextLine().trim();

        int available = 0;
        int total = 0;

        System.out.println();

        for (Bed b : beds) {
            if (ward.isEmpty() || b.getWardType().equalsIgnoreCase(ward)) {
                total++;
                if (!b.isOccupied()) {
                    available++;
                }
            }
        }

        if (total == 0) {
            System.out.println("  No beds found for ward: " + ward);
            return;
        }

        String wardName = ward.isEmpty() ? "All Wards" : ward;
        System.out.println("  ┌───────────────────────────────────┐");
        System.out.println("  │ Ward: " + wardName);
        System.out.println("  │ Total Beds: " + total);
        System.out.println("  │ Available:  " + available);
        System.out.println("  │ Occupied:   " + (total - available));
        System.out.println("  └───────────────────────────────────┘");

        // Show available beds
        if (available > 0) {
            System.out.println("\n  Available Beds:");
            for (Bed b : beds) {
                if (ward.isEmpty() || b.getWardType().equalsIgnoreCase(ward)) {
                    if (!b.isOccupied()) {
                        b.display();
                    }
                }
            }
        }
    }

    private void bookBed(Scanner sc) {
        try {
            System.out.print("  Patient ID: ");
            String patientId = sc.nextLine().trim();

            Patient patient = findPatient(patientId);
            if (patient == null) {
                System.out.println("  Patient ID " + patientId + " not found!");
                return;
            }

            // Check if patient already has a bed
            for (Bed b : beds) {
                if (b.isOccupied() && b.getPatientId().equals(patientId)) {
                    System.out.println("  Patient " + patient.getName() + " already has a bed: " + b.getId()
                            + " (" + b.getWardType() + ")");
                    return;
                }
            }

            System.out.println("  Patient: " + patient.getName() + " | Disease: " + patient.getDisease());
            System.out.println("\n  Select Ward Type:");
            System.out.println("    1. General       (Stay: " + GENERAL_STAY + " days)");
            System.out.println("    2. ICU           (Stay: " + ICU_STAY + " days)");
            System.out.println("    3. Private       (Stay: " + PRIVATE_STAY + " days)");
            System.out.println("    4. Semi-Private  (Stay: " + SEMI_PRIVATE_STAY + " days)");
            System.out.print("  Choose ward: ");
            String wardChoice = sc.nextLine().trim();

            String wardType;
            int stayDays;
            switch (wardChoice) {
                case "1": wardType = "General"; stayDays = GENERAL_STAY; break;
                case "2": wardType = "ICU"; stayDays = ICU_STAY; break;
                case "3": wardType = "Private"; stayDays = PRIVATE_STAY; break;
                case "4": wardType = "Semi-Private"; stayDays = SEMI_PRIVATE_STAY; break;
                default:
                    System.out.println("  Invalid ward selection!");
                    return;
            }

            // Find first available bed in the selected ward
            Bed availableBed = null;
            for (Bed b : beds) {
                if (b.getWardType().equals(wardType) && !b.isOccupied()) {
                    availableBed = b;
                    break;
                }
            }

            if (availableBed == null) {
                System.out.println("  No available beds in " + wardType + " ward!");
                return;
            }

            System.out.print("  Admit Date (e.g. 2026-03-26): ");
            String admitDate = sc.nextLine().trim();

            // Calculate discharge date by adding stayDays
            String dischargeDate = calculateDischargeDate(admitDate, stayDays);

            availableBed.book(patientId, admitDate, dischargeDate);
            storage.saveBeds(beds);

            System.out.println("\n  Bed booked successfully!");
            System.out.println("  Bed ID: " + availableBed.getId());
            System.out.println("  Ward: " + wardType);
            System.out.println("  Patient: " + patient.getName());
            System.out.println("  Admit Date: " + admitDate);
            System.out.println("  Expected Discharge: " + dischargeDate + " (" + stayDays + " days)");

        } catch (Exception e) {
            System.out.println("  Booking error: " + e.getMessage());
        }
    }

    private void dischargeBed(Scanner sc) {
        System.out.print("  Enter Bed ID to discharge: ");
        String bedId = sc.nextLine().trim();

        for (Bed b : beds) {
            if (b.getId().equalsIgnoreCase(bedId)) {
                if (!b.isOccupied()) {
                    System.out.println("  Bed " + bedId + " is already available.");
                    return;
                }

                String patientId = b.getPatientId();
                Patient patient = findPatient(patientId);
                String patientName = (patient != null) ? patient.getName() : patientId;

                b.discharge();
                storage.saveBeds(beds);
                System.out.println("  Patient " + patientName + " discharged from Bed " + bedId + ".");
                System.out.println("  Bed " + bedId + " is now AVAILABLE.");
                return;
            }
        }
        System.out.println("  Bed ID " + bedId + " not found!");
    }

    // Simple date calculation: adds days to a date string (YYYY-MM-DD format)
    private String calculateDischargeDate(String admitDate, int days) {
        try {
            String[] parts = admitDate.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            // Days in each month
            int[] daysInMonth = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

            // Check for leap year
            if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
                daysInMonth[2] = 29;
            }

            day = day + days;
            while (day > daysInMonth[month]) {
                day = day - daysInMonth[month];
                month++;
                if (month > 12) {
                    month = 1;
                    year++;
                    // Recalculate leap year for new year
                    if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
                        daysInMonth[2] = 29;
                    } else {
                        daysInMonth[2] = 28;
                    }
                }
            }

            return String.format("%04d-%02d-%02d", year, month, day);
        } catch (Exception e) {
            return admitDate + " + " + days + " days";
        }
    }
}
