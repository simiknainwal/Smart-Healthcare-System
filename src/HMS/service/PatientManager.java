package HMS.service;

import HMS.model.Patient;
import HMS.utils.FileStorageManager;
import java.util.ArrayList;
import java.util.Scanner;

public class PatientManager {
    private final ArrayList<Patient> patients;
    private final FileStorageManager storage;

    public PatientManager(ArrayList<Patient> patients, FileStorageManager storage) {
        this.patients = patients;
        this.storage = storage;
    }

    // ==================== OPERATIONS ====================

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

    // ==================== MENU ====================

    public void showMenu(Scanner sc) {
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
}
