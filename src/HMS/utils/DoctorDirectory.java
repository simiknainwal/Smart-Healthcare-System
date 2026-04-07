package HMS.utils;

import HMS.model.Doctor;
import java.util.ArrayList;
import java.util.HashMap;

public class DoctorDirectory {

    // Maps common diseases/symptoms to their specialization
    private static final HashMap<String, String> diseaseToSpecialization = new HashMap<>();

    static {
        // General Physician
        diseaseToSpecialization.put("fever", "General Physician");
        diseaseToSpecialization.put("cold", "General Physician");
        diseaseToSpecialization.put("cough", "General Physician");
        diseaseToSpecialization.put("headache", "General Physician");
        diseaseToSpecialization.put("flu", "General Physician");
        diseaseToSpecialization.put("weakness", "General Physician");
        diseaseToSpecialization.put("body pain", "General Physician");

        // Cardiologist
        diseaseToSpecialization.put("chest pain", "Cardiologist");
        diseaseToSpecialization.put("heart attack", "Cardiologist");
        diseaseToSpecialization.put("high bp", "Cardiologist");
        diseaseToSpecialization.put("low bp", "Cardiologist");
        diseaseToSpecialization.put("heart disease", "Cardiologist");

        // Orthopedic
        diseaseToSpecialization.put("fracture", "Orthopedic");
        diseaseToSpecialization.put("joint pain", "Orthopedic");
        diseaseToSpecialization.put("back pain", "Orthopedic");
        diseaseToSpecialization.put("bone injury", "Orthopedic");
        diseaseToSpecialization.put("sprain", "Orthopedic");

        // Dermatologist
        diseaseToSpecialization.put("skin rash", "Dermatologist");
        diseaseToSpecialization.put("acne", "Dermatologist");
        diseaseToSpecialization.put("allergy", "Dermatologist");
        diseaseToSpecialization.put("eczema", "Dermatologist");
        diseaseToSpecialization.put("skin infection", "Dermatologist");

        // Psychiatrist
        diseaseToSpecialization.put("anxiety", "Psychiatrist");
        diseaseToSpecialization.put("depression", "Psychiatrist");
        diseaseToSpecialization.put("stress", "Psychiatrist");
        diseaseToSpecialization.put("insomnia", "Psychiatrist");

        // Dentist
        diseaseToSpecialization.put("toothache", "Dentist");
        diseaseToSpecialization.put("cavity", "Dentist");
        diseaseToSpecialization.put("gum pain", "Dentist");
        diseaseToSpecialization.put("tooth decay", "Dentist");

        // Ophthalmologist
        diseaseToSpecialization.put("eye pain", "Ophthalmologist");
        diseaseToSpecialization.put("blurred vision", "Ophthalmologist");
        diseaseToSpecialization.put("eye infection", "Ophthalmologist");

        // ENT Specialist
        diseaseToSpecialization.put("ear pain", "ENT Specialist");
        diseaseToSpecialization.put("hearing loss", "ENT Specialist");
        diseaseToSpecialization.put("sore throat", "ENT Specialist");
        diseaseToSpecialization.put("tonsils", "ENT Specialist");
        diseaseToSpecialization.put("nose block", "ENT Specialist");

        // Endocrinologist
        diseaseToSpecialization.put("diabetes", "Endocrinologist");
        diseaseToSpecialization.put("thyroid", "Endocrinologist");
        diseaseToSpecialization.put("hormonal imbalance", "Endocrinologist");

        // Gastroenterologist
        diseaseToSpecialization.put("stomach pain", "Gastroenterologist");
        diseaseToSpecialization.put("acidity", "Gastroenterologist");
        diseaseToSpecialization.put("vomiting", "Gastroenterologist");
        diseaseToSpecialization.put("diarrhea", "Gastroenterologist");
        diseaseToSpecialization.put("food poisoning", "Gastroenterologist");
    }

    // Find the specialization for a given disease
    private static String getSpecialization(String disease) {
        if (disease == null) return null;
        return diseaseToSpecialization.get(disease.toLowerCase().trim());
    }

    // Find a matching doctor from the list based on patient's disease
    public static Doctor suggestDoctor(String disease, ArrayList<Doctor> doctors) {
        String specialization = getSpecialization(disease);
        if (specialization == null) return null;

        for (Doctor d : doctors) {
            if (d.getSpecialization().equalsIgnoreCase(specialization)) {
                return d;
            }
        }
        return null;
    }
}
