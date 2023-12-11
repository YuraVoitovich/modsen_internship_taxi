package io.voitovich.yura.rideservice.dto.responce

import java.math.BigDecimal

data class DriverProfileResponse(
    val name: String,
    val rating: BigDecimal,
    val experience: Int,
)
