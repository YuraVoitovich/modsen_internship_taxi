package io.voitovich.yura.rideservice.client

import io.voitovich.yura.rideservice.client.config.DriverServiceClientConfig
import io.voitovich.yura.rideservice.client.model.DriverProfileModel
import io.voitovich.yura.rideservice.client.model.DriverProfileModels
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.*
import kotlin.collections.List


@FeignClient(configuration = [DriverServiceClientConfig::class], name = "driver-service", path = "api/driver")
interface DriverServiceClient {

    @GetMapping("/profile/{id}")
    fun getDriverProfile(@PathVariable(name = "id") id: UUID): ResponseEntity<DriverProfileModel>

    @GetMapping("/profiles/{ids}")
    fun getDriverProfiles(@PathVariable(name = "ids") ids: List<UUID>): ResponseEntity<DriverProfileModels>
}