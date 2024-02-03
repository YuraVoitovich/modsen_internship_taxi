package io.voitovich.yura.rideservice.controller.impl

import io.swagger.v3.oas.annotations.tags.Tag
import io.voitovich.yura.rideservice.controller.RideController
import io.voitovich.yura.rideservice.dto.request.RidePageRequest
import io.voitovich.yura.rideservice.dto.responce.RidePageResponse
import io.voitovich.yura.rideservice.dto.responce.RideResponse
import io.voitovich.yura.rideservice.service.RideService
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/ride")
@Tag(name = "Ride Controller", description = "General ride management operations")
class RideControllerImpl(val service: RideService) : RideController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_modsen-admin')")
    override fun getRidePage(@RequestParam(name = "pageNumber") pageNumber: Int,
                             @RequestParam(name = "pageSize") pageSize: Int,
                             @RequestParam(name = "orderBy") orderBy: String): RidePageResponse {

        return service.getRidePage(RidePageRequest(
            pageNumber = pageNumber,
            pageSize = pageSize,
            orderBy = orderBy))
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_modsen-admin')")
    override fun deleteRideById(@PathVariable id: UUID) {
        service.deleteRideById(id)
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_modsen-user')")
    override fun getRideById(@PathVariable id: UUID): RideResponse {
        return service.getRideById(id)
    }
}
