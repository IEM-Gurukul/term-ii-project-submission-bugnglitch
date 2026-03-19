package exception;

public class PatientNotFoundException extends Exception {
    public PatientNotFoundException(String id) { super("No patient found with ID: " + id); }
}
