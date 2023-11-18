package io.voitovich.yura.rideservice.dto.responce

import java.math.BigDecimal
import java.util.*

data class AvailableRideResponse(
    val id: UUID,
    val passengerProfileId: UUID,
    val startGeo: ResponsePoint,
    val endGeo: ResponsePoint,
    val distance: BigDecimal,
)
