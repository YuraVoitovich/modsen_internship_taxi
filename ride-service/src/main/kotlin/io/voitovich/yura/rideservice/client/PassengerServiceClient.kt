package io.voitovich.yura.rideservice.client

import io.voitovich.yura.rideservice.client.model.DriverProfileModel
import io.voitovich.yura.rideservice.client.model.PassengerProfileModel
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.*

@FeignClient(name = "passenger-service", path = "api/passenger/profile/")
interface PassengerServiceClient {
    @GetMapping("{id}")
    fun getPassengerProfile(@PathVariable(name = "id") id: UUID): ResponseEntity<PassengerProfileModel>
}