package io.voitovich.yura.rideservice.dto.responce

import io.swagger.v3.oas.annotations.media.Schema
import io.voitovich.yura.rideservice.entity.RideStatus
import java.util.*

@Schema(description = "Response payload for updating the position of a ride participant.")
data class UpdatePositionResponse(
    @Schema(description = "The UUID of the ride.")
    val rideId: UUID,

    @Schema(description = "The updated position of the ride participant.")
    val userPosition: ResponsePoint?,

    @Schema(description = "The status of the ride.")
    val status: RideStatus
)