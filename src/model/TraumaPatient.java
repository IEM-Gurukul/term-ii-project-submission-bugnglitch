package model;

public class TraumaPatient extends Patient {
    private final String  injuryType;
    private final int     glasgowComaScale;
    private final boolean isConscious, hasInternalBleeding;

    public TraumaPatient(String name, int age, String blood,
                         String injury, int gcs, boolean conscious, boolean bleeding) {
        super(name, age, blood);
        injuryType = injury; glasgowComaScale = gcs;
        isConscious = conscious; hasInternalBleeding = bleeding;
        setTriageLevel(assessTriageLevel());
    }

    @Override
    public TriageLevel assessTriageLevel() {
        if (!isConscious || glasgowComaScale <= 8)          return TriageLevel.RESUSCITATION;
        if (hasInternalBleeding || glasgowComaScale <= 12)  return TriageLevel.EMERGENT;
        if (glasgowComaScale <= 14)                         return TriageLevel.URGENT;
        return TriageLevel.LESS_URGENT;
    }

    @Override
    public String getClinicalSummary() {
        return String.format("TRAUMA | %s | GCS:%d | Conscious:%s | Bleeding:%s",
                injuryType, glasgowComaScale, isConscious?"Yes":"No", hasInternalBleeding?"Yes":"No");
    }

    public String  getInjuryType()         { return injuryType; }
    public int     getGlasgowComaScale()   { return glasgowComaScale; }
    public boolean isConscious()           { return isConscious; }
    public boolean hasInternalBleeding()   { return hasInternalBleeding; }
}
