package io.voitovich.yura.rideservice.exceptionhandler.model

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.http.HttpStatus


@Schema(name = "ValidationExceptionInfo", description = "Represents information about validation exceptions.")
data class ValidationExceptionInfo(

    @Schema(name = "status", description = "Error status", example = "BAD_REQUEST")
    val status: HttpStatus,

    @Schema(name = "errors", description = "Map of validation errors", example = "{'name': 'Name is mandatory'}")
    val errors: MutableMap<String, String?>
)