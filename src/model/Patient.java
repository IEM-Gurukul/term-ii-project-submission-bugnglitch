package model;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Patient implements Comparable<Patient> {

    public enum PatientStatus { WAITING, IN_TREATMENT, DISCHARGED, TRANSFERRED }

    private final String patientId = UUID.randomUUID().toString().substring(0,8).toUpperCase();
    private String name;
    private final int age;
    private final String bloodType;
    private TriageLevel triageLevel;
    private final LocalDateTime arrivalTime = LocalDateTime.now();
    private PatientStatus status = PatientStatus.WAITING;

    public Patient(String name, int age, String bloodType) {
        this.name = name; this.age = age; this.bloodType = bloodType;
    }

    public abstract TriageLevel assessTriageLevel();
    public abstract String getClinicalSummary();

    @Override
    public int compareTo(Patient o) {
        return Integer.compare(triageLevel.getPriority(), o.triageLevel.getPriority());
    }

    public String        getPatientId()   { return patientId; }
    public String        getName()        { return name; }
    public int           getAge()         { return age; }
    public String        getBloodType()   { return bloodType; }
    public TriageLevel   getTriageLevel() { return triageLevel; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public PatientStatus getStatus()      { return status; }

    public void setTriageLevel(TriageLevel l) { triageLevel = l; }
    public void setStatus(PatientStatus s)    { status = s; }
    public void setName(String n)             { name = n; }

    @Override
    public String toString() {
        return String.format("[%s] %s (Age:%d) | %s | %s", patientId, name, age, triageLevel, status);
    }
}
