package HMS.model;

import HMS.utils.CounterManager;

public class Doctor extends Person {

    // variables
    private String specialization;

    // constructor
    public Doctor(String id, String name, int age, String specialization) {
        super(id, name, age);
        setSpecialization(specialization);
    }

    // create doctor
    public static Doctor create(String name, int age, String specialization) {
        String autoId = CounterManager.getNextDoctorId();
        return new Doctor(autoId, name, age, specialization);
    }

    // getters
    public String getSpecialization() {
        return specialization;
    }

    // setters
    public void setSpecialization(String specialization) {
        if (specialization == null || specialization.trim().isEmpty())
            throw new IllegalArgumentException("Specialization cannot be empty");
        this.specialization = specialization;
    }

    // display doctor info
    public void display() {
        displayBasicInfo();
        System.out.println("Specialization: " + specialization);
        System.out.println("---");
    }
}
