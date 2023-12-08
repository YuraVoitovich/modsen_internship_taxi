package io.voitovich.yura.rideservice.dto.responce

import java.math.BigDecimal
import java.util.*

data class DriverProfileResponse(
    val name: String,
    val rating: BigDecimal,
    val experience: Int,
)
