package io.voitovich.yura.passengerservice.exceptionhandler.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;


@Builder
public class ExceptionInfo {

    private final int code;
    @NonNull
    private final String message;
    @NonNull
    private final HttpStatus status;

    public ExceptionInfo(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
        this.code = status.value();
    }
}
