package io.voitovich.yura.rideservice.controller

import io.voitovich.yura.rideservice.dto.request.*
import io.voitovich.yura.rideservice.dto.responce.*
import io.voitovich.yura.rideservice.service.RideService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/ride")
class RideController(val service: RideService) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getRidePage(@Valid @RequestBody pageRequest: RidePageRequest) : RidePageResponse {
        return service.getRidePage(pageRequest)
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteRideById(@PathVariable id: UUID) {
        service.deleteRideById(id);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getRideById(@PathVariable id: UUID): RideResponse {
        return service.getRideById(id)
    }





}