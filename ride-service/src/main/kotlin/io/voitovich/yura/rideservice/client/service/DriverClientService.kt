package io.voitovich.yura.rideservice.client.service

import io.voitovich.yura.rideservice.client.model.DriverProfileModel
import java.util.*

interface DriverClientService {

    fun getDriverProfile(id: UUID): DriverProfileModel
}