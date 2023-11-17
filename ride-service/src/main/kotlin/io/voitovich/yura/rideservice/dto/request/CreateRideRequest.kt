package io.voitovich.yura.rideservice.dto.request

import jakarta.validation.constraints.NotEmpty
import java.math.BigDecimal
import java.util.UUID

data class CreateRideRequest(
    @field:NotEmpty
    val passengerId: UUID,
    @field:NotEmpty
    val startGeo: RequestPoint,
    @field:NotEmpty
    val endGeo: RequestPoint,
)
