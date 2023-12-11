package io.voitovich.yura.rideservice.event.model

import java.math.BigDecimal
import java.util.*

data class SendRatingModel(
    val raterId: UUID,
    val ratedId: UUID,
    val rideId: UUID,
    val rating: BigDecimal
)
