package io.voitovich.yura.rideservice.dto.request

import java.util.*

data class UpdatePositionRequest(
    val rideId: UUID,
    val id: UUID,
    val location: RequestPoint
)
