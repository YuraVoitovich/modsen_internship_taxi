package io.voitovich.yura.rideservice.dto.request

import java.util.*

data class AcceptRideRequest(
    val rideId : UUID,
    val driverId: UUID,
    val location: RequestPoint,
)
