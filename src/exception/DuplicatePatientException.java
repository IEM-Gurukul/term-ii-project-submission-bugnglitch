package exception;

public class DuplicatePatientException extends Exception {
    public DuplicatePatientException(String id) { super("Patient '" + id + "' already exists."); }
}
