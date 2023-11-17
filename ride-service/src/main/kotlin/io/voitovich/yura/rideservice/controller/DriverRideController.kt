package io.voitovich.yura.rideservice.controller

import io.voitovich.yura.rideservice.dto.request.AcceptRideRequest
import io.voitovich.yura.rideservice.dto.request.GetAvailableRidesRequest
import io.voitovich.yura.rideservice.dto.request.UpdatePositionRequest
import io.voitovich.yura.rideservice.dto.responce.GetAvailableRidesResponse
import io.voitovich.yura.rideservice.dto.responce.UpdatePositionResponse
import io.voitovich.yura.rideservice.service.RideService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/driver")
class DriverRideController(val service: RideService) {

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    fun getAvailableRides(@Valid @RequestBody getAvailableRidesRequest: GetAvailableRidesRequest) : GetAvailableRidesResponse {
        return service.getAvailableRides(getAvailableRidesRequest)
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    fun acceptRide(@Valid @RequestBody acceptRideRequest: AcceptRideRequest) {
        service.acceptRide(acceptRideRequest)
    }

    @PostMapping("accept")
    @ResponseStatus(HttpStatus.OK)
    fun updateDriverPosition(@Valid @RequestBody updatePositionRequest: UpdatePositionRequest) : UpdatePositionResponse {
        return service.updateDriverPosition(updatePositionRequest)
    }

}