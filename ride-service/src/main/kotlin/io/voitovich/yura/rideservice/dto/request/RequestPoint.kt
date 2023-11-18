package io.voitovich.yura.rideservice.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotEmpty
import java.math.BigDecimal

@Schema(description = "Represents a geographic point in a request.")
data class RequestPoint(
    @field:NotEmpty
    @Schema(description = "The latitude of the point.")
    val latitude: BigDecimal,

    @field:NotEmpty
    @Schema(description = "The longitude of the point.")
    val longitude: BigDecimal
)