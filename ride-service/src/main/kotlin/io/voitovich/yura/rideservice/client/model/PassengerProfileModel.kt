package io.voitovich.yura.rideservice.client.model

import java.math.BigDecimal
import java.util.UUID

data class PassengerProfileModel(
    val id: UUID,
    val phoneNumber: String,
    val name: String,
    val surname: String,
    val rating: BigDecimal
)
