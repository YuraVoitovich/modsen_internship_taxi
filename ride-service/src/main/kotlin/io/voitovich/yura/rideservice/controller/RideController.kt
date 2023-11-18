package io.voitovich.yura.rideservice.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import io.voitovich.yura.rideservice.dto.request.RidePageRequest
import io.voitovich.yura.rideservice.dto.responce.RidePageResponse
import io.voitovich.yura.rideservice.dto.responce.RideResponse
import io.voitovich.yura.rideservice.exceptionhandler.model.ExceptionInfo
import io.voitovich.yura.rideservice.exceptionhandler.model.ValidationExceptionInfo
import io.voitovich.yura.rideservice.service.RideService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/ride")
@Tag(name = "Ride Controller", description = "General ride management operations")
class RideController(val service: RideService) {

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
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getRidePage(@Valid @RequestBody pageRequest: RidePageRequest): RidePageResponse {
        return service.getRidePage(pageRequest)
    }

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
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteRideById(@PathVariable id: UUID) {
        service.deleteRideById(id)
    }

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
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getRideById(@PathVariable id: UUID): RideResponse {
        return service.getRideById(id)
    }
}
