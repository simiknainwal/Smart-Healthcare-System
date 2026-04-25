package HMS;

import HMS.db.*;
import HMS.model.*;
import HMS.service.*;
import HMS.utils.Logger;
import java.util.*;

public class HospitalApp {
    private final PatientManager patientManager;
    private final DoctorManager doctorManager;
    private final AppointmentManager appointmentManager;
    private final BedManager bedManager;
    private final PrescriptionManager prescriptionManager;
    private final BillingManager billingManager;
    private final ReportManager reportManager;

    public HospitalApp() {
        // Step 1: Initialize the database (creates tables if they don't exist)
        DBConnection.initializeDatabase();

        // Step 2: Create DAO instances
        PatientDAO patientDAO = new PatientDAO();
        DoctorDAO doctorDAO = new DoctorDAO();
        AppointmentDAO appointmentDAO = new AppointmentDAO();
        BedDAO bedDAO = new BedDAO();
        PrescriptionDAO prescriptionDAO = new PrescriptionDAO();
        BillDAO billDAO = new BillDAO();
        MedReportDAO reportDAO = new MedReportDAO();

        // Step 3: Load data from database into memory
        ArrayList<Patient> patients = new ArrayList<>(patientDAO.getAll());
        ArrayList<Doctor> doctors = new ArrayList<>(doctorDAO.getAll());
        ArrayList<Appointment> appointments = new ArrayList<>(appointmentDAO.getAll());
        ArrayList<Bed> beds = new ArrayList<>(bedDAO.getAll());
        ArrayList<Prescription> prescriptions = new ArrayList<>(prescriptionDAO.getAll());
        ArrayList<Bill> bills = new ArrayList<>(billDAO.getAll());
        ArrayList<MedReport> reports = new ArrayList<>(reportDAO.getAll());

        // Step 4: Create service managers with DAOs
        patientManager = new PatientManager(patients, patientDAO);
        doctorManager = new DoctorManager(doctors, doctorDAO);
        appointmentManager = new AppointmentManager(appointments, doctors, patientManager, doctorManager, appointmentDAO);
        bedManager = new BedManager(beds, patientManager, bedDAO);
        prescriptionManager = new PrescriptionManager(prescriptions, prescriptionDAO);
        billingManager = new BillingManager(bills, billDAO);
        reportManager = new ReportManager(reports, reportDAO);

        Logger.info("HospiCare application started successfully.");
    }

    public void runGUI() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // ignore
            }
            // Start with Login screen instead of Dashboard
            new HMS.ui.LoginFrame(this).setVisible(true);
        });
    }

    public void handleLoginSuccess(User user) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            if (user.getRole().equals("ADMIN")) {
                HMS.ui.Dashboard dashboard = new HMS.ui.Dashboard(patientManager, doctorManager, appointmentManager,
                        bedManager, billingManager, () -> runGUI());
                dashboard.setVisible(true);
            } else if (user.getRole().equals("DOCTOR")) {
                HMS.ui.DoctorDashboard dashboard = new HMS.ui.DoctorDashboard(
                        user, doctorManager, appointmentManager, patientManager,
                        prescriptionManager, billingManager, reportManager, () -> runGUI());
                dashboard.setVisible(true);
            } else if (user.getRole().equals("PATIENT")) {
                HMS.ui.PatientDashboard dashboard = new HMS.ui.PatientDashboard(
                        user, patientManager, doctorManager, appointmentManager, bedManager,
                        prescriptionManager, billingManager, reportManager, () -> runGUI());
                dashboard.setVisible(true);
            }
        });
    }



    public void runCLI() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            printMainMenu();
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    patientManager.showMenu(sc);
                    break;
                case "2":
                    doctorManager.showMenu(sc);
                    break;
                case "3":
                    appointmentManager.showMenu(sc);
                    break;
                case "4":
                    bedManager.showMenu(sc);
                    break;
                case "0":
                    System.out.println("\nThank you for using HospiCare. Goodbye!");
                    Logger.info("HospiCare application exited.");
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
