package io.voitovich.yura.passengerservice.exceptionhandler.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Builder
@Schema(name = "ValidationExceptionInfo", description = "represents all validation exceptions")
public record ValidationExceptionInfo (
    @Schema(name = "status", description = "error status", example = "BAD_REQUEST")
    @NonNull
    HttpStatus status,
    @Schema(name = "errors", description = "validation errors", example = "name: name is mandatory")
    @Singular
    Map<String, String> errors
){}

