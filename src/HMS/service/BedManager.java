package HMS.service;

import HMS.db.BedDAO;
import HMS.model.*;
import HMS.utils.CounterManager;
import HMS.utils.DateUtil;
import HMS.utils.Logger;
import java.util.ArrayList;
import java.util.Scanner;

public class BedManager {

    // variables
    private final ArrayList<Bed> beds;
    private final PatientManager patientManager;
    private final BedDAO bedDAO;

    // Fixed stay duration for each ward type
    private static final int GENERAL_STAY = 7;
    private static final int ICU_STAY = 3;
    private static final int PRIVATE_STAY = 10;
    private static final int SEMI_PRIVATE_STAY = 5;

    // constructor
    public BedManager(ArrayList<Bed> beds, PatientManager patientManager, BedDAO bedDAO) {
        this.beds = beds;
        this.patientManager = patientManager;
        this.bedDAO = bedDAO;
    }

    public ArrayList<Bed> getBeds() {
        return beds;
    }

    /** Called by UI panels to persist a bed update to the database. */
    public void updateBedInDB(Bed b) {
        bedDAO.update(b);
    }

    /** Called by UI panels to insert a new bed into the database. */
    public void insertBedInDB(Bed b) {
        bedDAO.insert(b);
    }

    // _________OPERATIONS_________

    // show menu
    public void showMenu(Scanner sc) {
        while (true) {
            System.out.println("\n┌──────────────────────────────────────────┐");
            System.out.println("│           BED MANAGEMENT                 │");
            System.out.println("├──────────────────────────────────────────┤");
            System.out.println("│   1. View All Beds                       │");
            System.out.println("│   2. Check Availability by Ward          │");
            System.out.println("│   3. Book a Bed                          │");
            System.out.println("│   4. Discharge Patient (Free Bed)        │");
            System.out.println("│   5. Add New Bed                         │");
            System.out.println("│                                          │");
            System.out.println("│   0. Back to Main Menu                   │");
            System.out.println("└──────────────────────────────────────────┘");
            System.out.print("  Enter your choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    viewAllBeds();
                    break;
                case "2":
                    checkAvailability(sc);
                    break;
                case "3":
                    bookBed(sc);
                    break;
                case "4":
                    dischargeBed(sc);
                    break;
                case "5":
                    addBed(sc);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    // view all beds
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

    // check availability
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

    // book bed
    private void bookBed(Scanner sc) {
        try {
            System.out.print("  Patient ID: ");
            String patientId = sc.nextLine().trim();

            Patient patient = patientManager.findPatient(patientId);
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
                case "1":
                    wardType = "General";
                    stayDays = GENERAL_STAY;
                    break;
                case "2":
                    wardType = "ICU";
                    stayDays = ICU_STAY;
                    break;
                case "3":
                    wardType = "Private";
                    stayDays = PRIVATE_STAY;
                    break;
                case "4":
                    wardType = "Semi-Private";
                    stayDays = SEMI_PRIVATE_STAY;
                    break;
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

            // ── Admit date (default: today) ──────────────────────────────────
            String todayStr = DateUtil.todayFormatted();
            System.out.print("  Admit Date [" + todayStr + "]: ");
            String admitInput = sc.nextLine().trim();

            String admitDate;
            if (admitInput.isEmpty()) {
                admitDate = todayStr;
            } else {
                try {
                    DateUtil.parse(admitInput); // validate
                    admitDate = admitInput;
                } catch (IllegalArgumentException e) {
                    System.out.println("  " + e.getMessage());
                    return;
                }
            }

            // Calculate discharge date using java.time
            String dischargeDate = DateUtil.addDays(admitDate, stayDays);

            availableBed.book(patient.getId(), admitDate, dischargeDate);
            bedDAO.update(availableBed);  // Save to database

            System.out.println("\n  Bed booked successfully!");
            System.out.println("  Bed ID: " + availableBed.getId());
            System.out.println("  Ward: " + wardType);
            System.out.println("  Patient: " + patient.getName());
            System.out.println("  Admit Date: " + DateUtil.display(admitDate));
            System.out.println("  Expected Discharge: " + DateUtil.display(dischargeDate) + " (" + stayDays + " days)");

        } catch (Exception e) {
            System.out.println("  Booking error: " + e.getMessage());
            Logger.error("Bed booking error: " + e.getMessage());
        }
    }

    // discharge bed
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
                Patient patient = patientManager.findPatient(patientId);
                String patientName = (patient != null) ? patient.getName() : patientId;

                b.discharge();
                bedDAO.update(b);  // Save to database
                System.out.println("  Patient " + patientName + " discharged from Bed " + bedId + ".");
                System.out.println("  Bed " + bedId + " is now AVAILABLE.");
                return;
            }
        }
        System.out.println("  Bed ID " + bedId + " not found!");
    }

    // add bed
    private void addBed(Scanner sc) {
        System.out.println("\n  Select Ward Type for new bed:");
        System.out.println("    1. General");
        System.out.println("    2. ICU");
        System.out.println("    3. Private");
        System.out.println("    4. Semi-Private");
        System.out.print("  Choose ward: ");
        String wardChoice = sc.nextLine().trim();

        String wardType;
        switch (wardChoice) {
            case "1":
                wardType = "General";
                break;
            case "2":
                wardType = "ICU";
                break;
            case "3":
                wardType = "Private";
                break;
            case "4":
                wardType = "Semi-Private";
                break;
            default:
                System.out.println("  Invalid ward selection!");
                return;
        }

        String bedId = CounterManager.getNextBedId();
        Bed newBed = new Bed(bedId, wardType);
        beds.add(newBed);
        bedDAO.insert(newBed);  // Save to database

        System.out.println("\n  New bed added successfully!");
        System.out.println("  Bed ID: " + bedId);
        System.out.println("  Ward: " + wardType);
        System.out.println("  Status: AVAILABLE");
    }
}
