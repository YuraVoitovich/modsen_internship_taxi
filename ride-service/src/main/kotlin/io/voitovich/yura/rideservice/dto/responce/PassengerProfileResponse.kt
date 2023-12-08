package io.voitovich.yura.rideservice.dto.responce

import java.math.BigDecimal
import java.util.*

data class PassengerProfileResponse(
    val name: String,
    val rating: BigDecimal
)
