package HMS.model;

import HMS.utils.CounterManager;
import HMS.utils.DateUtil;

/**
 * Prescription — represents a medication prescription issued by a doctor to a patient.
 *
 * Fields:
 *   id          — auto-generated (e.g. PRE001)
 *   patientId   — links to patients.id
 *   doctorId    — links to doctors.id
 *   medication  — name of the prescribed medicine
 *   dosage      — dosage instructions (e.g. "1 tablet twice daily")
 *   date        — date of prescription (yyyy-MM-dd)
 */
public class Prescription {

    private final String id;
    private final String patientId;
    private final String doctorId;
    private String medication;
    private String dosage;
    private final String date;

    // Full constructor (used when loading from DB)
    public Prescription(String id, String patientId, String doctorId,
                        String medication, String dosage, String date) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.medication = medication;
        this.dosage = dosage;
        this.date = date;
    }

    // Factory method (used when creating new prescriptions)
    public static Prescription create(String patientId, String doctorId,
                                      String medication, String dosage) {
        String autoId = CounterManager.getNextPrescriptionId();
        String today = DateUtil.todayFormatted();
        return new Prescription(autoId, patientId, doctorId, medication, dosage, today);
    }

    // Getters
    public String getId()         { return id; }
    public String getPatientId()  { return patientId; }
    public String getDoctorId()   { return doctorId; }
    public String getMedication() { return medication; }
    public String getDosage()     { return dosage; }
    public String getDate()       { return date; }

    // Setters
    public void setMedication(String medication) { this.medication = medication; }
    public void setDosage(String dosage)         { this.dosage = dosage; }

    // Display (for CLI)
    public void display() {
        System.out.println("Prescription ID: " + id);
        System.out.println("Patient: " + patientId + " | Doctor: " + doctorId);
        System.out.println("Medication: " + medication + " | Dosage: " + dosage);
        System.out.println("Date: " + DateUtil.display(date));
        System.out.println("---");
    }
}
