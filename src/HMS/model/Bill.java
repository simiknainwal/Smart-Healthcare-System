package HMS.model;

import HMS.utils.CounterManager;
import HMS.utils.DateUtil;

/**
 * Bill — represents a financial bill issued to a patient.
 *
 * Fields:
 *   id          — auto-generated (e.g. BIL001)
 *   patientId   — links to patients.id
 *   amount      — total amount in currency units
 *   description — reason for the bill (e.g. "Consultation fee", "Bed charge")
 *   date        — date of billing (yyyy-MM-dd)
 *   status      — either "UNPAID" or "PAID"
 */
public class Bill {

    public static final String UNPAID = "UNPAID";
    public static final String PAID   = "PAID";

    private final String id;
    private final String patientId;
    private final double amount;
    private final String description;
    private final String date;
    private String status;

    // Full constructor (used when loading from DB)
    public Bill(String id, String patientId, double amount,
                String description, String date, String status) {
        this.id = id;
        this.patientId = patientId;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.status = status;
    }

    // Factory method (used when creating new bills — always starts as UNPAID)
    public static Bill create(String patientId, double amount, String description) {
        String autoId = CounterManager.getNextBillId();
        String today = DateUtil.todayFormatted();
        return new Bill(autoId, patientId, amount, description, today, UNPAID);
    }

    // Getters
    public String getId()          { return id; }
    public String getPatientId()   { return patientId; }
    public double getAmount()      { return amount; }
    public String getDescription() { return description; }
    public String getDate()        { return date; }
    public String getStatus()      { return status; }

    public boolean isPaid() { return PAID.equals(status); }

    // Setter
    public void setStatus(String status) { this.status = status; }

    // Convenience method
    public void markAsPaid() { this.status = PAID; }

    // Display (for CLI)
    public void display() {
        System.out.println("Bill ID: " + id);
        System.out.println("Patient: " + patientId);
        System.out.printf("Amount: %.2f | Description: %s%n", amount, description);
        System.out.println("Date: " + DateUtil.display(date) + " | Status: " + status);
        System.out.println("---");
    }
}
