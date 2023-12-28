package io.voitovich.yura.rideservice.client.service

import io.voitovich.yura.rideservice.client.model.DriverProfileModel
import io.voitovich.yura.rideservice.client.model.DriverProfileModels
import java.util.*

interface DriverClientService {

    fun getDriverProfile(id: UUID): DriverProfileModel

    fun getDriverProfiles(ids: List<UUID>): List<DriverProfileModel>
}