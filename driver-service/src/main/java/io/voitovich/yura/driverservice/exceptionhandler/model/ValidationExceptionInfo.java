package io.voitovich.yura.driverservice.exceptionhandler.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Builder
public record ValidationExceptionInfo (
    @NonNull
    HttpStatus status,
    @Singular
    Map<String, String> errors
) {}
