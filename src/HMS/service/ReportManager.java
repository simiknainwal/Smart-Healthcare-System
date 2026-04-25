package HMS.service;

import HMS.db.MedReportDAO;
import HMS.model.MedReport;
import HMS.utils.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ReportManager — Business logic for managing medical reports.
 * Keeps an in-memory list (loaded from DB on startup) and delegates
 * persistence to MedReportDAO.
 */
public class ReportManager {

    private final ArrayList<MedReport> reports;
    private final MedReportDAO reportDAO;

    public ReportManager(ArrayList<MedReport> reports, MedReportDAO reportDAO) {
        this.reports = reports;
        this.reportDAO = reportDAO;
    }

    public ArrayList<MedReport> getReports() {
        return reports;
    }

    // ==================== OPERATIONS ====================

    /**
     * Creates and saves a new medical report.
     * Called by the Doctor Dashboard when writing a patient report.
     */
    public MedReport addReport(String patientId, String doctorId,
                               String type, String content) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Report type cannot be empty.");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Report content cannot be empty.");
        }

        MedReport r = MedReport.create(patientId, doctorId, type.trim(), content.trim());
        reports.add(r);
        reportDAO.insert(r);

        Logger.info("MedReport created: " + r.getId()
                + " for patient " + patientId + " by doctor " + doctorId);
        return r;
    }

    /**
     * Returns all reports for a given patient.
     * Used by the Patient Dashboard's "My Reports" tab.
     */
    public List<MedReport> getReportsByPatient(String patientId) {
        return reports.stream()
                .filter(r -> r.getPatientId().equalsIgnoreCase(patientId))
                .collect(Collectors.toList());
    }
}
