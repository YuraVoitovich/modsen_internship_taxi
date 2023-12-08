package io.voitovich.yura.rideservice.client.service

import io.voitovich.yura.rideservice.client.model.DriverProfileModel
import io.voitovich.yura.rideservice.client.model.PassengerProfileModel
import java.util.UUID

interface DriverClientService {

    fun getDriverProfile(id: UUID): DriverProfileModel
}