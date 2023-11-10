package io.voitovich.yura.passengerservice.exception;

public class NoSuchRecordException extends RuntimeException {
    public NoSuchRecordException() {
        super();
    }

    public NoSuchRecordException(String message) {
        super(message);
    }

    public NoSuchRecordException(String message, Throwable cause) {
        super(message, cause);
    }
}
