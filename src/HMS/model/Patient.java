package HMS.model;

import HMS.system.CounterManager;

public class Patient extends Person {

    // variables
    private String disease;

    // constructor
    public Patient(String id, String name, int age, String disease) {
        super(id, name, age);
        setDisease(disease);
    }

    // create patient
    public static Patient create(String name, int age, String disease) {
        String autoId = CounterManager.getNextPatientId();
        return new Patient(autoId, name, age, disease);
    }

    // getters
    public String getDisease() {
        return disease;
    }

    // setters
    public void setDisease(String disease) {
        if (disease == null || disease.trim().isEmpty())
            throw new IllegalArgumentException("Disease cannot be empty");
        this.disease = disease;
    }

    // display patient info
    public void display() {
        displayBasicInfo();
        System.out.println("Disease: " + disease);
        System.out.println("---");
    }
}
