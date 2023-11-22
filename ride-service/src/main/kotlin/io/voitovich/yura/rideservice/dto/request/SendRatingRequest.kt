package io.voitovich.yura.rideservice.dto.request

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.util.UUID

data class SendRatingRequest(
    @field:NotNull
    val rideId: UUID,
    @field:Min(1)
    @field:Max(5)
    val rating: BigDecimal
)
