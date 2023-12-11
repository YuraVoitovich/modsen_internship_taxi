package io.voitovich.yura.rideservice.event.model

import java.math.BigDecimal
import java.util.UUID

data class ConfirmRatingReceiveModel(
    val rideId: UUID,
    val rating: BigDecimal,
)
