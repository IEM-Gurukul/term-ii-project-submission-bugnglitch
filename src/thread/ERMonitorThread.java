package thread;

import model.Patient;
import model.TriageLevel;
import service.FileService;
import service.TriageService;

public class ERMonitorThread implements Runnable {
    private static final int INTERVAL = 5000, CRITICAL_S = 120, EMERGENT_S = 300;

    private final TriageService triageService;
    private final FileService   fileService;
    private volatile boolean    running = true;

    public ERMonitorThread(TriageService ts, FileService fs) { triageService = ts; fileService = fs; }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(INTERVAL);
                long now = System.currentTimeMillis();
                for (Patient p : triageService.getWaitingPatients()) {
                    long waited = (now - java.sql.Timestamp.valueOf(p.getArrivalTime()).getTime()) / 1000;
                    if (p.getTriageLevel() == TriageLevel.RESUSCITATION && waited > CRITICAL_S)
                        fileService.logActivity("ALERT: " + p.getName() + " [" + p.getPatientId() + "] waiting " + waited + "s!");
                    else if (p.getTriageLevel() == TriageLevel.EMERGENT && waited > EMERGENT_S)
                        fileService.logActivity("ALERT: " + p.getName() + " [" + p.getPatientId() + "] (EMERGENT) " + waited + "s");
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
        }
    }

    public void stop() { running = false; }
}
