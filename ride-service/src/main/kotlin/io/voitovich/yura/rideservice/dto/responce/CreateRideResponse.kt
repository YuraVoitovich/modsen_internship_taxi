package io.voitovich.yura.rideservice.dto.responce

import java.util.*

data class CreateRideResponse(
    val passengerId: UUID,
    val rideId: UUID
)
