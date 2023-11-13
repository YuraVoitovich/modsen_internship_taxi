package io.voitovich.yura.passengerservice.exceptionhandler.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;


@Builder
@Schema(description = "Exception info")
public record ExceptionInfo (

    @Schema(name = "code", description = "error status code", example = "400")
    int code,

    @Schema(name = "message", description = "error message", example = "Not found")
    @NonNull
    String message,
    @Schema(name = "status", description = "error status", example = "BAD_REQUEST")
    @NonNull
    HttpStatus status
) {}
