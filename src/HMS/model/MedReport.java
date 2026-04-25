package HMS.model;

import HMS.utils.CounterManager;
import HMS.utils.DateUtil;

/**
 * MedReport — represents a medical report written by a doctor for a patient.
 * Named "MedReport" to avoid conflict with java.lang.reflect.* Report classes.
 *
 * Fields:
 *   id        — auto-generated (e.g. REP001)
 *   patientId — links to patients.id
 *   doctorId  — links to doctors.id
 *   type      — category of report (e.g. "Blood Test", "X-Ray", "Diagnosis")
 *   content   — full text content of the report
 *   date      — date the report was created (yyyy-MM-dd)
 */
public class MedReport {

    private final String id;
    private final String patientId;
    private final String doctorId;
    private final String type;
    private String content;
    private final String date;

    // Full constructor (used when loading from DB)
    public MedReport(String id, String patientId, String doctorId,
                     String type, String content, String date) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.type = type;
        this.content = content;
        this.date = date;
    }

    // Factory method (used when creating new reports)
    public static MedReport create(String patientId, String doctorId,
                                   String type, String content) {
        String autoId = CounterManager.getNextReportId();
        String today = DateUtil.todayFormatted();
        return new MedReport(autoId, patientId, doctorId, type, content, today);
    }

    // Getters
    public String getId()        { return id; }
    public String getPatientId() { return patientId; }
    public String getDoctorId()  { return doctorId; }
    public String getType()      { return type; }
    public String getContent()   { return content; }
    public String getDate()      { return date; }

    // Setter
    public void setContent(String content) { this.content = content; }

    // Display (for CLI)
    public void display() {
        System.out.println("Report ID: " + id + " | Type: " + type);
        System.out.println("Patient: " + patientId + " | Doctor: " + doctorId);
        System.out.println("Date: " + DateUtil.display(date));
        System.out.println("Content: " + content);
        System.out.println("---");
    }
}
