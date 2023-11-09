package io.voitovich.yura.passengerservice.exceptionhandler;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class ExceptionInfo {
    private final int code;
    private final String message;
    private final HttpStatus status;

    public ExceptionInfo(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
        this.code = status.value();
    }
}
