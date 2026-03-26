package HMS.model;

public class Bed {
    private final String id;
    private String wardType;
    private boolean occupied;
    private String patientId;
    private String admitDate;
    private String dischargeDate;

    public Bed(String id, String wardType) {
        this.id = id;
        this.wardType = wardType;
        this.occupied = false;
        this.patientId = "";
        this.admitDate = "";
        this.dischargeDate = "";
    }

    public Bed(String id, String wardType, boolean occupied, String patientId,
               String admitDate, String dischargeDate) {
        this.id = id;
        this.wardType = wardType;
        this.occupied = occupied;
        this.patientId = patientId;
        this.admitDate = admitDate;
        this.dischargeDate = dischargeDate;
    }

    // Getters
    public String getId() { return id; }
    public String getWardType() { return wardType; }
    public boolean isOccupied() { return occupied; }
    public String getPatientId() { return patientId; }
    public String getAdmitDate() { return admitDate; }
    public String getDischargeDate() { return dischargeDate; }

    // Book this bed for a patient
    public void book(String patientId, String admitDate, String dischargeDate) {
        this.occupied = true;
        this.patientId = patientId;
        this.admitDate = admitDate;
        this.dischargeDate = dischargeDate;
    }

    // Discharge - free up the bed
    public void discharge() {
        this.occupied = false;
        this.patientId = "";
        this.admitDate = "";
        this.dischargeDate = "";
    }

    public void display() {
        System.out.print("  " + id + " | " + wardType);
        if (occupied) {
            System.out.println(" | OCCUPIED | Patient: " + patientId
                    + " | Admit: " + admitDate + " | Discharge: " + dischargeDate);
        } else {
            System.out.println(" | AVAILABLE");
        }
    }
}
