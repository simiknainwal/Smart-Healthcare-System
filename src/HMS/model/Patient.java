package HMS.model;

import HMS.system.CounterManager;

public class Patient extends Person {
    private String disease;

    public Patient(String id, String name, int age, String disease) {
        super(id, name, age);
        setDisease(disease);
    }

    public static Patient create(String name, int age, String disease) {
        String autoId = CounterManager.getNextPatientId();
        return new Patient(autoId, name, age, disease);
    }

    public String getDisease() { return disease; }
    public void setDisease(String disease) {
        if (disease == null || disease.trim().isEmpty())
            throw new IllegalArgumentException("Disease cannot be empty");
        this.disease = disease;
    }

    public void display() {
        displayBasicInfo();
        System.out.println("Disease: " + disease);
        System.out.println("---");
    }
}
