package io.voitovich.yura.rideservice.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import java.util.*

@Schema(description = "Request payload for retrieving available rides.")
data class GetAvailableRidesRequest(

    @field:NotNull
    @Schema(description = "The UUID of the driver.")
    val id: UUID,

    @field:NotNull
    @Schema(description = "The current location of the driver.")
    val currentLocation: RequestPoint,

    @Schema(description = "The optional radius within which to find available rides.", defaultValue = "500")
    val radius: Int?
)
