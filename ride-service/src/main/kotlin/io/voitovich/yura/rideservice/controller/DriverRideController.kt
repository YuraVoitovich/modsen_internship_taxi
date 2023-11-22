package io.voitovich.yura.rideservice.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import io.voitovich.yura.rideservice.dto.request.AcceptRideRequest
import io.voitovich.yura.rideservice.dto.request.GetAvailableRidesRequest
import io.voitovich.yura.rideservice.dto.request.SendRatingRequest
import io.voitovich.yura.rideservice.dto.request.UpdatePositionRequest
import io.voitovich.yura.rideservice.dto.responce.GetAvailableRidesResponse
import io.voitovich.yura.rideservice.dto.responce.RideResponse
import io.voitovich.yura.rideservice.dto.responce.UpdatePositionResponse
import io.voitovich.yura.rideservice.event.KafkaProducerService
import io.voitovich.yura.rideservice.event.model.SendRatingModel
import io.voitovich.yura.rideservice.exceptionhandler.model.ExceptionInfo
import io.voitovich.yura.rideservice.exceptionhandler.model.ValidationExceptionInfo
import io.voitovich.yura.rideservice.service.RideDriverManagementService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/ride/driver")
@Tag(name = "Driver ride controller", description = "Driver ride management operations")
class DriverRideController(val service: RideDriverManagementService, val kafkaProducerService: KafkaProducerService) {

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



    @Operation(
        description = "Confirm the start of a ride by the driver.",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "The ride start was successfully confirmed.",
            ),
            ApiResponse(
                responseCode = "404",
                description = "The specified ride was not found."
            ),
            ApiResponse(
                responseCode = "403",
                description = "Ride cannot be started as the driver is too far from the pickup location."
            )
        ]
    )
    @PostMapping("/confirm-start/{id}")
    fun confirmRideStart(@PathVariable id: UUID) {
        service.confirmRideStart(id)
    }

    @Operation(
        description = "Confirm the end of a ride by the driver.",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "The ride end was successfully confirmed.",
            ),
            ApiResponse(
                responseCode = "404",
                description = "The specified ride was not found."
            ),
            ApiResponse(
                responseCode = "403",
                description = "Ride cannot be started as the driver is too far from the ride-end location."
            )
        ]
    )
    @PostMapping("/confirm-end/{id}")
    fun confirmRideEnd(@PathVariable id: UUID) {
        service.confirmRideEnd(id)
    }

    @PostMapping("/rate")
    @ResponseStatus(HttpStatus.OK)
    fun ratePassenger(@Valid @RequestBody request: SendRatingRequest) {
        kafkaProducerService.ratePassenger(request)
    }

}