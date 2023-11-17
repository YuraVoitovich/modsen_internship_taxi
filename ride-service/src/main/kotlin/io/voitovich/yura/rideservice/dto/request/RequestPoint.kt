package io.voitovich.yura.rideservice.dto.request

import jakarta.validation.constraints.NotEmpty
import java.math.BigDecimal

data class RequestPoint(
    @NotEmpty
    val latitude: BigDecimal,
    @NotEmpty
    val longitude: BigDecimal,
)
