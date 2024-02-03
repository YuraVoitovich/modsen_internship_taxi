package io.voitovich.yura.rideservice.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.voitovich.yura.rideservice.dto.request.*
import io.voitovich.yura.rideservice.dto.responce.CreateRideResponse
import io.voitovich.yura.rideservice.dto.responce.RidePageResponse
import io.voitovich.yura.rideservice.dto.responce.UpdatePositionResponse
import io.voitovich.yura.rideservice.exceptionhandler.model.ExceptionInfo
import io.voitovich.yura.rideservice.exceptionhandler.model.ValidationExceptionInfo
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

interface PassengerRideController {

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
    fun createRide(@Valid @RequestBody request: CreateRideRequest): CreateRideResponse

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
    fun updatePassengerPosition(@Valid @RequestBody updatePositionRequest: UpdatePositionRequest): UpdatePositionResponse

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
    fun cancelRide(@Valid @RequestBody cancelRequest: CancelRequest)

    fun rateDriver(@Valid @RequestBody request: SendRatingRequest)

    fun getAllPassengerRides(@PathVariable(name = "id") id: UUID,
                             @RequestParam(name = "pageNumber") pageNumber: Int,
                             @RequestParam(name = "pageSize") pageSize: Int,
                             @RequestParam(name = "orderBy") orderBy: String) : RidePageResponse

}