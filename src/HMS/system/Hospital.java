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

    public void addPatient(Patient p) {
        patients.add(p);
        storage.savePatients(patients);
    }

    public Patient findPatient(String id) {
        for (Patient p : patients) {
            if (p.getId().equals(id)) return p;
        }
        return null;
    }

    public void viewPatients() {
        if (patients.isEmpty()) {
            System.out.println("\nNo patients registered");
            return;
        }
        System.out.println("\n=== ALL PATIENTS (" + patients.size() + ") ===");
        for (Patient p : patients) {
            p.display();
        }
    }

    public void addDoctor(Doctor d) {
        doctors.add(d);
        storage.saveDoctors(doctors);
    }

    public Doctor findDoctor(String id) {
        for (Doctor d : doctors) {
            if (d.getId().equals(id)) return d;
        }
        return null;
    }

    public void viewDoctors() {
        if (doctors.isEmpty()) {
            System.out.println("\nNo doctors registered");
            return;
        }
        System.out.println("\n=== ALL DOCTORS (" + doctors.size() + ") ===");
        for (Doctor d : doctors) {
            d.display();
        }
    }

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
    }

    public void viewAppointments() {
        if (appointments.isEmpty()) {
            System.out.println("\nNo appointments scheduled");
            return;
        }
        System.out.println("\n=== ALL APPOINTMENTS (" + appointments.size() + ") ===");
        for (Appointment a : appointments) {
            a.display();
        }
    }

    public void run() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n1. Add Patient  2. Add Doctor  3. Schedule Appointment");
            System.out.println("4. View Patients  5. View Doctors  6. View Appointments");
            System.out.println("0. Exit");
            System.out.print("Choose: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": inputPatient(sc); break;
                case "2": inputDoctor(sc); break;
                case "3": inputAppointment(sc); break;
                case "4": viewPatients(); break;
                case "5": viewDoctors(); break;
                case "6": viewAppointments(); break;
                case "0":
                    CounterManager.saveAll();
                    return;
                default: System.out.println("Invalid option");
            }
        }
    }

    private void inputPatient(Scanner sc) {
        try {
            System.out.print("Name: ");
            String name = sc.nextLine().trim();
            System.out.print("Age: ");
            int age = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Disease: ");
            String disease = sc.nextLine().trim();
            Patient p = Patient.create(name, age, disease);
            addPatient(p);
        } catch (Exception e) {
            System.out.println("Input error: " + e.getMessage());
        }
    }

    private void inputDoctor(Scanner sc) {
        try {
            System.out.print("Name: ");
            String name = sc.nextLine().trim();
            System.out.print("Age: ");
            int age = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Specialization: ");
            String specialization = sc.nextLine().trim();
            Doctor d = Doctor.create(name, age, specialization);
            addDoctor(d);
        } catch (Exception e) {
            System.out.println("Input error: " + e.getMessage());
        }
    }

    private void inputAppointment(Scanner sc) {
        try {
            System.out.print("Patient ID: ");
            String pId = sc.nextLine().trim();
            System.out.print("Doctor ID: ");
            String dId = sc.nextLine().trim();
            System.out.print("Date: ");
            String date = sc.nextLine().trim();
            scheduleAppointment(pId, dId, date);
        } catch (Exception e) {
            System.out.println("Appointment error");
        }
    }
}
