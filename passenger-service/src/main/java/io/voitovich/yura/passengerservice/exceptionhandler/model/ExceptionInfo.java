package io.voitovich.yura.passengerservice.exceptionhandler.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;


@Builder
public record ExceptionInfo (
    int code,
    @NonNull
    String message,
    @NonNull
    HttpStatus status
) {}
