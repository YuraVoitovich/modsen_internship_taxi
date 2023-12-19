package io.voitovich.yura.rideservice.client.service.impl

import io.voitovich.yura.rideservice.client.PassengerServiceClient
import io.voitovich.yura.rideservice.client.model.PassengerProfileModel
import io.voitovich.yura.rideservice.client.service.PassengerClientService
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.util.*

@Service
class PassengerClientServiceImpl(
    val passengerServiceClient: PassengerServiceClient
) : PassengerClientService {

    val logger = KotlinLogging.logger {  }
    override fun getPassengerProfile(id: UUID): PassengerProfileModel {
        logger.info { "Execute getPassengerProfile method with id: ${id}" }
        return passengerServiceClient.getPassengerProfile(id).body!!
    }
}