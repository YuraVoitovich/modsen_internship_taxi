package io.voitovich.yura.driverservice.exceptionhandler.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Builder
@Getter
public class ValidationExceptionInfo {

    private int code;

    @NonNull
    private HttpStatus status;

    @Singular
    Map<String, String> errors = new HashMap<>();
}
