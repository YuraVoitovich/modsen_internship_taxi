package io.voitovich.yura.rideservice.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import io.voitovich.yura.rideservice.dto.request.AcceptRideRequest
import io.voitovich.yura.rideservice.dto.request.GetAvailableRidesRequest
import io.voitovich.yura.rideservice.dto.request.UpdatePositionRequest
import io.voitovich.yura.rideservice.dto.responce.GetAvailableRidesResponse
import io.voitovich.yura.rideservice.dto.responce.RideResponse
import io.voitovich.yura.rideservice.dto.responce.UpdatePositionResponse
import io.voitovich.yura.rideservice.exceptionhandler.model.ExceptionInfo
import io.voitovich.yura.rideservice.exceptionhandler.model.ValidationExceptionInfo
import io.voitovich.yura.rideservice.service.RideDriverManagementService
import io.voitovich.yura.rideservice.service.RideService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/ride/driver")
@Tag(name = "Driver ride controller", description = "Driver ride management operations")
class DriverRideController(val service: RideDriverManagementService) {

    @Operation(description = "Get a list of all created rides around the driver within a specified radius")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "List of available rides returned",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = GetAvailableRidesResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Bad request data",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ValidationExceptionInfo::class)
                    )
                ]
            ),
        ]
    )
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    fun getAvailableRides(@Valid @RequestBody getAvailableRidesRequest: GetAvailableRidesRequest) : GetAvailableRidesResponse {
        return service.getAvailableRides(getAvailableRidesRequest)
    }

    @PostMapping("/accept")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        description = "Accept a ride as a driver",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Ride accepted successfully",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = RideResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Bad request data",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = ValidationExceptionInfo::class))]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Ride already accepted",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = ExceptionInfo::class))]
            )
        ]
    )
    fun acceptRide(@Valid @RequestBody acceptRideRequest: AcceptRideRequest): RideResponse {
        return service.acceptRide(acceptRideRequest)
    }

    @PostMapping("/update-position")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        description = "Update the driver's position during a ride",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Driver's position updated successfully",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = UpdatePositionResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Bad request data",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = ValidationExceptionInfo::class))]
            )
        ]
    )
    fun updateDriverPosition(@Valid @RequestBody updatePositionRequest: UpdatePositionRequest): UpdatePositionResponse {
        return service.updateDriverPosition(updatePositionRequest)
    }

}