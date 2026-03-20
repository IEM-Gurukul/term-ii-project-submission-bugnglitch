package service;

import model.Patient;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FileService {
    private static final String DIR = "records";
    private static final String LOG = DIR + "/er_log.txt";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public FileService() {
        try { Files.createDirectories(Paths.get(DIR)); } catch (IOException ignored) {}
    }

    public void logActivity(String msg) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOG, true))) {
            bw.write("[" + LocalDateTime.now().format(FMT) + "] " + msg);
            bw.newLine();
        } catch (IOException e) { System.err.println("Log error: " + e.getMessage()); }
    }

    public void exportPatientsToCSV(List<Patient> patients, String filename) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DIR + "/" + filename))) {
            pw.println("PatientID,Name,Age,BloodType,TriageLevel,Status,ArrivalTime,ClinicalSummary");
            for (Patient p : patients)
                pw.printf("%s,\"%s\",%d,%s,%s,%s,%s,\"%s\"%n",
                    p.getPatientId(), p.getName(), p.getAge(), p.getBloodType(),
                    p.getTriageLevel(), p.getStatus(),
                    p.getArrivalTime().format(FMT), p.getClinicalSummary().replace("\"","'"));
        }
    }
}
