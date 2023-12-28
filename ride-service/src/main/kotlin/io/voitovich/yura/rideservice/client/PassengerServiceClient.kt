package io.voitovich.yura.rideservice.client

import io.voitovich.yura.rideservice.client.config.PassengerServiceClientConfig
import io.voitovich.yura.rideservice.client.model.DriverProfileModels
import io.voitovich.yura.rideservice.client.model.PassengerProfileModel
import io.voitovich.yura.rideservice.client.model.PassengerProfileModels
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.*

@FeignClient(configuration = [PassengerServiceClientConfig::class], name = "passenger-service", path = "api/passenger")
interface PassengerServiceClient {
    @GetMapping("/profile/{id}")
    fun getPassengerProfile(@PathVariable(name = "id") id: UUID): ResponseEntity<PassengerProfileModel>

    @GetMapping("/profiles/{ids}")
    fun getPassengerProfiles(@PathVariable(name = "ids") ids: List<UUID>): ResponseEntity<PassengerProfileModels>
}