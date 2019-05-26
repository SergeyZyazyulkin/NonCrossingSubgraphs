package by.zsp.ncst.exception;

public class MatroidException extends RuntimeException {

    public MatroidException() {
    }

    public MatroidException(String message) {
        super(message);
    }

    public MatroidException(String message, Throwable cause) {
        super(message, cause);
    }

    public MatroidException(Throwable cause) {
        super(cause);
    }
}
