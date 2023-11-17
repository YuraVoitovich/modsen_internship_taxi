package io.voitovich.yura.rideservice.dto.request

import java.util.UUID

data class CancelRequest(
    val passengerId: UUID,
    val rideId: UUID,
)
