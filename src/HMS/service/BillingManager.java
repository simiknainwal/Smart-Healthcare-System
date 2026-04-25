package HMS.service;

import HMS.db.BillDAO;
import HMS.model.Bill;
import HMS.utils.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * BillingManager — Business logic for managing patient bills.
 * Keeps an in-memory list (loaded from DB on startup) and delegates
 * persistence to BillDAO.
 */
public class BillingManager {

    private final ArrayList<Bill> bills;
    private final BillDAO billDAO;

    public BillingManager(ArrayList<Bill> bills, BillDAO billDAO) {
        this.bills = bills;
        this.billDAO = billDAO;
    }

    public ArrayList<Bill> getBills() {
        return bills;
    }

    // ==================== OPERATIONS ====================

    /**
     * Creates and saves a new bill for a patient.
     * Called automatically when a prescription is written, or manually by admin.
     */
    public Bill generateBill(String patientId, double amount, String description) {
        if (patientId == null || patientId.trim().isEmpty()) {
            throw new IllegalArgumentException("Patient ID cannot be empty.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Bill amount must be greater than zero.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Bill description cannot be empty.");
        }

        Bill b = Bill.create(patientId, amount, description.trim());
        bills.add(b);
        billDAO.insert(b);

        Logger.info("Bill generated: " + b.getId()
                + " for patient " + patientId + " — " + description + " (₹" + amount + ")");
        return b;
    }

    /**
     * Marks a bill as PAID and persists the change.
     * Called by the Admin Dashboard's "Billing" panel.
     */
    public boolean markAsPaid(String billId) {
        for (Bill b : bills) {
            if (b.getId().equalsIgnoreCase(billId)) {
                if (b.isPaid()) {
                    return false; // Already paid — no-op
                }
                b.markAsPaid();
                billDAO.update(b);
                Logger.info("Bill marked as PAID: " + billId);
                return true;
            }
        }
        return false; // Not found
    }

    /**
     * Returns all bills for a given patient.
     * Used by the Patient Dashboard's "My Bills" tab.
     */
    public List<Bill> getBillsByPatient(String patientId) {
        return bills.stream()
                .filter(b -> b.getPatientId().equalsIgnoreCase(patientId))
                .collect(Collectors.toList());
    }

    /**
     * Returns all bills — used by the Admin Dashboard's "Billing" overview.
     */
    public List<Bill> getAll() {
        return bills;
    }
}
