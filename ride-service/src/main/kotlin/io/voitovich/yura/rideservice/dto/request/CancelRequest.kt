package io.voitovich.yura.rideservice.dto.request

import java.util.*

data class CancelRequest(
    val passengerId: UUID,
    val rideId: UUID,
)
