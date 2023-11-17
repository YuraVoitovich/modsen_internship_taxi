package io.voitovich.yura.rideservice.dto.request

import java.util.UUID

data class UpdatePositionRequest(
    val ride_id: UUID,
    val id: UUID,
    val location: RequestPoint
)
