package io.voitovich.yura.rideservice.exception;

public class KafkaSendingException extends RuntimeException{
    public KafkaSendingException() {
    }

    public KafkaSendingException(String message) {
        super(message);
    }

    public KafkaSendingException(String message, Throwable cause) {
        super(message, cause);
    }
}
