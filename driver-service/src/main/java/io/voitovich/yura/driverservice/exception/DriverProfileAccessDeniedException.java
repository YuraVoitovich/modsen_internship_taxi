package io.voitovich.yura.driverservice.exception;

public class DriverProfileAccessDeniedException extends RuntimeException {
    public DriverProfileAccessDeniedException() {
        super();
    }

    public DriverProfileAccessDeniedException(String message) {
        super(message);
    }

    public DriverProfileAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
