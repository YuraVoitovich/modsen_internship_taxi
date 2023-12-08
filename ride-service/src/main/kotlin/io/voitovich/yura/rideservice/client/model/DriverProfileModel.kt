package io.voitovich.yura.rideservice.client.model

import java.math.BigDecimal
import java.util.*

data class DriverProfileModel(
    val id: UUID,
    val phoneNumber: String,
    val name: String,
    val surname: String,
    val rating: BigDecimal,
    val experience: Int,
)
