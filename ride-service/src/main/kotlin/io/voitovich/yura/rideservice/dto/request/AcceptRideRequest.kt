package io.voitovich.yura.rideservice.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "Request payload for accepting a ride.")
data class AcceptRideRequest(
    @Schema(description = "The UUID of the ride to be accepted.")
    val rideId: UUID,

    @Schema(description = "The UUID of the driver accepting the ride.")
    val driverId: UUID,

    @Schema(description = "The current location of the driver.")
    val location: RequestPoint
)
