package io.voitovich.yura.rideservice.controller

import io.voitovich.yura.rideservice.dto.request.*
import io.voitovich.yura.rideservice.dto.responce.*
import io.voitovich.yura.rideservice.service.RideService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
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

    @PutMapping("/create")
    @ResponseStatus(HttpStatus.OK)
    fun createRide(@Valid @RequestBody request: CreateRideRequest): CreateRideResponse {
        return service.createRide(request);
    }

    @GetMapping("/driver/ride")
    @ResponseStatus(HttpStatus.OK)
    fun getAvailableRides(@Valid @RequestBody getAvailableRidesRequest: GetAvailableRidesRequest) : GetAvailableRidesResponse {
        return service.getAvailableRides(getAvailableRidesRequest)
    }

    @PostMapping("driver/ride")
    @ResponseStatus(HttpStatus.OK)
    fun acceptRide(@Valid @RequestBody acceptRideRequest: AcceptRideRequest) {
        service.acceptRide(acceptRideRequest)
    }

    @PostMapping("driver/position")
    @ResponseStatus(HttpStatus.OK)
    fun updateDriverPosition(@Valid @RequestBody updatePositionRequest: UpdatePositionRequest) : UpdatePositionResponse {
        return service.updateDriverPosition(updatePositionRequest)
    }

    @PostMapping("passenger/position")
    @ResponseStatus(HttpStatus.OK)
    fun updatePassengerPosition(@Valid @RequestBody updatePositionRequest: UpdatePositionRequest) : UpdatePositionResponse {
        return service.updatePassengerPosition(updatePositionRequest)
    }

}