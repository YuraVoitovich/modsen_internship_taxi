package io.voitovich.yura.rideservice.dto.responce

import io.voitovich.yura.rideservice.entity.RideStatus
import java.util.*

data class UpdatePositionResponse(
    val rideId: UUID,
    val userPosition: ResponsePoint?,
    val status: RideStatus
)
