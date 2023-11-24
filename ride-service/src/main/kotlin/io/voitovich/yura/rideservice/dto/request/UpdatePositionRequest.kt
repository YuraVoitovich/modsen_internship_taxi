package io.voitovich.yura.rideservice.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "Request payload for updating the position of a ride participant.")
data class UpdatePositionRequest(
    @Schema(description = "The UUID of the ride.")
    val rideId: UUID,

    @Schema(description = "The UUID of the ride participant.")
    val id: UUID,

    @Schema(description = "The updated location of the ride participant.")
    val location: RequestPoint
)
