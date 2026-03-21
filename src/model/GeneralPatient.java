package model;

public class GeneralPatient extends Patient {
    private final String chiefComplaint;
    private final int    painScale, respiratoryRate;
    private final double temperature;

    public GeneralPatient(String name, int age, String blood,
                          String complaint, int pain, double temp, int rr) {
        super(name, age, blood);
        chiefComplaint = complaint; painScale = pain; temperature = temp; respiratoryRate = rr;
        setTriageLevel(assessTriageLevel());
    }

    @Override
    public TriageLevel assessTriageLevel() {
        if (respiratoryRate < 8 || respiratoryRate > 30 || temperature > 40.5) return TriageLevel.RESUSCITATION;
        if (painScale >= 8 || temperature > 39.5)  return TriageLevel.EMERGENT;
        if (painScale >= 5 || temperature > 38.5)  return TriageLevel.URGENT;
        if (painScale >= 3)                         return TriageLevel.LESS_URGENT;
        return TriageLevel.NON_URGENT;
    }

    @Override
    public String getClinicalSummary() {
        return String.format("GENERAL | %s | Pain:%d/10 | Temp:%.1f°C | RR:%d/min",
                chiefComplaint, painScale, temperature, respiratoryRate);
    }

    public String getChiefComplaint() { return chiefComplaint; }
    public int    getPainScale()      { return painScale; }
    public double getTemperature()    { return temperature; }
    public int    getRespiratoryRate(){ return respiratoryRate; }
}
