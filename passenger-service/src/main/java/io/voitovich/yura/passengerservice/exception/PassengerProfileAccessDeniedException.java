package io.voitovich.yura.passengerservice.exception;

public class PassengerProfileAccessDeniedException extends RuntimeException {
    public PassengerProfileAccessDeniedException() {
        super();
    }

    public PassengerProfileAccessDeniedException(String message) {
        super(message);
    }

    public PassengerProfileAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
