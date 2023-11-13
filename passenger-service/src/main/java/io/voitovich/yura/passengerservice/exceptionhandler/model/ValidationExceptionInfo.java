package io.voitovich.yura.passengerservice.exceptionhandler.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Builder
public record ValidationExceptionInfo (
    int code,
    @NonNull
    HttpStatus status,
    @Singular
    Map<String, String> errors
){}

