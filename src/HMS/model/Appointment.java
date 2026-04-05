package HMS.model;

import HMS.system.CounterManager;

public class Appointment {
    private final String id;
    private final String patientId;
    private final String doctorId;
    private String date;
    private String status;
    private String priority;

    public Appointment(String id, String patientId, String doctorId, String date) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.status = "Scheduled";
        this.priority = "Normal";
    }

    public static Appointment create(String patientId, String doctorId, String date) {
        String autoId = CounterManager.getNextAppointmentId();
        return new Appointment(autoId, patientId, doctorId, date);
    }

    public String getId() { return id; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public String getDate() { return date; }
    public String getStatus() { return status; }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority()
    {
        return priority;
    }

    public void setPriority(String priority)
    {
        this.priority = priority;
    }
    
    public void display() {
        System.out.println("Appointment ID: " + id);
        System.out.println("Patient ID: " + patientId + ", Doctor ID: " + doctorId);
        System.out.println("Date: " + date + ", Status: " + status + ", Priority: " + priority);
        System.out.println("---");
    }
}
