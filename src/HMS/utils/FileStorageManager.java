package HMS.utils;

import HMS.model.*;
import java.io.*;
import java.util.*;

public class FileStorageManager {
    private static final String PATIENTS_FILE = "patients.txt";
    private static final String DOCTORS_FILE = "doctors.txt";
    private static final String APPOINTMENTS_FILE = "appointments.txt";
    private static final String BEDS_FILE = "beds.txt";

    public void savePatients(List<Patient> patients) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PATIENTS_FILE))) {
            for (Patient p : patients) {
                writer.println(p.getId() + "|" + p.getName() + "|" + p.getAge() + "|" + p.getDisease());
            }
        } catch (IOException e) {
            System.err.println("Save patients failed: " + e.getMessage());
        }
    }

    public void saveDoctors(List<Doctor> doctors) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DOCTORS_FILE))) {
            for (Doctor d : doctors) {
                writer.println(d.getId() + "|" + d.getName() + "|" + d.getAge() + "|" + d.getSpecialization());
            }
        } catch (IOException e) {
            System.err.println("Save doctors failed: " + e.getMessage());
        }
    }

    public void saveAppointments(List<Appointment> appointments) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(APPOINTMENTS_FILE))) {
            for (Appointment a : appointments) {
                writer.println(a.getId() + "|" + a.getPatientId() + "|" + a.getDoctorId() +
                        "|" + a.getDate() + "|" + a.getStatus());
            }
        } catch (IOException e) {
            System.err.println("Save appointments failed: " + e.getMessage());
        }
    }

    public void saveBeds(List<Bed> beds) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(BEDS_FILE))) {
            for (Bed b : beds) {
                String status = b.isOccupied() ? "Occupied" : "Available";
                writer.println(b.getId() + "|" + b.getWardType() + "|" + status + "|"
                        + b.getPatientId() + "|" + b.getAdmitDate() + "|" + b.getDischargeDate());
            }
        } catch (IOException e) {
            System.err.println("Save beds failed: " + e.getMessage());
        }
    }

    public List<Patient> loadPatients() {
        List<Patient> patients = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(PATIENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 4) {
                    patients.add(new Patient(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3]));
                }
            }
        } catch (Exception e) {
            System.err.println("Load patients failed: " + e.getMessage());
        }
        return patients;
    }

    public List<Doctor> loadDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DOCTORS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 4) {
                    doctors.add(new Doctor(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3]));
                }
            }
        } catch (Exception e) {
            System.err.println("Load doctors failed: " + e.getMessage());
        }
        return doctors;
    }

    public List<Appointment> loadAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(APPOINTMENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 5) {
                    Appointment a = new Appointment(parts[0], parts[1], parts[2], parts[3]);
                    a.setStatus(parts[4]);
                    appointments.add(a);
                }
            }
        } catch (Exception e) {
            System.err.println("Load appointments failed: " + e.getMessage());
        }
        return appointments;
    }

    public List<Bed> loadBeds() {
        List<Bed> beds = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(BEDS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (parts.length == 6) {
                    boolean occupied = parts[2].equals("Occupied");
                    beds.add(new Bed(parts[0], parts[1], occupied, parts[3], parts[4], parts[5]));
                }
            }
        } catch (Exception e) {
            System.err.println("Load beds failed: " + e.getMessage());
        }
        return beds;
    }
}
