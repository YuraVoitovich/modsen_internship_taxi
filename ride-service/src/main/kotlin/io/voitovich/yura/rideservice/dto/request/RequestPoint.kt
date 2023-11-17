package io.voitovich.yura.rideservice.dto.request

import jakarta.validation.constraints.NotEmpty
import java.math.BigDecimal

data class RequestPoint(
    @field:NotEmpty
    val latitude: BigDecimal,
    @field:NotEmpty
    val longitude: BigDecimal,
)
