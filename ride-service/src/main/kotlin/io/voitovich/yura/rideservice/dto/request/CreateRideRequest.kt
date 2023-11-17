package io.voitovich.yura.rideservice.dto.request

import jakarta.validation.constraints.NotEmpty
import java.math.BigDecimal
import java.util.UUID

data class CreateRideRequest(
    @NotEmpty
    val passengerId: UUID,
    @NotEmpty
    val startGeo: RequestPoint,
    @NotEmpty
    val endGeo: RequestPoint,
)
