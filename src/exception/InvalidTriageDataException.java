package exception;

public class InvalidTriageDataException extends RuntimeException {
    public InvalidTriageDataException(String field, String reason) {
        super("Invalid '" + field + "': " + reason);
    }
}
