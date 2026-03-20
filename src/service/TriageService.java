package service;

import exception.DuplicatePatientException;
import exception.PatientNotFoundException;
import model.Patient;
import model.TriageLevel;

import java.util.*;
import java.util.stream.Collectors;

public class TriageService {
    private final PriorityQueue<Patient>  waitingQueue    = new PriorityQueue<>();
    private final Map<String, Patient>    patientRegistry = new LinkedHashMap<>();
    private final List<Patient>           allPatients     = new ArrayList<>();

    public void admitPatient(Patient p) throws DuplicatePatientException {
        if (patientRegistry.containsKey(p.getPatientId()))
            throw new DuplicatePatientException(p.getPatientId());
        patientRegistry.put(p.getPatientId(), p);
        waitingQueue.offer(p);
        allPatients.add(p);
    }

    public Patient callNextPatient() throws PatientNotFoundException {
        Patient p = waitingQueue.poll();
        if (p == null) throw new PatientNotFoundException("QUEUE_EMPTY");
        p.setStatus(Patient.PatientStatus.IN_TREATMENT);
        return p;
    }

    public void dischargePatient(String id) throws PatientNotFoundException {
        Patient p = findPatient(id);
        p.setStatus(Patient.PatientStatus.DISCHARGED);
        waitingQueue.remove(p);
    }

    public Patient findPatient(String id) throws PatientNotFoundException {
        Patient p = patientRegistry.get(id.toUpperCase());
        if (p == null) throw new PatientNotFoundException(id);
        return p;
    }

    public List<Patient> getWaitingPatients() {
        return waitingQueue.stream().sorted().collect(Collectors.toList());
    }

    public Map<TriageLevel, Long> getTriageLevelStats() {
        return allPatients.stream().collect(Collectors.groupingBy(Patient::getTriageLevel, Collectors.counting()));
    }

    public int          getWaitingCount() { return waitingQueue.size(); }
    public List<Patient> getAllPatients() { return Collections.unmodifiableList(allPatients); }
}
