package io.voitovich.yura.rideservice.client.service

import io.voitovich.yura.rideservice.client.model.DriverProfileModel
import io.voitovich.yura.rideservice.client.model.DriverProfileModels
import io.voitovich.yura.rideservice.client.model.PassengerProfileModel
import io.voitovich.yura.rideservice.client.model.PassengerProfileModels
import java.util.*

interface PassengerClientService {
    fun getPassengerProfile(id: UUID): PassengerProfileModel

    fun getPassengerProfiles(ids: List<UUID>): List<PassengerProfileModel>
}