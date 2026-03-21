package model;

public class CardiacPatient extends Patient {
    private final int     heartRate, systolicBP;
    private final boolean chestPain, stElevation;

    public CardiacPatient(String name, int age, String blood,
                          int hr, int bp, boolean chest, boolean stemi) {
        super(name, age, blood);
        heartRate = hr; systolicBP = bp; chestPain = chest; stElevation = stemi;
        setTriageLevel(assessTriageLevel());
    }

    @Override
    public TriageLevel assessTriageLevel() {
        if (stElevation || systolicBP < 80 || heartRate > 150)       return TriageLevel.RESUSCITATION;
        if (chestPain && (heartRate > 120 || systolicBP < 100))      return TriageLevel.EMERGENT;
        if (chestPain)                                                 return TriageLevel.URGENT;
        return TriageLevel.LESS_URGENT;
    }

    @Override
    public String getClinicalSummary() {
        return String.format("CARDIAC | HR:%d bpm | BP:%d mmHg | Chest:%s | STEMI:%s",
                heartRate, systolicBP, chestPain?"Yes":"No", stElevation?"YES":"No");
    }

    public int     getHeartRate()    { return heartRate; }
    public int     getSystolicBP()   { return systolicBP; }
    public boolean hasChestPain()    { return chestPain; }
    public boolean hasSTElevation()  { return stElevation; }
}
