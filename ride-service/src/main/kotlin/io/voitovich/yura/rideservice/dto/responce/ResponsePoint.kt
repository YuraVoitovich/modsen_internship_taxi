package io.voitovich.yura.rideservice.dto.responce

import jakarta.validation.constraints.NotEmpty
import java.math.BigDecimal

data class ResponsePoint(
    val latitude: Double,
    val longitude: Double,
)
