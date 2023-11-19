package io.voitovich.yura.rideservice.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import java.util.*

@Schema(description = "Request payload for creating a ride.")
data class CreateRideRequest(
    @field:NotNull
    @Schema(description = "The UUID of the passenger.")
    val passengerId: UUID,

    @field:NotNull
    @Schema(description = "The starting geographic location of the ride.")
    val startGeo: RequestPoint,

    @field:NotNull
    @Schema(description = "The ending geographic location of the ride.")
    val endGeo: RequestPoint
)