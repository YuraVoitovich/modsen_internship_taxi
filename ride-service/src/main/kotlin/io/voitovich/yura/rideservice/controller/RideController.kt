package io.voitovich.yura.rideservice.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.voitovich.yura.rideservice.dto.request.RidePageRequest
import io.voitovich.yura.rideservice.dto.responce.RidePageResponse
import io.voitovich.yura.rideservice.dto.responce.RideResponse
import io.voitovich.yura.rideservice.exceptionhandler.model.ExceptionInfo
import io.voitovich.yura.rideservice.exceptionhandler.model.ValidationExceptionInfo
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

interface RideController {

    @Operation(description = "Get a paginated list of rides")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Page of rides returned",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = RidePageResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Bad request data",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = ValidationExceptionInfo::class))]
            )
        ]
    )
    fun getRidePage(@RequestParam(name = "pageNumber") pageNumber: Int,
                    @RequestParam(name = "pageSize") pageSize: Int,
                    @RequestParam(name = "orderBy") orderBy: String): RidePageResponse

    @Operation(description = "Delete a ride by its ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Ride deleted successfully"),
            ApiResponse(
                responseCode = "404",
                description = "Ride not found",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = ExceptionInfo::class))]
            )
        ]
    )
    fun deleteRideById(@PathVariable id: UUID)

    @Operation(description = "Get a ride by its ID")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Ride details returned",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = RideResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Ride not found",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = ExceptionInfo::class))]
            )
        ]
    )
    fun getRideById(@PathVariable id: UUID): RideResponse
}