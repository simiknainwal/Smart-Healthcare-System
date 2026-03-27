#HospiCare — Hospital Management System

A console-based Hospital Management System built in Java. Manages patients, doctors, appointments, and bed allocations with file-based persistent storage.

---

## Features

- Add, view, search, update, and remove patients and doctors
- Schedule appointments with smart doctor suggestions based on patient disease
- Manage hospital bed allocations across 4 ward types
- File-based persistent storage — data is saved and reloaded between sessions
- Auto-incrementing IDs for all entities

---

## Project Structure

```
src/
└── HMS/
    ├── Main.java                        # Entry point
    ├── model/
    │   ├── Person.java                  # Abstract base class for Doctor and Patient
    │   ├── Patient.java                 # Patient entity
    │   ├── Doctor.java                  # Doctor entity
    │   ├── Appointment.java             # Appointment entity
    │   └── Bed.java                     # Bed entity 
    └── system/
        ├── Hospital.java                
        ├── PatientManager.java          # CRUD operations and menu for patients
        ├── DoctorManager.java           # CRUD operations and menu for doctors
        ├── AppointmentManager.java      # Appointment scheduling and listing
        ├── BedManager.java              # Bed booking, availability check, and discharge
        ├── DoctorDirectory.java         # Disease-to-specialization mapping and doctor suggestion
        ├── CounterManager.java          # Auto-increment ID counters
        └── FileStorageManager.java      # Reads and writes all data to text files.
```

---

## Getting Started

**Prerequisites:** Java 8 or higher

**Compile:**
```bash
javac -d out src/HMS/Main.java src/HMS/model/*.java src/HMS/system/*.java
```

**Run:**
```bash
java -cp out HMS.Main
```
---

## Modules

### Patient Management
Add, view, search, update, and remove patients. Each patient stores a name, age, and disease. A unique ID (`PAT001`, `PAT002`, …) is auto-assigned on creation.

### Doctor Management
Add, view, search, update, and remove doctors. Each doctor stores a name, age, and specialization. A unique ID (`DOC001`, `DOC002`, …) is auto-assigned on creation.

### Appointment Management
Schedule appointments between a patient and a doctor. When scheduling, the system checks the patient's disease against the `DoctorDirectory` and suggests a matching doctor automatically — you can confirm or override. If no match is found, the full doctor list is shown. Appointments get IDs like `APP001` and start with status `Scheduled`.

### Bed Management
Book and discharge beds across 4 ward types. The discharge date is calculated automatically based on fixed ward stay durations. A patient cannot be booked into more than one bed at a time.

| Ward | Default Stay |
|------|-------------|
| General | 7 days |
| ICU | 3 days |
| Private | 10 days |
| Semi-Private | 5 days |

---

## Doctor Directory

When scheduling an appointment, the system maps the patient's disease to a specialization and suggests a matching doctor from the registered list.

| Specialization | Conditions |
|---------------|-----------|
| General Physician | Fever, cold, cough, flu, headache, weakness, body pain |
| Cardiologist | Chest pain, heart attack, high BP, low BP, heart disease |
| Orthopedic | Fracture, joint pain, back pain, bone injury, sprain |
| Dermatologist | Skin rash, acne, allergy, eczema, skin infection |
| Psychiatrist | Anxiety, depression, stress, insomnia |
| Dentist | Toothache, cavity, gum pain, tooth decay |
| Ophthalmologist | Eye pain, blurred vision, eye infection |
| ENT Specialist | Ear pain, hearing loss, sore throat, tonsils, nose block |
| Endocrinologist | Diabetes, thyroid, hormonal imbalance |
| Gastroenterologist | Stomach pain, acidity, vomiting, diarrhea, food poisoning |

---

## Data Persistence

All data is stored in .txt files in the working directory, saved automatically after every operation.

| File | Contents |
|------|----------|
| `patients.txt` | Patient records |
| `doctors.txt` | Doctor records |
| `appointments.txt` | Appointment records |
| `beds.txt` | Bed records including occupancy state |
| `counters.txt` | Current ID counters for each entity |

**Storage format:**
```
patients.txt     →  PAT001|John Doe|35|Diabetes
doctors.txt      →  DOC001|Dr. Smith|45|Endocrinologist
appointments.txt →  APP001|PAT001|DOC001|2026-04-01|Scheduled
beds.txt         →  BED001|ICU|Occupied|PAT001|2026-03-27|2026-03-30
counters.txt     →  PATIENT=1
```
