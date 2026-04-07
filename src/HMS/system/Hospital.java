package HMS.system;

import HMS.model.*;
import java.util.*;

public class Hospital {
    private final PatientManager patientManager;
    private final DoctorManager doctorManager;
    private final AppointmentManager appointmentManager;
    private final BedManager bedManager;

    public Hospital() {
        FileStorageManager storage = new FileStorageManager();

        ArrayList<Patient> patients = new ArrayList<>(storage.loadPatients());
        ArrayList<Doctor> doctors = new ArrayList<>(storage.loadDoctors());
        ArrayList<Appointment> appointments = new ArrayList<>(storage.loadAppointments());
        ArrayList<Bed> beds = new ArrayList<>(storage.loadBeds());

        patientManager = new PatientManager(patients, storage);
        doctorManager = new DoctorManager(doctors, storage);
        appointmentManager = new AppointmentManager(appointments, doctors, patientManager, doctorManager, storage);
        bedManager = new BedManager(beds, patientManager, storage);
    }

    public void run() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            printMainMenu();
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": patientManager.showMenu(sc); break;
                case "2": doctorManager.showMenu(sc); break;
                case "3": appointmentManager.showMenu(sc); break;
                case "4": bedManager.showMenu(sc); break;
                case "0":
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
}
