package HMS.model;

import HMS.system.CounterManager;
import HMS.system.DateUtil;

public class Appointment {

    // variables
    private final String id;
    private final String patientId;
    private final String doctorId;
    private String date;
    private String status;

    // constructor
    public Appointment(String id, String patientId, String doctorId, String date) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.status = "Scheduled";
    }

    // create appointment
    public static Appointment create(String patientId, String doctorId, String date) {
        String autoId = CounterManager.getNextAppointmentId();
        return new Appointment(autoId, patientId, doctorId, date);
    }

    // getters
    public String getId() {
        return id;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    // setters
    public void setDate(String date) {
        this.date = date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // display appointment info
    public void display() {
        System.out.println("Appointment ID: " + id);
        System.out.println("Patient ID: " + patientId + ", Doctor ID: " + doctorId);
        System.out.println("Date: " + DateUtil.display(date) + ", Status: " + status);
        System.out.println("---");
    }
}
