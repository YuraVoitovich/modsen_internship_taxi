package io.voitovich.yura.rideservice.controller.impl

import io.swagger.v3.oas.annotations.tags.Tag
import io.voitovich.yura.rideservice.controller.PassengerRideController
import io.voitovich.yura.rideservice.dto.request.*
import io.voitovich.yura.rideservice.dto.responce.CreateRideResponse
import io.voitovich.yura.rideservice.dto.responce.RidePageResponse
import io.voitovich.yura.rideservice.dto.responce.UpdatePositionResponse
import io.voitovich.yura.rideservice.service.RidePassengerManagementService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/ride/passenger")
@Tag(name = "Passenger Ride Controller", description = "Passenger ride management operations")
class PassengerRideControllerImpl(val service: RidePassengerManagementService) : PassengerRideController {

    @PutMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_modsen-user')")
    override fun createRide(@Valid @RequestBody request: CreateRideRequest): CreateRideResponse {
        return service.createRide(request)
    }

    @PostMapping("/update-position")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_modsen-user')")
    override fun updatePassengerPosition(@Valid @RequestBody updatePositionRequest: UpdatePositionRequest): UpdatePositionResponse {
        return service.updatePassengerPosition(updatePositionRequest)
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_modsen-user')")
    override fun cancelRide(@Valid @RequestBody cancelRequest: CancelRequest) {
        service.cancelRide(cancelRequest)
    }

    @PostMapping("/rate")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_modsen-user')")
    override fun rateDriver(@Valid @RequestBody request: SendRatingRequest) {
        service.rateDriver(request)
    }

    @GetMapping("/rides/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_modsen-user')")
    override fun getAllPassengerRides(@PathVariable(name = "id") id: UUID,
                                      @RequestParam(name = "pageNumber") pageNumber: Int,
                                      @RequestParam(name = "pageSize") pageSize: Int,
                                      @RequestParam(name = "orderBy") orderBy: String) : RidePageResponse {
        return service.getAllRides(id, RidePageRequest(pageNumber, pageSize, orderBy))
    }
}
