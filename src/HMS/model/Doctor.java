package HMS.model;
import HMS.system.CounterManager;
public class Doctor extends Person {
    private String specialization;

    public Doctor(String id, String name, int age, String specialization) {
        super(id, name, age);
        setSpecialization(specialization);
    }

    public static Doctor create(String name, int age, String specialization) {
        String autoId = CounterManager.getNextDoctorId();
        return new Doctor(autoId, name, age, specialization);
    }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) {
        if (specialization == null || specialization.trim().isEmpty())
            throw new IllegalArgumentException("Specialization cannot be empty");
        this.specialization = specialization;
    }

    public void display() {
        displayBasicInfo();
        System.out.println("Specialization: " + specialization);
        System.out.println("---");
    }
}
