package HMS.service;

import HMS.db.PrescriptionDAO;
import HMS.model.Prescription;
import HMS.utils.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PrescriptionManager — Business logic for managing prescriptions.
 * Keeps an in-memory list (loaded from DB on startup) and delegates
 * persistence to PrescriptionDAO.
 */
public class PrescriptionManager {

    private final ArrayList<Prescription> prescriptions;
    private final PrescriptionDAO prescriptionDAO;

    public PrescriptionManager(ArrayList<Prescription> prescriptions, PrescriptionDAO prescriptionDAO) {
        this.prescriptions = prescriptions;
        this.prescriptionDAO = prescriptionDAO;
    }

    public ArrayList<Prescription> getPrescriptions() {
        return prescriptions;
    }

    // ==================== OPERATIONS ====================

    /**
     * Creates and saves a new prescription.
     * Called by the Doctor Dashboard when a doctor prescribes medication.
     */
    public Prescription addPrescription(String patientId, String doctorId,
                                        String medication, String dosage) {
        if (medication == null || medication.trim().isEmpty()) {
            throw new IllegalArgumentException("Medication name cannot be empty.");
        }
        if (dosage == null || dosage.trim().isEmpty()) {
            throw new IllegalArgumentException("Dosage instructions cannot be empty.");
        }

        Prescription p = Prescription.create(patientId, doctorId, medication.trim(), dosage.trim());
        prescriptions.add(p);
        prescriptionDAO.insert(p);

        Logger.info("Prescription created: " + p.getId()
                + " for patient " + patientId + " by doctor " + doctorId);
        return p;
    }

    /**
     * Returns all prescriptions for a given patient.
     * Used by the Patient Dashboard's "My Prescriptions" tab.
     */
    public List<Prescription> getPrescriptionsByPatient(String patientId) {
        return prescriptions.stream()
                .filter(p -> p.getPatientId().equalsIgnoreCase(patientId))
                .collect(Collectors.toList());
    }

    /**
     * Returns all prescriptions written by a given doctor.
     * Used by the Doctor Dashboard's "Prescribe" tab history.
     */
    public List<Prescription> getPrescriptionsByDoctor(String doctorId) {
        return prescriptions.stream()
                .filter(p -> p.getDoctorId().equalsIgnoreCase(doctorId))
                .collect(Collectors.toList());
    }
}
