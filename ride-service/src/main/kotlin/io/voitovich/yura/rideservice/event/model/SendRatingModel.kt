package io.voitovich.yura.rideservice.event.model

import java.math.BigDecimal
import java.util.UUID

data class SendRatingModel(
    val raterId: UUID,
    val ratedId: UUID,
    val rating: BigDecimal
)
