package io.voitovich.yura.rideservice.dto.responce

import java.math.BigDecimal

data class PassengerProfileResponse(
    val name: String,
    val rating: BigDecimal
)
