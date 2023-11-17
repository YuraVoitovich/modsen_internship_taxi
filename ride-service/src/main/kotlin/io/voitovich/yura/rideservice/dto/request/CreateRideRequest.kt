package io.voitovich.yura.rideservice.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.util.UUID

data class CreateRideRequest(
    @field:NotNull
    val passengerId: UUID,
    @field:NotNull
    val startGeo: RequestPoint,
    @field:NotNull
    val endGeo: RequestPoint,
)
