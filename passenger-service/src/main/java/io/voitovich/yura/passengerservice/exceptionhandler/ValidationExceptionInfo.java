package io.voitovich.yura.passengerservice.exceptionhandler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ValidationExceptionInfo {
    private final int code;
    private final HttpStatus status;
    private final Map<String, String> errors =  new HashMap<>();

    public ValidationExceptionInfo(HttpStatus status) {
        this.code = status.value();
        this.status = status;
    }

    public void addError(String fieldName, String errorMessage) {
        errors.put(fieldName, errorMessage);
    }


}
