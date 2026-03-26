package HMS.system;

import HMS.model.Doctor;
import java.util.ArrayList;
import java.util.Scanner;

public class DoctorManager {
    private final ArrayList<Doctor> doctors;
    private final FileStorageManager storage;

    public DoctorManager(ArrayList<Doctor> doctors, FileStorageManager storage) {
        this.doctors = doctors;
        this.storage = storage;
    }

    // ==================== OPERATIONS ====================

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

    // ==================== MENU ====================

    public void showMenu(Scanner sc) {
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
}
