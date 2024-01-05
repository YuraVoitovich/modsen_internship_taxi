package io.voitovich.yura.rideservice.client.service

import io.voitovich.yura.rideservice.client.model.PassengerProfileModel
import java.util.*

interface PassengerClientService {
    fun getPassengerProfile(id: UUID): PassengerProfileModel

    fun getPassengerProfiles(ids: List<UUID>): List<PassengerProfileModel>
}