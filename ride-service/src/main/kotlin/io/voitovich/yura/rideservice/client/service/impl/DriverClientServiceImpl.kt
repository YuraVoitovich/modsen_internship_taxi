package io.voitovich.yura.rideservice.client.service.impl

import io.voitovich.yura.rideservice.client.DriverServiceClient
import io.voitovich.yura.rideservice.client.model.DriverProfileModel
import io.voitovich.yura.rideservice.client.service.DriverClientService
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.util.*

@Service
class DriverClientServiceImpl(
    val driverServiceClient: DriverServiceClient
) : DriverClientService {

    val logger = KotlinLogging.logger { }

    override fun getDriverProfile(id: UUID): DriverProfileModel {
        logger.info { "Execute getDriverProfile method with id: ${id}" }
        return driverServiceClient.getDriverProfile(id).body!!
    }

    override fun getDriverProfiles(ids: List<UUID>): List<DriverProfileModel> {
        logger.info { "Execute getDriverProfiles method with ids: $ids" }
        return driverServiceClient.getDriverProfiles(ids).body!!.models
    }
}