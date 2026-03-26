package util;

import exception.InvalidTriageDataException;

public final class InputValidator {
    private InputValidator() {}

    public static int    validateAge(int v)       { return check(v >= 0 && v <= 130,  "age",         "0–130. Got:" + v,       v); }
    public static int    validatePainScale(int v)  { return check(v >= 0 && v <= 10,   "painScale",   "0–10. Got:" + v,        v); }
    public static double validateTemperature(double v){ return checkD(v >= 30 && v <= 45, "temperature", "30–45°C. Got:" + v,  v); }
    public static int    validateHeartRate(int v)  { return check(v >= 0 && v <= 300,  "heartRate",   "0–300. Got:" + v,       v); }
    public static int    validateGCS(int v)        { return check(v >= 3 && v <= 15,   "GCS",         "3–15. Got:" + v,        v); }

    public static String validateBloodType(String b) {
        String u = b.toUpperCase().trim();
        if (!u.matches("(A|B|AB|O)[+-]"))
            throw new InvalidTriageDataException("bloodType", "Invalid: " + b);
        return u;
    }

    public static String validateNonEmpty(String v, String field) {
        if (v == null || v.isBlank())
            throw new InvalidTriageDataException(field, "Cannot be empty.");
        return v.trim();
    }

    private static int    check(boolean ok, String f, String msg, int v)    { if (!ok) throw new InvalidTriageDataException(f, msg); return v; }
    private static double checkD(boolean ok, String f, String msg, double v){ if (!ok) throw new InvalidTriageDataException(f, msg); return v; }
}
