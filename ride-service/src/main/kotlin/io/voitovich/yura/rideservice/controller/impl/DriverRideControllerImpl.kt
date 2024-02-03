package io.voitovich.yura.rideservice.controller.impl

import io.swagger.v3.oas.annotations.tags.Tag
import io.voitovich.yura.rideservice.controller.DriverRideController
import io.voitovich.yura.rideservice.dto.request.*
import io.voitovich.yura.rideservice.dto.responce.GetAvailableRidesResponse
import io.voitovich.yura.rideservice.dto.responce.RidePageResponse
import io.voitovich.yura.rideservice.dto.responce.UpdatePositionResponse
import io.voitovich.yura.rideservice.service.RideDriverManagementService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/ride/driver")
@Tag(name = "Driver ride controller", description = "Driver ride management operations")
class DriverRideControllerImpl(val service: RideDriverManagementService) : DriverRideController {

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_modsen-user')")
    override fun getAvailableRides(@Valid @RequestBody getAvailableRidesRequest: GetAvailableRidesRequest) : GetAvailableRidesResponse {
        return service.getAvailableRides(getAvailableRidesRequest)
    }

    @PostMapping("/accept")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_modsen-user')")
    override fun acceptRide(@Valid @RequestBody acceptRideRequest: AcceptRideRequest) {
        service.acceptRide(acceptRideRequest)
    }

    @PostMapping("/update-position")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_modsen-user')")
    override fun updateDriverPosition(@Valid @RequestBody updatePositionRequest: UpdatePositionRequest): UpdatePositionResponse {
        return service.updateDriverPosition(updatePositionRequest)
    }


    @PostMapping("/confirm-start/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_modsen-user')")
    override fun confirmRideStart(@PathVariable id: UUID) {
        service.confirmRideStart(id)
    }

    @PostMapping("/confirm-end/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_modsen-user')")
    override fun confirmRideEnd(@PathVariable id: UUID) {
        service.confirmRideEnd(id)
    }

    @PostMapping("/rate")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_modsen-user')")
    override fun ratePassenger(@Valid @RequestBody request: SendRatingRequest) {
        service.ratePassenger(request)
    }

    @GetMapping("/rides/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_modsen-user')")
    override fun getAllDriverRides(@PathVariable(name = "id") id: UUID,
                                   @RequestParam(name = "pageNumber") pageNumber: Int,
                                   @RequestParam(name = "pageSize") pageSize: Int,
                                   @RequestParam(name = "orderBy") orderBy: String) : RidePageResponse {
        return service.getAllRides(id, RidePageRequest(pageNumber, pageSize, orderBy))
    }

}