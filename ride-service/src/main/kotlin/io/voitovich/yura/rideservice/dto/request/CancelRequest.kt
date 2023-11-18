package io.voitovich.yura.rideservice.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "Request payload for cancelling a ride.")
data class CancelRequest(
    @Schema(description = "The UUID of the passenger.")
    val passengerId: UUID,

    @Schema(description = "The UUID of the ride to be cancelled.")
    val rideId: UUID
)