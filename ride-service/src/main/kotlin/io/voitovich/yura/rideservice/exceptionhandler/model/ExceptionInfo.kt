package io.voitovich.yura.rideservice.exceptionhandler.model

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.http.HttpStatus


@Schema(description = "Represents information about an exception.")
data class ExceptionInfo(
    @Schema(description = "The error status code.", example = "400")
    val code: Int,

    @Schema(description = "The error status.", example = "BAD_REQUEST")
    val status: HttpStatus,

    @Schema(description = "The error message.")
    val message: String
)
