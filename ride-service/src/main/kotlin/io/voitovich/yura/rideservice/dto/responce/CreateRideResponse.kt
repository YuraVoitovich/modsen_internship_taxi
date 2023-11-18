package io.voitovich.yura.rideservice.dto.responce

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "Response payload for creating a ride.")
data class CreateRideResponse(
    @Schema(description = "The UUID of the passenger.")
    val passengerId: UUID,

    @Schema(description = "The UUID of the created ride.")
    val rideId: UUID
)
