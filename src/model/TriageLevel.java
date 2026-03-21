package model;

public enum TriageLevel {
    RESUSCITATION(1, "Immediate life threat"),
    EMERGENT     (2, "High risk situation"),
    URGENT       (3, "Stable, needs care"),
    LESS_URGENT  (4, "Minor condition"),
    NON_URGENT   (5, "Routine visit");

    private final int priority;
    private final String description;

    TriageLevel(int p, String d) { priority = p; description = d; }

    public int    getPriority()    { return priority; }
    public String getDescription() { return description; }
}
