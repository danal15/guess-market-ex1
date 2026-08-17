package engine.api.exception;

public class StatePersistenceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public StatePersistenceException(String message) {
        super(message);
    }

    public StatePersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
