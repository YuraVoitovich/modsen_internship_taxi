package io.voitovich.yura.rideservice.controller

import io.voitovich.yura.rideservice.dto.request.CancelRequest
import io.voitovich.yura.rideservice.dto.request.CreateRideRequest
import io.voitovich.yura.rideservice.dto.request.UpdatePositionRequest
import io.voitovich.yura.rideservice.dto.responce.CreateRideResponse
import io.voitovich.yura.rideservice.dto.responce.UpdatePositionResponse
import io.voitovich.yura.rideservice.service.RideService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/ride/passenger")
class PassengerRideController(val service: RideService) {
    @PutMapping()
    @ResponseStatus(HttpStatus.CREATED)
    fun createRide(@Valid @RequestBody request: CreateRideRequest): CreateRideResponse {
        return service.createRide(request);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    fun updatePassengerPosition(@Valid @RequestBody updatePositionRequest: UpdatePositionRequest) : UpdatePositionResponse {
        return service.updatePassengerPosition(updatePositionRequest)
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun cancelRide(@Valid @RequestBody cancelRequest: CancelRequest) {
        service.cancelRide(cancelRequest)
    }

}