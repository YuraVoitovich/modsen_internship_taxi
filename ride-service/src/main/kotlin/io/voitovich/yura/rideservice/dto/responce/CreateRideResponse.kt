package io.voitovich.yura.rideservice.dto.responce

import java.util.UUID

data class CreateRideResponse(
    val passengerId: UUID,
    val rideId: UUID
)
