package io.voitovich.yura.driverservice.exceptionhandler.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

@Builder
@Getter
public class ExceptionInfo {
    private int code;
    @NonNull
    private HttpStatus status;
    @NonNull
    private String message;

}
