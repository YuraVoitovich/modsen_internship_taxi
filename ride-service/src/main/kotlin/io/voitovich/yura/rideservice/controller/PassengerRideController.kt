package io.voitovich.yura.rideservice.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import io.voitovich.yura.rideservice.dto.request.CancelRequest
import io.voitovich.yura.rideservice.dto.request.CreateRideRequest
import io.voitovich.yura.rideservice.dto.request.UpdatePositionRequest
import io.voitovich.yura.rideservice.dto.responce.CreateRideResponse
import io.voitovich.yura.rideservice.dto.responce.UpdatePositionResponse
import io.voitovich.yura.rideservice.exceptionhandler.model.ExceptionInfo
import io.voitovich.yura.rideservice.exceptionhandler.model.ValidationExceptionInfo
import io.voitovich.yura.rideservice.service.RideService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/ride/passenger")
@Tag(name = "Passenger Ride Controller", description = "Passenger ride management operations")
class PassengerRideController(val service: RideService) {

    @Operation(description = "Create a new ride for a passenger")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Ride created successfully",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = CreateRideResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request data",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = ValidationExceptionInfo::class))]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Ride is already created",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = ExceptionInfo::class))]
            )
        ]
    )
    @PutMapping()
    @ResponseStatus(HttpStatus.CREATED)
    fun createRide(@Valid @RequestBody request: CreateRideRequest): CreateRideResponse {
        return service.createRide(request)
    }

    @Operation(description = "Update the passenger's position during a ride")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Passenger's position updated successfully",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = UpdatePositionResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Bad request data",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = ValidationExceptionInfo::class))]
            )
        ]
    )
    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    fun updatePassengerPosition(@Valid @RequestBody updatePositionRequest: UpdatePositionRequest): UpdatePositionResponse {
        return service.updatePassengerPosition(updatePositionRequest)
    }

    @Operation(description = "Cancel a ride for a passenger")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Ride canceled successfully",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Bad request data",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = ValidationExceptionInfo::class))]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Ride is already canceled",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = ExceptionInfo::class))]
            )
        ]
    )
    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun cancelRide(@Valid @RequestBody cancelRequest: CancelRequest) {
        service.cancelRide(cancelRequest)
    }
}
