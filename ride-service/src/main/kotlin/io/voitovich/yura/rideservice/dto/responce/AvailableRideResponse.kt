package io.voitovich.yura.rideservice.dto.responce

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.util.*

@Schema(description = "Response payload for an available ride.")
data class AvailableRideResponse(
    @Schema(description = "The UUID of the ride.")
    val id: UUID,

    @Schema(description = "The UUID of the passenger profile.")
    val passengerProfileId: UUID,

    @Schema(description = "The starting geographic location of the ride.")
    val startGeo: ResponsePoint,

    @Schema(description = "The ending geographic location of the ride.")
    val endGeo: ResponsePoint,

    @Schema(description = "The distance between driver and starting location.")
    val distance: BigDecimal
)
