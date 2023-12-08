package io.voitovich.yura.rideservice.client

import io.voitovich.yura.rideservice.client.model.DriverProfileModel
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.UUID


@FeignClient(name = "driver-service", path = "api/driver/profile/")
interface DriverServiceClient {

    @GetMapping("{id}")
    fun getDriverProfile(@PathVariable(name = "id") id: UUID): ResponseEntity<DriverProfileModel>
}