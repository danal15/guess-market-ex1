package engine.api.exception;

public class InvalidMarketFileException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidMarketFileException(String message) {
        super(message);
    }

    public InvalidMarketFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
